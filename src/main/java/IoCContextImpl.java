import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IoCContextImpl implements IoCContext {
    static HashMap<Class, Class> classStorage = new HashMap<>();
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
            this.classStorage.put(beanClazz, null);
        } catch (NoSuchMethodException e) {
            String message = name + " has no default constructor";
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public <T> void registerBean(Class<? super T> resolveClazz, Class<T> beanClazz) {
        if (isClose) throw new IllegalStateException();
        isNullClass(resolveClazz == null || beanClazz == null);

        this.classStorage.put(resolveClazz, beanClazz);

    }

    @Override
    public <T> T getBean(Class<? super T> resolveClazz) {
        T instance;

        if (!isClose) isClose = true;

        if (resolveClazz == null) throw new IllegalArgumentException();

        if (!this.classStorage.containsKey(resolveClazz)) throw new IllegalStateException();
        try {
            if (isAnAbstractClass(resolveClazz)) {
                instance = (T)this.classStorage.get(resolveClazz).newInstance();
            } else {
                instance = (T)resolveClazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Field[] fields = resolveClazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(CreateOnTheFly.class) != null) {
                Class<?> classType = field.getType();
                if (!this.classStorage.containsKey(classType)) throw new IllegalStateException();
                try {
                    field.setAccessible(true);
                    field.set(instance, classType.newInstance());
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
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
