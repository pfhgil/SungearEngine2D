package sungear.exception;

public enum SungearEngineError {

    THERE_ARE_NO_PERMISSIONS("SEE-SUNGEARENGINE-1", "There are no permissions to %s"),
    FILE_NOT_FOUND("SEE-SUNGEARENGINE-2", "File %s has been not found"),
    SCRIPT_DIR_DOES_NOT_EXIST("SEE-SUNGEARENGINE-3", "Script directory with path '%s' has not been found"),
    DIR_NOT_FOUND("SEE-SUNGEARENGINE-4", "Directory with path '%s' has not been found"),
    FILE_CREATION_ERROR("SEE-SUNGEARENGINE-5", "File with path '%s' has not be created"),
    SCRIPTS_COMPILATION_ERROR("SEE-SUNGEARENGINE-6", "Compilation '%s' with classpath '%s'. Error message: '%s'"),
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
