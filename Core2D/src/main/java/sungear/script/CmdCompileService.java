package sungear.script;

import sungear.exception.SungearEngineError;
import sungear.exception.SungearEngineException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CmdCompileService implements CompileService {
    private final long compilerExecutionTimeoutSec;
    private final String tmpJavaFilesDirPath;
    private final String compiledClassFilesDir;
    private final String libsDirPath;

    public CmdCompileService(long compilerExecutionTimeoutSec, String tmpJavaFilesDirPath, String compiledClassFilesDir, String libsDirPath) {
        this.compilerExecutionTimeoutSec = compilerExecutionTimeoutSec;
        this.tmpJavaFilesDirPath = validatePathSeparator(tmpJavaFilesDirPath);
        new File(tmpJavaFilesDirPath).mkdirs();
        this.compiledClassFilesDir = validatePathSeparator(compiledClassFilesDir);
        this.libsDirPath = validatePathSeparator(libsDirPath);
    }

    // TODO: use sourcepath only with scripts are imported
    // TODO: investigate compilation strategy - when and how many scripts are needed to be compiled
    @Override
    public String compile(String scriptsPath) throws InterruptedException, IOException, SungearEngineException {
        String javaPath = System.getProperty("java.home");
        Path compilerPath = Path.of(javaPath, "bin", "javac.exe");
        String sourcepath = "*.java";
        String classpath = String.join(";", Arrays.stream(new File(libsDirPath).listFiles()).map(File::getAbsolutePath).toList());
        String params = String.join(" ", new ArrayList<>() {{
            add(scriptsPath);
            add("-sourcepath " + sourcepath);
            add("-classpath \"" + classpath + "\"");
            add("-d \"" + compiledClassFilesDir + "\"");
        }});
        Process process = Runtime.getRuntime().exec(String.format("\"%s\" %s", compilerPath, params), null, new File(validatePathSeparator(tmpJavaFilesDirPath)));
        process.waitFor(compilerExecutionTimeoutSec, TimeUnit.SECONDS);
        String error = new String(process.getErrorStream().readAllBytes());
        String input = new String(process.getInputStream().readAllBytes());
        if (!error.isBlank()) {
            throw new SungearEngineException(SungearEngineError.SCRIPTS_COMPILATION_ERROR, classpath, sourcepath, error);
        }
        return input;
    }

    private String validatePathSeparator(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        path = path.replace("\\", "/");
        return path.endsWith("/") ? path : path + "/";
    }

    public void moveJavaFilesToTmpDir(String srcRootDir) throws SungearEngineException {
        File root = new File(srcRootDir);
        if (!root.isDirectory()) {
            throw new SungearEngineException(SungearEngineError.DIR_NOT_FOUND, root.getAbsolutePath());
        }
        Queue<File> haveToScanDirs = new LinkedList<>();
        haveToScanDirs.add(root);
        while (!haveToScanDirs.isEmpty()) {
            File file = haveToScanDirs.poll();
            for (File children : file.listFiles()) {
                if (children.isFile() && children.getName().endsWith((".java"))) {
                    File javaFileNewPath = new File(tmpJavaFilesDirPath + children.getName());
                    try {
                        Files.copy(children.toPath(), (javaFileNewPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new SungearEngineException(e, SungearEngineError.FILE_CREATION_ERROR, javaFileNewPath.getAbsolutePath());
                    }
                } else if (children.isDirectory()) {
                    haveToScanDirs.add(children);
                }
            }
        }
    }

    public long getCompilerExecutionTimeoutSec() {
        return compilerExecutionTimeoutSec;
    }

    public String getTmpJavaFilesDirPath() {
        return tmpJavaFilesDirPath;
    }

    public String getCompiledClassFilesDir() {
        return compiledClassFilesDir;
    }

    public String getLibsDirPath() {
        return libsDirPath;
    }

}
