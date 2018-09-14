public class ConstructorThrowsTest {
    public ConstructorThrowsTest() throws MyException {
        throw new MyException();
    }
}
