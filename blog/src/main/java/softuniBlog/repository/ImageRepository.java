package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.Images;

public interface ImageRepository extends JpaRepository<Images, Integer> {
    Images findByTitle(String title);
}