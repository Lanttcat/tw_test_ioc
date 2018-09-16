import java.util.ArrayList;
import java.util.List;

public class CloseError implements AutoCloseable {
    static List<String> stringArrayList = new ArrayList<>();

    private boolean isClosed = false;

    public boolean isClosed() {
        return isClosed;
    }

    public static List<String> getStringArrayList() {
        return stringArrayList;
    }

    @Override
    public void close() {
        stringArrayList.add(this.toString());
        throw new IllegalStateException();
    }

}
