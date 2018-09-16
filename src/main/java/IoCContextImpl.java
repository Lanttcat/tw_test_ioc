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

        List<Field> fields = getFields(resolveClazz);

        addInstanceForExtend(instance, fields);
        this.beanContainer.putToInstance(resolveClazz, instance);
        return instance;
    }

    private <T> void addInstanceForExtend(T instance, List<Field> fields) {
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
    }

    private <T> List<Field> getFields(Class<? super T> resolveClazz) {
        Class superClass;
        List<Field> fields = new ArrayList<>();
        superClass = resolveClazz;
        while (superClass != Object.class) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }
        Collections.reverse(fields);
        return fields;
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

        List<Exception> exceptions = new ArrayList<>();

        reverse.forEach((key, value) -> {
            Type[] types = key.getGenericInterfaces();
            for (int index = 0; index < types.length; index++) {
                Type type = types[index];
                if (type.getTypeName() == "java.lang.AutoCloseable") {
                    runClose(reverse, exceptions, key);
                }
            }
        });
        if (exceptions.size() > 0) {
            throw exceptions.get(0);
        }
    }

    private void runClose(HashMap<Class, List<Object>> reverse, List<Exception> exceptions, Class key) {
        List<Object>  objects = reverse.get(key);
        for (int objIndex = objects.size() - 1; objIndex >= 0; objIndex--) {
            AutoCloseable closeable = (AutoCloseable)objects.get(objIndex);
            try {
                closeable.close();
            } catch (Exception e) {
                exceptions.add(e);
                e.getStackTrace();
            }
        }
    }
}
