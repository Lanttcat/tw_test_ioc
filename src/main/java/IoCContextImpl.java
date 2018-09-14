import java.util.HashMap;

public class IoCContextImpl implements IoCContext {
    static HashMap<String, Object> instances = new HashMap<>();
    private boolean isClose = false;

    @Override
    public void registerBean(Class<?> beanClazz) {
        if (isClose) throw new IllegalStateException();
        if (beanClazz == null) {
            String message = "beanClazz is mandatory";
            throw new IllegalArgumentException(message);
        }
        try {
            if (!this.instances.containsKey(beanClazz.getName())) {
                this.instances.put(beanClazz.getName(), beanClazz.newInstance());
            };
        } catch (IllegalAccessException e) {
            String message = beanClazz.getName() + " has no default constructor";
            throw new IllegalArgumentException(message);
        } catch (InstantiationException e) {
            String message = beanClazz.getName() + " is abstract";
            throw new IllegalArgumentException(message);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public <T> T getBean(Class<T> resolveClazz) {
        if (!isClose) isClose = true;

        if (resolveClazz == null) throw new IllegalArgumentException();

        String name = resolveClazz.getName();

        if (!this.instances.containsKey(name)) throw new IllegalStateException();

        return (T)instances.get(name);
    }
}
