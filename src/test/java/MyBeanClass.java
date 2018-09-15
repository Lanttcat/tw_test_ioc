public class MyBeanClass implements MyBeanClassInterface {
    @CreateOnTheFly
    private MyDependency myDependency;
    @Override
    public void test() {

    }
}
