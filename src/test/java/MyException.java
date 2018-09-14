public class MyException extends Exception {
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}
