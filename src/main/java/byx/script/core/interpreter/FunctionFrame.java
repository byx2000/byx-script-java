package byx.script.core.interpreter;

import byx.script.core.interpreter.value.Value;

import java.util.ArrayDeque;
import java.util.function.Consumer;

public class FunctionFrame {
    private final Consumer<Value> returnCont;
    private final ArrayDeque<Consumer<Void>> breakStack = new ArrayDeque<>();
    private final ArrayDeque<Consumer<Void>> continueStack = new ArrayDeque<>();

    public FunctionFrame(Consumer<Value> returnCont) {
        this.returnCont = returnCont;
    }

    public Consumer<Value> getReturnCont() {
        return returnCont;
    }

    public boolean isBreakStackEmpty() {
        return breakStack.isEmpty();
    }

    public Consumer<Void> peekBreakStack() {
        return breakStack.peek();
    }

    public Consumer<Void> popBreakStack() {
        return breakStack.pop();
    }

    public void popBreakStackUntil(Consumer<Void> cont) {
        while (!breakStack.isEmpty() && breakStack.peek() != cont) {
            breakStack.pop();
        }
    }

    public void pushBreakStack(Consumer<Void> cont) {
        breakStack.push(cont);
    }

    public boolean isContinueStackEmpty() {
        return continueStack.isEmpty();
    }

    public Consumer<Void> peekContinueStack() {
        return continueStack.peek();
    }

    public Consumer<Void> popContinueStack() {
        return continueStack.pop();
    }

    public void popContinueStackUntil(Consumer<Void> cont) {
        while (!continueStack.isEmpty() && continueStack.peek() != cont) {
            continueStack.pop();
        }
    }

    public void pushContinueStack(Consumer<Void> cont) {
        continueStack.push(cont);
    }
}
