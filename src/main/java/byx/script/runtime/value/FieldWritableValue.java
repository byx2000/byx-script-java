package byx.script.runtime.value;

public abstract class FieldWritableValue extends FieldReadableValue {
    @Override
    public void fieldAssign(String field, Value rhs) {
        setField(field, rhs);
    }
}
