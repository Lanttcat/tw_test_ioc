import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IoCContextImplTest {
    @Test
    void should_create_instance_for_ioc() {
        IoCContext context = new IoCContextImpl();

        context.registerBean(MyBean.class);
        MyBean myBeanInstance = context.getBean(MyBean.class);

        assertEquals(MyBean.class, myBeanInstance.getClass());
    }
}
