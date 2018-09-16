public class MyClosableType implements AutoCloseable {
    public boolean isClose() {
        return isClose;
    }

    boolean isClose  = false;
    @Override
    public void close() throws Exception {
        isClose = true;
    }
}
