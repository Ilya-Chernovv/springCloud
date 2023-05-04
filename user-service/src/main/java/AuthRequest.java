import lombok.Data;

// DTO для запросов авторизации

@Data
public class AuthRequest {
    private String login;
    private String password;
}
