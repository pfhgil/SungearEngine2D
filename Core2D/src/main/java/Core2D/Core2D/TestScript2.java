package Core2D.Core2D;

public class TestScript2 implements TestInterface{

    public TestScript2() {
    }

    @Override
    public String getMsg() {
        return this.getClass().getSimpleName().concat("_kek2");
    }

    @Override
    public String getBroMsg() {
        return null;
    }
}
