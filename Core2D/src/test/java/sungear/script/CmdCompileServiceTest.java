package sungear.script;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sungear.exception.SungearEngineException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CmdCompileServiceTest {
    private static String buildDirPath;
    private static String srcDirPath;
    private static CmdCompileService compileService;

    @BeforeAll
    public static void beforeAll() {
        buildDirPath = System.getProperty("user.dir").concat("/build/test");
        srcDirPath = getResourceDir(CmdCompileServiceTest.class).getAbsolutePath();
        String libs = srcDirPath.concat("/lib");
        String tmp = buildDirPath.concat("/tmp");
        String out = buildDirPath.concat("/out");
        compileService = new CmdCompileService(20, tmp, out, libs);
    }

    @AfterAll
    public static void afterAll() {
        recursiveDeletion(compileService.getCompiledClassFilesDir());
    }

    @Test
    public void compileTest() throws SungearEngineException, IOException, InterruptedException {
        String src = srcDirPath.concat("/src");
        File srcFile = new File(src);
        srcFile.mkdirs();
        compileService.moveJavaFilesToTmpDir(src);
        File tmpFile = new File(compileService.getTmpJavaFilesDirPath());
        assertTrue(tmpFile.isDirectory( ));
        assertFalse(Arrays.stream(tmpFile.list()).filter(f -> f.endsWith(".java")).toList().isEmpty(), tmpFile.getAbsolutePath());
        assertTrue(dirIsEmpty(compileService.getCompiledClassFilesDir()));
        compileService.compile("TestClass1.java");
        assertFalse(dirIsEmpty(compileService.getCompiledClassFilesDir()));
        recursiveDeletion(tmpFile);
    }

    private static void recursiveDeletion(String path) {
        recursiveDeletion(new File(path));
    }

    private boolean dirIsEmpty(String path) {
        File dir = new File(path);
        return !dir.exists() || (dir.isDirectory() && dir.list().length == 0);
    }

    private static void recursiveDeletion(File file) {
        Arrays.stream(file.listFiles()).forEach(f -> {
            if (f.isFile() || (f.isDirectory() && f.list().length == 0)) {
                assertTrue(f.delete(), f.getAbsolutePath());
            } else if (f.isDirectory()) {
                recursiveDeletion(f);
            }
        });
        assertTrue(file.delete(), file.getAbsolutePath());
    }

    private static File getResourceDir(Class<?> clazz) {
        return new File("/" + clazz.getResource(clazz.getSimpleName()).getFile());
    }
}
