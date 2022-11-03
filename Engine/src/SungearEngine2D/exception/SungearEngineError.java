package SungearEngine2D.exception;

public enum SungearEngineError {

    THERE_ARE_NO_PERMISSIONS("SEE-SUNGEARENGINE-1", "There are no permissions to %s"),
    FILE_NOT_FOUND("SEE-SUNGEARENGINE-2", "File %s has been not found"),
    ;

    private final String code;
    private final String errorText;

    SungearEngineError(String code, String errorText) {
        this.code = code;
        this.errorText = errorText;
    }

    public String getErrorText() {
        return code + ": " + errorText;
    }
}
