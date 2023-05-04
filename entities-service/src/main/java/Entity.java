import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("entities")
public class Entity {
    @Id
    private Long id;
    private Long userId;
    private String name;
}