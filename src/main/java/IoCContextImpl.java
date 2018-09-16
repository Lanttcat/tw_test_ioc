import sun.rmi.rmic.iiop.InterfaceType;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class IoCContextImpl implements IoCContext {
    private static BeanContainer beanContainer = new BeanContainer();
    private static HashMap<Class, Class> classStorage = beanContainer.getContainer();
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
        Class superClass;

        if (!isClose) isClose = true;

        if (resolveClazz == null) throw new IllegalArgumentException();

        if (!this.classStorage.containsKey(resolveClazz)) throw new IllegalStateException();

        try {
            if (isAnAbstractClass(resolveClazz)) {
                resolveClazz = classStorage.get(resolveClazz);
                instance = (T) resolveClazz.newInstance();
            } else {
                instance = (T)resolveClazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        List<Field> fields = new ArrayList<>();
        superClass = resolveClazz;
        while (superClass != Object.class) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }
        Collections.reverse(fields);

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
        this.beanContainer.putToInstance(resolveClazz, instance);
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

    @Override
    public void close() throws Exception {
        HashMap<Class, List<Object>> reverse = this.beanContainer.reverseInstance();

        reverse.forEach((key, value) -> {
            Type[] types = key.getInterfaces();
            for (int index = 0; index < types.length; index++) {
                Type type = types[index];
                if (type.getTypeName() == "java.lang.AutoCloseable") {
                    try {
                        List<Object>  objects = reverse.get(key);
                        for (Object object : objects) {
                            AutoCloseable closeable = (AutoCloseable)object;
                            closeable.close();
                        }
                        Method closeMethod = key.getMethod("close");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
