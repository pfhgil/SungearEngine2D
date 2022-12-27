package Core2D.Core2D;

public class TestScript1 implements TestInterface {

    public TestScript1() {
    }

    @Override
    public String getMsg() {
        return this.getClass().getSimpleName().concat("_kek12");
    }

    public String getBroMsg() {
        return new TestScript2().getMsg();
    }
}
