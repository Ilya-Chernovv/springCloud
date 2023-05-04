import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RedisSessionService redisSessionService;

    // обрабатывает post запросы на /login
    // проверяет наличие такого пользователя и возвращает  ошибку 401 если этого пользователя нет
    // аутентифицирует пользователя
    // создает сессию в redis с помощью RedisSessionService
    // устанавливает cookie с идентификатором сессии
    @PostMapping("/login")
    public Mono<Void> login(@RequestBody AuthRequest authRequest, ServerWebExchange exchange) {
        return userRepository.findByLogin(authRequest.getLogin())
                .filter(user -> user.getPassword().equals(authRequest.getPassword()))
                .flatMap(redisSessionService::createSession)
                .doOnNext(sessionId -> {
                    ResponseCookie cookie = ResponseCookie.from("sessionId", sessionId)
                            .path("/")
                            .maxAge(redisSessionService.SESSION_DURATION)
                            .httpOnly(true)
                            .build();
                    exchange.getResponse().addCookie(cookie);
                })
                .then();
    }

    // обрабатывает post запросы на /logout
    // удаляет сессию из redis
    // используя RedisSessionService и удаляет cookie сессии
    @PostMapping("/logout")
    public Mono<Void> logout(ServerWebExchange exchange) {
        String sessionId = exchange.getRequest().getCookies().getFirst("sessionId").getValue();
        return redisSessionService.deleteSession(sessionId)
                .doOnNext(removed -> {
                    ResponseCookie cookie = ResponseCookie.from("sessionId", "")
                            .path("/")
                            .maxAge(0)
                            .httpOnly(true)
                            .build();
                    exchange.getResponse().addCookie(cookie);
                })
                .then();
    }


    // извлекает идентификатор сессии из cookies и если сессия существует
    // получает пользователя из Redis используя RedisSessionService
    // если пользователь найден, метод возвращает информацию о пользователе в виде http ответа с кодом состояния 200
    // если идентификатор сессии не найден или сессия истекла, метод возвращает ответ с кодом состояния 401
    @GetMapping("/me")
    public Mono<ResponseEntity<User>> getCurrentUser(@CookieValue(name = "sessionId", required = false) String sessionId) {
        if (sessionId == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return redisSessionService.getUserBySessionId(sessionId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
