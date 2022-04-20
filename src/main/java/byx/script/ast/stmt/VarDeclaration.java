package byx.script.ast.stmt;

import byx.script.ast.expr.Expr;
import byx.script.runtime.Scope;

public class VarDeclaration implements Statement {
    private final String varName;
    private final Expr expr;

    public VarDeclaration(String varName, Expr expr) {
        this.varName = varName;
        this.expr = expr;
    }

    @Override
    public void execute(Scope scope) {
        scope.declareVar(varName, expr.eval(scope));
    }
}
