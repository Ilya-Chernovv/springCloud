import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    // поиск пользователя в базе по логину
    Mono<User> findByLogin(String login);
}