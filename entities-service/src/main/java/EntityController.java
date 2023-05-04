import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class EntityController {

    private final EntityRepository entityRepository;
    private final RedisSessionService redisSessionService;

    @GetMapping("/entities")
    public Flux<Entity> getAllEntities(ServerWebExchange exchange) {
        return getSessionUser(exchange)
                .flatMapMany(user -> entityRepository.findAllByUserId(user.getId()));
    }

    private Mono<User> getSessionUser(ServerWebExchange exchange) {
        String sessionId = exchange.getRequest().getCookies().getFirst("sessionId").getValue();
        return redisSessionService.getUserBySessionId(sessionId);
    }
}
