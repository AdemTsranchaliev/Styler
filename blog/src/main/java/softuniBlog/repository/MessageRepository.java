package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.Images;
import softuniBlog.entity.Messages;

public interface MessageRepository extends JpaRepository<Messages, Integer> {
}