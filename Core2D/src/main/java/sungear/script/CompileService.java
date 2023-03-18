package sungear.script;

import sungear.exception.SungearEngineException;

import java.io.IOException;

public interface CompileService {
    String compile(String scriptsPath) throws InterruptedException, IOException, SungearEngineException;
}
