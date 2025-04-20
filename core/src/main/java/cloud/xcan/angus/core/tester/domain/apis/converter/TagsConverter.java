package cloud.xcan.angus.core.tester.domain.apis.converter;

import static cloud.xcan.angus.core.tester.domain.apis.converter.ApiResponseConverter.OPENAPI_MAPPER;

import cloud.xcan.angus.spec.utils.ObjectUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.v3.oas.models.tags.Tag;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class TagsConverter implements AttributeConverter<List<Tag>, String> {

  @Override
  public String convertToDatabaseColumn(List<Tag> attribute) {
    try {
      return OPENAPI_MAPPER.writeValueAsString(attribute);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public List<Tag> convertToEntityAttribute(String dbData) {
    if (ObjectUtils.isEmpty(dbData)) {
      return null;
    }
    try {
      return OPENAPI_MAPPER.readValue(dbData, new TypeReference<List<Tag>>() {
      });
    } catch (Exception e) {
      return null;
    }
  }
}
