package Core2D.Core2D;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScriptProvider {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        String scriptPath1 = "Q:\\projects\\SungearEngine2D\\Core2D\\build\\classes\\java\\main\\Core2D\\Core2D\\TestScript1.class";
        String scriptPath2 = "Q:\\projects\\SungearEngine2D\\Core2D\\build\\classes\\java\\main\\Core2D\\Core2D\\TestScript2.class";
        CustomLoader customLoader = new CustomLoader();
        Class<TestInterface> testScript1Class = customLoader.loadNewClass(scriptPath1);
        Class<TestInterface> testScript2Class = customLoader.loadNewClass(scriptPath2);
        TestInterface testScript1 = testScript1Class.getConstructor().newInstance();
        TestInterface testScript2 = testScript2Class.getConstructor().newInstance();
        System.out.println(testScript1.getMsg());
        System.out.println(testScript2.getMsg());
        System.out.println("_________");
        customLoader = new CustomLoader();
        Class<TestInterface> newTestScript1Class = customLoader.loadNewClass(scriptPath1);
        TestInterface newTestScript1 = newTestScript1Class.getConstructor().newInstance();
        System.out.println(newTestScript1.getMsg());
        System.out.println(newTestScript1.getBroMsg());
    }

    public static class CustomLoader extends ClassLoader {

        public <T> Class<T> loadNewClass(String path) throws IOException, ClassNotFoundException {
            byte[] classBytes = Files.readAllBytes(Path.of(path));
            Class<?> clazz = this.defineClass(null, classBytes, 0, classBytes.length);
            Class<T> loadedClass = (Class<T>) this.loadClass(clazz.getName());
            return loadedClass;
        }
    }
}
