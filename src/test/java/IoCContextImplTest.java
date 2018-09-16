import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class IoCContextImplTest {
    IoCContextImpl context = new IoCContextImpl();
    @Test
    void should_create_instance_for_ioc_AC1() {
        context.registerBean(MyBean.class);
        MyBean myBeanInstance = context.getBean(MyBean.class);

        assertEquals(MyBean.class, myBeanInstance.getClass());
    }

    @Test
    void should_get_two_time_after_AC1() {
        context.registerBean(MyBean.class);
        MyBean myBeanInstance = context.getBean(MyBean.class);
        MyBean myBeanInstance2 = context.getBean(MyBean.class);

        assertNotSame(myBeanInstance, myBeanInstance2);
    }

    @Test
    void should_create_instance_for_two() {
        context.registerBean(MyBean.class);
        context.registerBean(String.class);
        MyBean myBeanInstance = context.getBean(MyBean.class);
        String stringInstance = context.getBean(String.class);

        assertEquals(MyBean.class, myBeanInstance.getClass());
        assertEquals(String.class, stringInstance.getClass());

    }

    @Test
    void should_throw_when_bean_is_null_AC3() {
        try {
            context.registerBean(null);
        } catch (Exception e) {
            assertEquals("beanClazz is mandatory", e.getMessage());
        }
    }

    @Test
    void should_throw_bean_is_abstract_AC3() {
        try {
            context.registerBean(IoCContext.class);
        } catch (Exception e) {
            assertEquals("IoCContext is abstract", e.getMessage());
        }

    }

    @Test
    void should_pass_when_reg_same_AC5() {

        context.registerBean(MyBean.class);

        assertDoesNotThrow(() -> context.registerBean(MyBean.class));
    }

    @Test
    void should_throw_when_not_have_default_constructor_AC4() {
        try {
            context.registerBean(Array.class);
        } catch (Exception e) {
            assertEquals("java.lang.reflect.Array has no default constructor", e.getMessage());
        }
    }

    @Test
    void should_throw_when_resolveClazz_null_AC6() {
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
        try {
            context.registerBean(ConstructorThrowsTest.class);
        } catch (Exception e) {
            assertEquals(MyException.class, e.getClass());
        }

    }

    @Test
    void should_throw_when_close_set_AC8() {
        context.registerBean(MyBean.class);
        context.getBean(MyBean.class);
        try {
            context.registerBean(String.class);
        } catch (Exception e) {
            assertEquals(IllegalStateException.class, e.getClass());
        }
    }


    @Test
    void should_create_instance_by_interface() {

        context.registerBean(MyBeanClassInterface.class, MyBeanClass.class);

        MyBeanClass myBeanClassTest = context.getBean(MyBeanClassInterface.class);

        assertSame(MyBeanClass.class, myBeanClassTest.getClass());
    }

    @Test
    void should_use_after_class_when_have_repair_class() {

        MyBeanClassInterface myBeanAnotherClass = new MyBeanAnotherClass();

        context.registerBean(MyBeanClassInterface.class, MyBeanClass.class);
        context.registerBean(MyBeanClassInterface.class, MyBeanAnotherClass.class);

        MyBeanClassInterface myBeanClassTest = context.getBean(MyBeanClassInterface.class);

        assertSame(MyBeanAnotherClass.class, myBeanClassTest.getClass());
        assertNotSame(MyBeanClass.class, myBeanClassTest.getClass());

    }

    @Test
    void should_find_dependency_when_have_create_on_the_fly() {
        context.registerBean(MyBeanClass.class);
        context.registerBean(MyDependency.class);

        MyBeanClass myBeanClass = context.getBean(MyBeanClass.class);

        assertSame(MyDependency.class, myBeanClass.getMyDependency().getClass());
    }

    @Test
    void should_throw_when_have_annotation_but_not_register() {
        context.registerBean(MyBeanClass.class);

        try {
            MyBeanClass myBeanClass = context.getBean(MyBeanClass.class);
        } catch (Exception e) {
            assertSame(IllegalStateException.class, e.getClass());
        }

    }

    @Test
    void should_find_all_dependency_when_have_parent() {
        context.registerBean(MyBeanClassChild.class);
        context.registerBean(MyDependency.class);
        context.registerBean(MyDependencyChild.class);

        MyBeanClassChild myBeanClassChild = context.getBean(MyBeanClassChild.class);

        assertSame(MyDependency.class, myBeanClassChild.getMyDependency().getClass());
        assertSame(MyDependencyChild.class, myBeanClassChild.getMyDependencyChild().getClass());

    }

    @Test
    void should_throw_when_have_no_child_annotation_but_not_register() {
        context.registerBean(MyBeanClassChild.class);

        try {
            MyBeanClass myBeanClass = context.getBean(MyBeanClass.class);
        } catch (Exception e) {
            assertSame(IllegalStateException.class, e.getClass());
        }

    }

    @Test
    void should_throw_when_have_one_annotation_but_not_register() {
        context.registerBean(MyBeanClass.class);
        context.registerBean(MyDependency.class);

        try {
            MyBeanClass myBeanClass = context.getBean(MyBeanClass.class);
        } catch (Exception e) {
            assertSame(IllegalStateException.class, e.getClass());
        }

    }

    @Test
    void should_run_close_when_ioc_close() throws Exception {
        context.registerBean(MyClosableType.class);
        MyClosableType myClosableType = context.getBean(MyClosableType.class);

        context.close();

        assertEquals(true, myClosableType.isClose);
    }

    @Test
    void should_run_close_by_order_when_context_close() {
        context.registerBean(ClosableStateReference.class);

        ClosableStateReference closableStateReference1 = context.getBean(ClosableStateReference.class);
        ClosableStateReference closableStateReference2 = context.getBean(ClosableStateReference.class);

        try {
            context.close();
            assertIterableEquals(ClosableStateReference.getStrings(),
                    Arrays.asList(closableStateReference2.toString(), closableStateReference1.toString()));
        } catch (Exception e) {}
    }

    @Test
    void should_finish_when_some_close_have_error() {
        context.registerBean(ClosableStateReference.class);
        context.registerBean(CloseError.class);

        ClosableStateReference closableStateReference = context.getBean(ClosableStateReference.class);
        CloseError closeError = context.getBean(CloseError.class);
        ClosableStateReference closableStateReference1 = context.getBean(ClosableStateReference.class);

        try {
            context.close();
        } catch (Exception e) {}
        assertEquals(true, closableStateReference.isClosed());
        assertEquals(true, closableStateReference1.isClosed());
    }

    @Test
    void should_finish_and_throw_when_some_close_have_error() {
        context.registerBean(ClosableStateReference.class);
        context.registerBean(CloseError.class);

        ClosableStateReference closableStateReference = context.getBean(ClosableStateReference.class);
        CloseError closeError = context.getBean(CloseError.class);

        try {
            context.close();
        } catch (Exception e) {
            assertEquals(IllegalStateException.class, e.getClass());
        }
    }
}
