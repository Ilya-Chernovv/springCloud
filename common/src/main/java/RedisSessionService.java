import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.UUID;

// сервис для работы с redis

@Service
@RequiredArgsConstructor
public class RedisSessionService {

    private final ReactiveRedisOperations<String, User> userOps;
    public  static final Duration SESSION_DURATION = Duration.ofHours(2);

    // создает сессию в redis для аутентифицированного пользователя
    // генерирует уникальный идентификатор сессии и сохраняет его в redis вместе с информацией о пользователе
    // период времени (SESSION_DURATION).
    public Mono<String> createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        return userOps.opsForValue()
                .set(sessionId, user, SESSION_DURATION)
                .thenReturn(sessionId);
    }

    // удаляет сессию из redis, используя идентификатор сессии
    public Mono<Void> deleteSession(String sessionId) {
        return userOps.delete(sessionId).then();
    }

    // получает пользователя, связанного с определенным идентификатором сессии из redis
    public Mono<User> getUserBySessionId(String sessionId) {
        return userOps.opsForValue().get(sessionId);
    }
}
