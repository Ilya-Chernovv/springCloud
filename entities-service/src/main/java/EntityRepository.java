import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EntityRepository extends ReactiveCrudRepository<Entity, Long> {

    Flux<Entity> findAllByUserId(Long userId);

}
