public class MyBeanClass implements MyBeanClassInterface {
    @CreateOnTheFly
    private MyDependency myDependency;

    public MyDependency getMyDependency() {
        return myDependency;
    }

    @Override
    public void test() {

    }
}
