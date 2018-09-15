import com.sun.org.apache.xpath.internal.operations.Mod;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;

public class IoCContextImpl implements IoCContext {
    static HashSet<Class> instances = new HashSet<>();
    private boolean isClose = false;

    @Override
    public void registerBean(Class<?> beanClazz) {
        if (isClose) throw new IllegalStateException();

        if (beanClazz == null) {
            String message = "beanClazz is mandatory";
            throw new IllegalArgumentException(message);
        }

        String name = beanClazz.getName();

        if(Modifier.isAbstract(beanClazz.getModifiers())) {
            String message = name + " is abstract";
            throw new IllegalArgumentException(message);
        }
        try {
            beanClazz.getConstructor();
            this.instances.add(beanClazz.getClass());
        } catch (NoSuchMethodException e) {

            String message = name + " has no default constructor";
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public <T> void registerBean(Class<? super T> resolveClazz, Class<T> beanClazz) {

    }

    @Override
    public <T> T getBean(Class<T> resolveClazz) {
        if (!isClose) isClose = true;

        if (resolveClazz == null) throw new IllegalArgumentException();

        Class className = resolveClazz.getClass();

        if (!this.instances.contains(className)) throw new IllegalStateException();

        try {
            return resolveClazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
