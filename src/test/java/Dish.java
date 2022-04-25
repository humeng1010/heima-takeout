import lombok.Data;

import java.io.Serializable;

@Data
public class Dish{
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
