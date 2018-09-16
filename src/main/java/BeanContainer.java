import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;

public class BeanContainer extends HashMap<Class, Class> {
    private HashMap<Class, Class> container = new LinkedHashMap<>();
    private HashMap<Class, List<Object>> instances = new LinkedHashMap<>();

    public void putToInstance(Class key, Object obj) {
        if (instances.containsKey(key)) {
            List<Object> list = instances.get(key);
            list.add(obj);
            instances.put(key, list);
        } else {
            List<Object> list = new ArrayList<>();
            list.add(obj);
            instances.put(key, list);
        }
    }

    public void putToContainer(Class key, Object value) {

    }

    public HashMap<Class, List<Object>> reverseInstance() {
        HashMap<Class, List<Object>> reverseLinked = new LinkedHashMap<>();

        Set<Class> sets = instances.keySet();
        List<Object> keys = Arrays.asList(sets.toArray());
        Collections.reverse(keys);

        for (int index = keys.size() - 1; index >= 0; index--) {
            Class key = (Class) keys.get(index);
            reverseLinked.put(key, instances.get(key));

        }
        return  reverseLinked;
    }

    public HashMap<Class, Class> getContainer() {
        return container;
    }
}
