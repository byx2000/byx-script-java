package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 列表字面量
 */
public class ListLiteral implements Expr {
    private final List<Expr> elems;

    public ListLiteral(List<Expr> elems) {
        this.elems = elems;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(elems.stream().map(e -> e.eval(scope)).collect(Collectors.toList()));
    }
}
