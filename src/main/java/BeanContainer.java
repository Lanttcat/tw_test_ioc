import java.util.HashMap;
import java.util.LinkedHashMap;

public class BeanContainer extends HashMap<Class, Class> {
    private HashMap<Class, Class> container = new LinkedHashMap<>();

    public HashMap<Class, Class> getContainer() {
        return container;
    }
}
