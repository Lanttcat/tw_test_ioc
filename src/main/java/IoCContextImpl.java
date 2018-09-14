import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class IoCContextImpl implements IoCContext {
    HashMap<String, Object> instances = new HashMap<>();
    @Override
    public void registerBean(Class<?> beanClazz) throws IllegalAccessException, InstantiationException {
        if (beanClazz == null) {
            String message = "beanClazz is mandatory";
            throw new IllegalArgumentException(message);
        }
        try {
            instances.put(beanClazz.getName(), beanClazz.newInstance());
        } catch (Exception e) {
            String message = beanClazz.getName() + " has no default constructor";
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public <T> T getBean(Class<T> resolveClazz) {
        return (T)instances.get(resolveClazz.getName());
    }
}
