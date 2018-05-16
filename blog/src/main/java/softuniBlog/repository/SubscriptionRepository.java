package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuniBlog.entity.Subscription;
import softuniBlog.entity.User;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
}
