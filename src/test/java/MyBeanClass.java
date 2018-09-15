public class MyBeanClass implements MyBeanClassInterface {
    public MyDependency getMyDependency() {
        return myDependency;
    }

    @CreateOnTheFly
    private MyDependency myDependency;

    @Override
    public void test() {

    }
}
