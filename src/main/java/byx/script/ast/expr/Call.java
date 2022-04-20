package byx.script.ast.expr;

import byx.script.ast.Expr;
import byx.script.runtime.Scope;
import byx.script.runtime.Value;

import java.util.List;
import java.util.stream.Collectors;

public class Call implements Expr {
    private final Expr expr;
    private final List<Expr> args;

    public Call(Expr callable, List<Expr> expr) {
        this.expr = callable;
        this.args = expr;
    }

    @Override
    public Value eval(Scope scope) {
        return expr.eval(scope).call(args.stream().map(p -> p.eval(scope)).collect(Collectors.toList()));
    }
}
