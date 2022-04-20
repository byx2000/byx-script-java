package byx.script.ast.expr;

import byx.script.ast.Expr;
import byx.script.runtime.Scope;
import byx.script.runtime.Value;

import java.util.Map;
import java.util.stream.Collectors;

public class ObjectLiteral implements Expr {
    private final Map<String, Expr> props;

    public ObjectLiteral(Map<String, Expr> props) {
        this.props = props;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(props.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().eval(scope))));
    }
}
