package cloud.xcan.angus.core.tester.domain.apis.converter;

import static cloud.xcan.angus.core.tester.domain.apis.converter.ApiResponseConverter.OPENAPI_MAPPER;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.spec.utils.ObjectUtils;
import io.swagger.v3.oas.models.ExternalDocumentation;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ExternalDocConverter implements AttributeConverter<ExternalDocumentation, String> {

  @Override
  public String convertToDatabaseColumn(ExternalDocumentation attribute) {
    try {
      return OPENAPI_MAPPER.writeValueAsString(attribute);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public ExternalDocumentation convertToEntityAttribute(String dbData) {
    if (isEmpty(dbData)) {
      return null;
    }
    try {
      return OPENAPI_MAPPER.readValue(dbData, ExternalDocumentation.class);
    } catch (Exception e) {
      return null;
    }
  }
}
