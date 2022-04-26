package byx.script.interpreter.value;

public abstract class FieldWritableValue extends FieldReadableValue {
    @Override
    public void fieldAssign(String field, Value rhs) {
        setField(field, rhs);
    }

    @Override
    public void setField(String field, Value value) {
        super.setField(field, value);
    }
}
