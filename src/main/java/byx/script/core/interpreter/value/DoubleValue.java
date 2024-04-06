package byx.script.core.interpreter.value;

public record DoubleValue(double value) implements Value {
    @Override
    public String typeId() {
        return "double";
    }
}
