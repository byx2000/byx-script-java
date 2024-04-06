package byx.script.core.interpreter;

import byx.script.core.parser.ast.Program;
import byx.script.core.parser.ast.expr.*;
import byx.script.core.parser.ast.stmt.*;

/**
 * 抽象语法树访问者基类
 * @param <R> 返回值类型
 * @param <C> 上下文参数类型
 */
public interface ASTVisitor<R, C> {
    // 语句节点
    R visit(Program node, C ctx);
    R visit(VarDeclare node, C ctx);
    R visit(Assign node, C ctx);
    R visit(If node, C ctx);
    R visit(For node, C ctx);
    R visit(While node, C ctx);
    R visit(Block node, C ctx);
    R visit(Break node, C ctx);
    R visit(Continue node, C ctx);
    R visit(Return node, C ctx);
    R visit(ExprStatement node, C ctx);
    R visit(Try node, C ctx);
    R visit(Throw node, C ctx);

    // 表达式节点
    R visit(Literal node, C ctx);
    R visit(ListLiteral node, C ctx);
    R visit(ObjectLiteral node, C ctx);
    R visit(CallableLiteral node, C ctx);
    R visit(Var node, C ctx);
    R visit(UnaryExpr node, C ctx);
    R visit(BinaryExpr node, C ctx);
    R visit(FieldAccess node, C ctx);
    R visit(Subscript node, C ctx);
    R visit(Call node, C ctx);
}
