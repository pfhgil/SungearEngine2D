package sungear.exception;

import java.io.IOException;

public class SungearEngineException extends Exception {


    public SungearEngineException(SungearEngineError error, String... arguments) {
        super(new IOException(String.format(error.getErrorText(), arguments)));
    }

    public SungearEngineException(Throwable throwable, SungearEngineError error, String... arguments) {
        super(new IOException(String.format(error.getErrorText(), arguments), throwable));
    }
}
