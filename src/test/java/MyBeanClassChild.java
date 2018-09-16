public class MyBeanClassChild extends MyBeanClass{
    @CreateOnTheFly
    private MyDependencyChild myDependencyChild;

    public MyDependencyChild getMyDependencyChild() {
        return myDependencyChild;
    }

    @Override
    public void test() {
    }
}
