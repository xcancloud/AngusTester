package cloud.xcan.angus.core.tester.application.cmd.mock.impl;


import static cloud.xcan.angus.core.tester.application.converter.MockDataConverter.toAngusScript;
import static cloud.xcan.angus.remote.message.ProtocolException.M.EXPRESSION_PARSING_ERROR_T;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.tester.application.cmd.exec.ExecCmd;
import cloud.xcan.angus.core.tester.application.cmd.mock.MockDataCmd;
import cloud.xcan.angus.core.tester.application.cmd.script.ScriptCmd;
import cloud.xcan.angus.core.tester.domain.mock.data.MockFuncData;
import cloud.xcan.angus.core.tester.domain.mock.data.MockFuncRequest;
import cloud.xcan.angus.core.tester.domain.mock.data.MockTextData;
import cloud.xcan.angus.core.tester.domain.mock.data.MockTextRequest;
import cloud.xcan.angus.core.tester.domain.script.Script;
import cloud.xcan.angus.model.element.mock.data.MockData;
import cloud.xcan.angus.model.script.AngusScript;
import cloud.xcan.angus.model.script.configuration.Configuration;
import cloud.xcan.angus.parser.AngusParser;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.jmock.core.parser.replacer.DefaultMockExpressionReplacer;
import cloud.xcan.jmock.core.parser.replacer.DefaultMockTextReplacer;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Command implementation for mock data generation.
 * <p>
 * Provides methods for generating mock function and text data, and managing mock data scripts.
 * <p>
 * Ensures batch operations, error handling, and integration with script and execution commands.
 */
@Biz
public class MockDataCmdImpl implements MockDataCmd {

  @Resource
  private DefaultMockTextReplacer defaultMockTextReplacer;
  @Resource
  private DefaultMockExpressionReplacer defaultMockExpressionReplacer;
  @Resource
  private ScriptCmd scriptCmd;
  @Resource
  private ExecCmd execCmd;

  /**
   * Generate mock function data for a given function and iteration count.
   * <p>
   * Returns a list of generated values.
   */
  @Override
  public List<Object> mockFunc(String function, int iterations) {
    return new BizTemplate<List<Object>>() {

      @Override
      protected List<Object> process() {
        return mockFunc0(iterations, function);
      }
    }.execute();
  }

  /**
   * Generate mock function data in batch for multiple functions.
   * <p>
   * Returns a list of mock function data objects.
   */
  @Override
  public List<MockFuncData> mockFuncInBatch(List<MockFuncRequest> functions) {
    return new BizTemplate<List<MockFuncData>>() {

      @Override
      protected List<MockFuncData> process() {
        List<MockFuncData> funcDatas = new ArrayList<>();
        for (MockFuncRequest func : functions) {
          funcDatas.add(new MockFuncData().setOutKey(func.getOutKey())
              .setValues(mockFunc0(func.getIterations(), func.getFunction())));
        }
        return funcDatas;
      }
    }.execute();
  }

  /**
   * Generate mock text data for a given text and iteration count.
   * <p>
   * Returns a list of generated text values.
   */
  @Override
  public List<String> mockText(String text, int iterations) {
    return new BizTemplate<List<String>>() {

      @Override
      protected List<String> process() {
        return mockText0(iterations, text);
      }
    }.execute();
  }

  /**
   * Generate mock text data in batch for multiple texts.
   * <p>
   * Returns a list of mock text data objects.
   */
  @Override
  public List<MockTextData> mockTextInBatch(List<MockTextRequest> texts) {
    return new BizTemplate<List<MockTextData>>() {

      @Override
      protected List<MockTextData> process() {
        List<MockTextData> result = new ArrayList<>();
        for (MockTextRequest text : texts) {
          result.add(new MockTextData().setOutKey(text.getOutKey())
              .setValues(mockText0(text.getIterations(), text.getText())));
        }
        return result;
      }
    }.execute();
  }

  /**
   * Add or update a mock data script.
   * <p>
   * Generates or updates a script for mock data and returns its ID.
   */
  @Override
  public IdKey<Long, Object> dataScriptAdd(Long scriptId, Long projectId, String plugin,
      Configuration configuration, MockData mockData) {
    return new BizTemplate<IdKey<Long, Object>>() {

      @Override
      protected IdKey<Long, Object> process() {
        // TODO 强制设置执行节点数为1; 最大支持1000个线程，不支持rampUp/Down
        // TODO 将生产行数设置成迭代数
        // TODO 保存修改脚本时也添加上面校验

        // Generate script
        if (isNull(scriptId)) {
          AngusScript angusScript = toAngusScript(plugin, mockData, configuration);
          return scriptCmd.addByAngus(projectId, angusScript, true);
        }

        // Update script
        AngusScript angusScript = toAngusScript(plugin, mockData, configuration);
        scriptCmd.update(new Script().setId(scriptId), angusScript, true);
        return new IdKey<>(scriptId, null);
      }
    }.execute();
  }

  /**
   * View the YAML representation of a mock data script.
   * <p>
   * Returns the YAML string for the generated script.
   */
  @Override
  public String dataScriptView(String plugin, Configuration configuration, MockData mockData) {
    return new BizTemplate<String>() {

      @Override
      protected String process() {
        AngusScript angusScript = toAngusScript(plugin, mockData, configuration);
        try {
          return AngusParser.YAML_MAPPER.writeValueAsString(angusScript);
        } catch (Exception e) {
          throw ProtocolException.of("Script format error, reason: " + getRootCauseMessage(e));
        }
      }
    }.execute();
  }

  /**
   * Generate data by executing a mock data script.
   * <p>
   * Adds or updates the script, then triggers execution and returns the execution result ID.
   */
  @Override
  public IdKey<Long, Object> dataGen(Long scriptId, Long projectId, String plugin,
      Configuration configuration, MockData mockData) {
    return new BizTemplate<IdKey<Long, Object>>() {

      @Override
      protected IdKey<Long, Object> process() {
        Long scriptId0 = dataScriptAdd(scriptId, projectId, plugin, configuration, mockData).getId();

        // Create execution
        return execCmd.addByRemoteScript(null, scriptId0, null, null, null, null);
      }
    }.execute();
  }

  /**
   * Internal: Generate mock function data for a given function and iteration count.
   * <p>
   * Handles exceptions and returns a list of generated values.
   */
  @NotNull
  public List<Object> mockFunc0(int iterations, String mockFunc) {
    try {
      List<Object> result = new ArrayList<>(iterations);
      for (int i = 0; i < iterations; i++) {
        result.add(defaultMockExpressionReplacer.replace(mockFunc));
      }
      return result;
    } catch (Exception e) {
      throw ProtocolException.of(EXPRESSION_PARSING_ERROR_T, new Object[]{mockFunc});
    }
  }

  /**
   * Internal: Generate mock text data for a given text and iteration count.
   * <p>
   * Handles exceptions and returns a list of generated text values.
   */
  @NotNull
  private List<String> mockText0(int iterations, String text) {
    try {
      List<String> result = new ArrayList<>(iterations);
      for (int i = 0; i < iterations; i++) {
        result.add(defaultMockTextReplacer.replace(text));
      }
      return result;
    } catch (Exception e) {
      throw ProtocolException.of(EXPRESSION_PARSING_ERROR_T, new Object[]{text});
    }
  }

}
