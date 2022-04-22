package byx.script.ast.stmt;

import byx.script.ast.expr.Expr;
import byx.script.runtime.Scope;

import java.util.List;
import java.util.stream.Collectors;

public class CallStatement implements Statement {
    private final Expr callable;
    private final List<Expr> args;

    public CallStatement(Expr callable, List<Expr> args) {
        this.callable = callable;
        this.args = args;
    }

    @Override
    public void execute(Scope scope) {
        callable.eval(scope).call(args.stream().map(p -> p.eval(scope)).collect(Collectors.toList()));
    }
}
