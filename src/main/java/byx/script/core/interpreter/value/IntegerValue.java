package byx.script.core.interpreter.value;

public record IntegerValue(int value) implements Value {
    @Override
    public String typeId() {
        return "integer";
    }
}
