import com.sun.org.apache.xpath.internal.operations.Mod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;

public class IoCContextImpl implements IoCContext {
    static HashMap<Class, Class> instances = new HashMap<>();
    private boolean isClose = false;

    @Override
    public void registerBean(Class<?> beanClazz) {
        if (isClose) throw new IllegalStateException();

        isNullClass(beanClazz == null);

        String name = beanClazz.getName();

        if(isAnAbstractClass(beanClazz)) {
            String message = name + " is abstract";
            throw new IllegalArgumentException(message);
        }
        try {
            beanClazz.getConstructor();
            this.instances.put(beanClazz, null);
        } catch (NoSuchMethodException e) {
            String message = name + " has no default constructor";
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public <T> void registerBean(Class<? super T> resolveClazz, Class<T> beanClazz) {
        if (isClose) throw new IllegalStateException();
        isNullClass(resolveClazz == null || beanClazz == null);

        this.instances.put(resolveClazz, beanClazz);

    }

    @Override
    public <T> T getBean(Class<? super T> resolveClazz) {
        if (!isClose) isClose = true;

        if (resolveClazz == null) throw new IllegalArgumentException();

        if (!this.instances.containsKey(resolveClazz)) throw new IllegalStateException();


        try {
            if (isAnAbstractClass(resolveClazz)) {
                return (T)this.instances.get(resolveClazz).newInstance();
            } else {
                return (T)resolveClazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void isNullClass(boolean b) {
        if (b) {
            String message = "beanClazz is mandatory";
            throw new IllegalArgumentException(message);
        }
    }

    private boolean isAnAbstractClass(Class<?> beanClazz) {
        return Modifier.isAbstract(beanClazz.getModifiers());
    }
}
