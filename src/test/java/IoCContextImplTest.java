import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IoCContextImplTest {
    @Test
    void should_create_instance_for_ioc() throws InstantiationException, IllegalAccessException {
        IoCContext context = new IoCContextImpl();

        context.registerBean(MyBean.class);
        MyBean myBeanInstance = context.getBean(MyBean.class);

        assertEquals(MyBean.class, myBeanInstance.getClass());
    }

    @Test
    void should_throw_when_bean_is_null() {
        IoCContext context = new IoCContextImpl();
        try {
            context.registerBean(null);
        } catch (Exception e) {
            assertEquals("beanClazz is mandatory", e.getMessage());
        }
        try {
            context.registerBean(IoCContext.class);
        } catch (Exception e) {
            assertEquals("IoCContext has no default constructor", e.getMessage());
        }
    }
}
