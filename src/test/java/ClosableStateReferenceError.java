import java.util.ArrayList;
import java.util.List;

public class ClosableStateReferenceError implements AutoCloseable {
    static List<String> strings = new ArrayList<>();

    private boolean isClosed;

    public boolean isClosed() {
        return isClosed;
    }

    public static List<String> getStrings() {
        return strings;
    }

    public void close() {
        throw new IllegalStateException();
    }

}
