package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.Hashtags;


public interface HashTagRepository extends JpaRepository<Hashtags, Integer> {
    Hashtags findByHashTag(String hashTag);
}
