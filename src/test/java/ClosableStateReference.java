public class ClosableStateReference implements AutoCloseable{
    private boolean isClosed;

    public boolean isClosed() {
        return isClosed;
    }

    public void close() {
        isClosed = true;
    }
}
