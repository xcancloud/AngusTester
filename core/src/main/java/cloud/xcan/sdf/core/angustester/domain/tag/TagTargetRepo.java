package cloud.xcan.sdf.core.angustester.domain.tag;

import cloud.xcan.sdf.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TagTargetRepo extends BaseRepository<TagTarget, Long> {

  long countAllByTargetId(Long targetId);

  @Query(value = "SELECT tag_id FROM tag_target WHERE target_id = ?1", nativeQuery = true)
  Set<Long> findTagIdByTargetId(Long targetId);

  List<TagTarget> findAllByTargetIdIn(Collection<Long> targetIds);

  @Modifying
  @Query(value = "DELETE FROM tag_target WHERE tag_id in ?1", nativeQuery = true)
  void deleteByTagIdIn(Collection<Long> tagIds);

  @Modifying
  @Query(value = "DELETE FROM tag_target WHERE target_id in ?1", nativeQuery = true)
  void deleteByTargetIdIn(Collection<Long> targetIds);

}
