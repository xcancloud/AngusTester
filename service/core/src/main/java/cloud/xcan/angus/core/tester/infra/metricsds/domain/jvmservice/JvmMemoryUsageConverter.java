package cloud.xcan.angus.core.tester.infra.metricsds.domain.jvmservice;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.core.tester.infra.metricsds.domain.jvmservice.JvmServiceUsage.JvmMemory;
import cloud.xcan.angus.spec.experimental.CsvConverter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JvmMemoryUsageConverter implements AttributeConverter<CsvConverter<JvmMemory>, String>{

  @Override
  public String convertToDatabaseColumn(CsvConverter usage) {
    return isNull(usage) ? null : usage.toString();
  }

  @Override
  public JvmMemory convertToEntityAttribute(String data) {
    return isNull(data) ? null : new JvmMemory().fromString(data);
  }

}
