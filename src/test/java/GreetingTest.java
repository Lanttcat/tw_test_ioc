import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreetingTest {
    @Test
    void should_return_hello() {
        Greeting greeting = new Greeting();
        assertEquals("Hello world!", greeting.greet());
    }
}
