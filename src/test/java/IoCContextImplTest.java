import org.junit.jupiter.api.Test;

import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IoCContextImplTest {
    @Test
    void should_create_instance_for_ioc_AC1() {
        IoCContext context = new IoCContextImpl();

        context.registerBean(MyBean.class);
        MyBean myBeanInstance = context.getBean(MyBean.class);

        assertEquals(MyBean.class, myBeanInstance.getClass());
    }

    @Test
    void should_get_two_time_after_AC1() {
        IoCContext context = new IoCContextImpl();

        context.registerBean(MyBean.class);
        MyBean myBeanInstance = context.getBean(MyBean.class);
        MyBean myBeanInstance2 = context.getBean(MyBean.class);

        assertNotSame(myBeanInstance, myBeanInstance2);
    }

    @Test
    void should_create_instance_for_two() {
        IoCContext context = new IoCContextImpl();

        context.registerBean(MyBean.class);
        context.registerBean(String.class);
        MyBean myBeanInstance = context.getBean(MyBean.class);
        String stringInstance = context.getBean(String.class);

        assertEquals(MyBean.class, myBeanInstance.getClass());
        assertEquals(String.class, stringInstance.getClass());

    }

    @Test
    void should_throw_when_bean_is_null_AC3() {
        IoCContext context = new IoCContextImpl();
        try {
            context.registerBean(null);
        } catch (Exception e) {
            assertEquals("beanClazz is mandatory", e.getMessage());
        }
    }

    @Test
    void should_throw_bean_is_abstract_AC3() {
        IoCContext context = new IoCContextImpl();
        try {
            context.registerBean(IoCContext.class);
        } catch (Exception e) {
            assertEquals("IoCContext is abstract", e.getMessage());
        }

    }

    @Test
    void should_pass_when_reg_same_AC5() {
        IoCContextImpl cContext = new IoCContextImpl();

        cContext.registerBean(MyBean.class);

        assertDoesNotThrow(() -> cContext.registerBean(MyBean.class));
    }

    @Test
    void should_throw_when_not_have_default_constructor_AC4() {
        IoCContext context = new IoCContextImpl();
        try {
            context.registerBean(Array.class);
        } catch (Exception e) {
            assertEquals("java.lang.reflect.Array has no default constructor", e.getMessage());
        }
    }

    @Test
    void should_throw_when_resolveClazz_null_AC6() {
        IoCContextImpl context = new IoCContextImpl();

        context.registerBean(MyBean.class);
        Class expectedType = IllegalArgumentException.class;
        try {
            context.getBean(null);
        } catch (Exception e) {
            assertEquals(expectedType, e.getClass());
        }
    }

    @Test
    void should_throw_when_register_have_error_AC6() {
        IoCContextImpl context = new IoCContextImpl();

        context.registerBean(MyBean.class);
        Class expectedType = IllegalStateException.class;
        try {
            context.getBean(String.class);
        } catch (Exception e) {
            assertEquals(expectedType, e.getClass());
        }

    }

    @Test
    void should_throw_when_use_constructor_throw_AC7() {
        IoCContext context = new IoCContextImpl();
        try {
            context.registerBean(ConstructorThrowsTest.class);
        } catch (Exception e) {
            assertEquals(MyException.class, e.getClass());
        }

    }

    @Test
    void should_throw_when_close_set_AC8() {
        IoCContextImpl context = new IoCContextImpl();

        context.registerBean(MyBean.class);
        context.getBean(MyBean.class);
        try {
            context.registerBean(String.class);
        } catch (Exception e) {
            assertEquals(IllegalStateException.class, e.getClass());
        }
    }
}
