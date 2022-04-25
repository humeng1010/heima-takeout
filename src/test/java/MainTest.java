import org.junit.jupiter.api.Test;

public class MainTest {
    @Test
    public void test1(){
        DishDto dishDto = new DishDto();
        dishDto.setId(1);
        System.out.println(dishDto.getId());
    }
}
