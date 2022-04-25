package byx.script.ast.expr;

import byx.script.ast.ASTVisitor;
import byx.script.ast.stmt.Statement;

import java.util.List;

/**
 * 函数字面量
 */
public class FunctionLiteral implements Expr {
    private final List<String> params;
    private final Statement body;

    public FunctionLiteral(List<String> params, Statement body) {
        this.params = params;
        this.body = body;
    }

    public List<String> getParams() {
        return params;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
