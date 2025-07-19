package cloud.xcan.angus.core.tester.application.query.data;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.tester.domain.data.dataset.Dataset;
import cloud.xcan.angus.model.element.dataset.DatasetParameter;
import cloud.xcan.angus.model.element.extraction.DefaultExtraction;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface DatasetQuery {

  Dataset detail(Long id);

  LinkedHashMap<String, List<String>> valuePreview(Long id, String name,
      List<DatasetParameter> parameters, DefaultExtraction extraction, Long rowNum);

  Map<String, String> valuePreview(List<Long> ids);

  Page<Dataset> list(GenericSpecification<Dataset> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  List<Dataset> findByProjectAndIds(Long projectId, LinkedHashSet<Long> ids);

  Dataset checkAndFind(Long id);

  List<Dataset> checkAndFind(Collection<Long> ids);

  List<Dataset> checkAndFindByName(Long projectId, List<String> names);

  void checkTenantQuota(int inc);

  void checkRequiredParam(Dataset dataset);

  void checkAddNameExists(Dataset dataset);

  void checkUpdateNameExists(Dataset dataset);

  void setSafeCloneName(Dataset dataset);

}
