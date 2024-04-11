package byx.script.core.interpreter;

import byx.script.core.interpreter.value.Value;

import java.util.ArrayDeque;
import java.util.function.Consumer;

public class FunctionStack {
    private final ArrayDeque<FunctionFrame> functionStack = new ArrayDeque<>();

    public FunctionStack() {
        functionStack.push(new FunctionFrame(null));
    }

    public void push(Consumer<Value> returnCont) {
        functionStack.push(new FunctionFrame(returnCont));
    }

    public void popUntil(FunctionFrame frame) {
        while (functionStack.peek() != frame) {
            functionStack.pop();
        }
    }

    public boolean isEmpty() {
        return functionStack.size() == 1;
    }

    public FunctionFrame peek() {
        return functionStack.peek();
    }

    public FunctionFrame pop() {
        return functionStack.pop();
    }
}
