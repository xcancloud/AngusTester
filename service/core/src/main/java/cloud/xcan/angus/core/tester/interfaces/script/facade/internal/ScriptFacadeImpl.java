package cloud.xcan.angus.core.tester.interfaces.script.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.tester.interfaces.script.facade.internal.assembler.ScriptAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.tester.interfaces.script.facade.internal.assembler.ScriptAssembler.importDtoToDomain;
import static cloud.xcan.angus.core.tester.interfaces.script.facade.internal.assembler.ScriptAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.tester.interfaces.script.facade.internal.assembler.ScriptAssembler.toAngusDetailVo;
import static cloud.xcan.angus.core.tester.interfaces.script.facade.internal.assembler.ScriptAssembler.toDetailVo;
import static cloud.xcan.angus.core.tester.interfaces.script.facade.internal.assembler.ScriptAssembler.toInfoVo;
import static cloud.xcan.angus.core.tester.interfaces.script.facade.internal.assembler.ScriptAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.core.utils.ServletUtils.buildDownloadResourceResponseEntity;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

import cloud.xcan.angus.api.tester.script.ScriptDetailVo;
import cloud.xcan.angus.api.tester.script.dto.ScriptAddDto;
import cloud.xcan.angus.api.tester.script.dto.ScriptFindDto;
import cloud.xcan.angus.api.tester.script.vo.AngusScriptDetailVo;
import cloud.xcan.angus.api.tester.script.vo.ScriptInfoListVo;
import cloud.xcan.angus.api.tester.script.vo.ScriptInfoVo;
import cloud.xcan.angus.api.tester.script.vo.ScriptInfosVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.tester.application.cmd.script.ScriptCmd;
import cloud.xcan.angus.core.tester.application.query.script.ScriptQuery;
import cloud.xcan.angus.core.tester.domain.script.Script;
import cloud.xcan.angus.core.tester.domain.script.ScriptFormat;
import cloud.xcan.angus.core.tester.domain.script.ScriptInfo;
import cloud.xcan.angus.core.tester.interfaces.script.facade.ScriptFacade;
import cloud.xcan.angus.core.tester.interfaces.script.facade.dto.ScriptImportDto;
import cloud.xcan.angus.core.tester.interfaces.script.facade.dto.ScriptReplaceDto;
import cloud.xcan.angus.core.tester.interfaces.script.facade.dto.ScriptUpdateDto;
import cloud.xcan.angus.core.tester.interfaces.script.facade.internal.assembler.ScriptAssembler;
import cloud.xcan.angus.core.tester.interfaces.script.facade.vo.ScriptListVo;
import cloud.xcan.angus.model.script.AngusScript;
import cloud.xcan.angus.parser.AngusParser;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ScriptFacadeImpl implements ScriptFacade {

  @Resource
  private ScriptQuery scriptQuery;

  @Resource
  private ScriptCmd scriptCmd;

  @Override
  public IdKey<Long, Object> add(ScriptAddDto dto) {
    return scriptCmd.add(addDtoToDomain(dto), true);
  }

  @Override
  public void update(ScriptUpdateDto dto) {
    scriptCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public IdKey<Long, Object> replace(ScriptReplaceDto dto) {
    return scriptCmd.replace(replaceDtoToDomain(dto));
  }

  @Override
  public IdKey<Long, Object> angusAdd(Long projectId, AngusScript script) {
    return scriptCmd.addByAngus(projectId, script, true);
  }

  @Override
  public void angusReplace(Long id, AngusScript script) {
    scriptCmd.angusReplace(id, script, true);
  }

  @Override
  public IdKey<Long, Object> clone(Long id) {
    return scriptCmd.clone(id);
  }

  @Override
  public IdKey<Long, Object> imports(ScriptImportDto dto) {
    return scriptCmd.imports(importDtoToDomain(dto));
  }

  @Override
  public List<IdKey<Long, Object>> importExample(Long projectId) {
    return scriptCmd.importExample(projectId);
  }

  @Override
  public void delete(Collection<Long> ids) {
    scriptCmd.delete(ids);
  }

  @NameJoin
  @Override
  public ScriptDetailVo detail(Long id) {
    return toDetailVo(scriptQuery.detail(id));
  }

  @Override
  public ScriptInfoVo info(Long id) {
    return toInfoVo(scriptQuery.findById(id));
  }

  @Override
  public List<ScriptInfosVo> infos(Set<Long> ids) {
    return scriptQuery.infos(ids).stream().map(ScriptAssembler::toInfosVo)
        .toList();
  }

  @Override
  public AngusScriptDetailVo angusDetail(Long id) {
    return toAngusDetailVo(scriptQuery.angusDetail(id));
  }

  @NameJoin
  @Override
  public PageResult<ScriptListVo> list(ScriptFindDto dto) {
    GenericSpecification<ScriptInfo> spec = ScriptAssembler.getSpecification(dto);
    boolean queryAll = spec.getCriteria().isEmpty();
    Page<ScriptInfo> page = scriptQuery.list(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    if (page.isEmpty()) {
      PrincipalContext.addExtension("queryAllEmpty", queryAll);
      return PageResult.empty();
    }
    return buildVoPageResult(page, ScriptAssembler::toScriptListVo);
  }

  @Override
  public PageResult<ScriptInfoListVo> infoList(ScriptFindDto dto) {
    GenericSpecification<ScriptInfo> spec = ScriptAssembler.getSpecification(dto);
    boolean queryAll = spec.getCriteria().isEmpty();
    Page<ScriptInfo> page = scriptQuery.infoList(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    if (page.isEmpty()) {
      PrincipalContext.addExtension("queryAllEmpty", queryAll);
      return PageResult.empty();
    }
    return buildVoPageResult(page, ScriptAssembler::toScriptInfoListVo);
  }

  @SneakyThrows
  @Override
  public ResponseEntity<org.springframework.core.io.Resource> export(Long id,
      ScriptFormat format, HttpServletResponse response) {
    Script script = scriptQuery.detail(id);
    String content = format.isYaml() ? script.getContent()
        : AngusParser.JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(
            AngusParser.getInstance().readContents(script.getContent(), null).getScript());
    byte[] contentBytes = content.getBytes();
    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(contentBytes));
    return buildDownloadResourceResponseEntity(-1, APPLICATION_OCTET_STREAM,
        script.getName() + format.getFileSuffix(), contentBytes.length, resource);
  }
}
