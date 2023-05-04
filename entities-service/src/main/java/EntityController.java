import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
public class EntityController {

    private final EntityRepository entityRepository;
    private final RedisSessionService redisSessionService;


    // обрабатыват get запросы на получение всех записей текущего пользователя
    // вызывает getSessionUser для получения текущего пользователя
    // использует EntityRepository для получения всех записей, принадлежащих этому пользователю
    @GetMapping("/entities")
    public Flux<Entity> getAllEntities(ServerWebExchange exchange) {
        return getSessionUser(exchange)
                .flatMapMany(user -> entityRepository.findAllByUserId(user.getId()));
    }

    // получает идентификатор сессии из cookies
    // использует RedisSessionService для получения текущего пользователя по этому идентификатору
    private Mono<User> getSessionUser(ServerWebExchange exchange) {
        String sessionId = exchange.getRequest().getCookies().getFirst("sessionId").getValue();
        return redisSessionService.getUserBySessionId(sessionId);
    }
}
