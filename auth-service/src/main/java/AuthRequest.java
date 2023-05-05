import lombok.Data;

// хранит данные полученные от пользователя при попытке авторизации

@Data
public class AuthRequest {
    private String login;
    private String password;
}
