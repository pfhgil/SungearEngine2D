package sungear.test;

import sungear.script.ScriptProvider;

public class TestClass1 {

    public static void main(String[] args) {
        ScriptProvider scriptProvider = new ScriptProvider(null, null);
        for (int i = 0; i < 5; i++) {
            System.out.println(new TestClass2());
            System.out.println(scriptProvider);
        }
    }

}
