package byx.script.core.interpreter.exception;

public class FieldNotExistException extends ByxScriptRuntimeException {
    public FieldNotExistException(String fieldName) {
        super(String.format("field %s not exist", fieldName));
    }
}
