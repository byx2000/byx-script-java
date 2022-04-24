package byx.script.ast;

import byx.script.ast.expr.*;
import byx.script.ast.stmt.*;

/**
 * 抽象语法树访问者基类
 * @param <R> 返回值类型
 * @param <C> 上下文参数类型
 */
public interface ASTVisitor<R, C> {
    R visit(C ctx, Program node);
    R visit(C ctx, VarDeclaration node);
    R visit(C ctx, Assign node);
    R visit(C ctx, If node);
    R visit(C ctx, For node);
    R visit(C ctx, While node);
    R visit(C ctx, Block node);
    R visit(C ctx, Break node);
    R visit(C ctx, Continue node);
    R visit(C ctx, Return node);
    R visit(C ctx, ExprStatement node);

    R visit(C ctx, Literal node);
    R visit(C ctx, ListLiteral node);
    R visit(C ctx, ObjectLiteral node);
    R visit(C ctx, FunctionLiteral node);
    R visit(C ctx, Var node);
    R visit(C ctx, UnaryExpr node);
    R visit(C ctx, BinaryExpr node);
    R visit(C ctx, FieldAccess node);
    R visit(C ctx, Subscript node);
    R visit(C ctx, Call node);
}
