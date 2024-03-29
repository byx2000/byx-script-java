package byx.script.parser.ast.expr;

import byx.script.parser.ast.ASTVisitor;
import byx.script.parser.ast.stmt.Statement;

import java.util.List;

/**
 * 函数字面量
 */
public class CallableLiteral implements Expr {
    private final List<String> params;
    private final Statement body;

    public CallableLiteral(List<String> params, Statement body) {
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
