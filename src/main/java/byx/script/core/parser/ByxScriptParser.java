package byx.script.core.parser;

import byx.script.core.common.Pair;
import byx.script.core.interpreter.exception.ByxScriptRuntimeException;
import byx.script.core.interpreter.value.*;
import byx.script.core.parser.ast.Program;
import byx.script.core.parser.ast.expr.*;
import byx.script.core.parser.ast.stmt.*;
import byx.script.core.parser.exception.ByxScriptParseException;
import byx.script.core.parser.exception.InternalParseException;
import byx.script.core.parser.parserc.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.script.core.parser.parserc.Parsers.*;

/**
 * ByxParser解析器
 * 将ByxScript脚本解析成抽象语法树
 */
public class ByxScriptParser {
    // 空白字符
    private static final Parser<?> ws = chs(' ', '\t', '\r', '\n');

    // 行注释
    private static final Parser<?> lineComment = str("//").and(not(ch('\n')).and(any()).many()).and('\n');

    // 块注释
    private static final Parser<?> blockComment = str("/*").and(not(str("*/")).and(any()).many()).and("*/");

    // 可忽略元素
    private static final Parser<?> ignorable = alt(ws, lineComment, blockComment).many();

    // 字母
    private static final Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));

    // 数字
    private static final Parser<Character> digit = range('0', '9');

    // 数字串
    private static final Parser<String> digits = digit.many1().map(ByxScriptParser::join);

    // 下划线
    private static final Parser<Character> underline = ch('_');

    // 整数
    private static final Parser<String> integer = digits.surround(ignorable);

    // 浮点数
    private static final Parser<String> decimal = seq(digits, ch('.'), digits)
            .map(rs -> String.valueOf(rs.get(0)) + rs.get(1) + rs.get(2))
            .surround(ignorable);

    // 字符串
    private static final Parser<String> string = seq(ch('\''), not('\'').many(), ch('\''))
            .map(rs -> join((List<?>) rs.get(1)))
            .surround(ignorable);

    // 布尔值
    private static final Parser<String> bool = strs("true", "false").surround(ignorable);

    // 符号
    private static final Parser<String> assign = str("=").notFollow(strs("=", ">")).surround(ignorable);
    private static final Parser<String> assign_fatal = withFatal(assign, "expect '='");
    private static final Parser<String> semi = str(";").surround(ignorable);
    private static final Parser<String> semi_fatal = withFatal(semi, "expect semi");
    private static final Parser<String> comma = str(",").surround(ignorable);
    private static final Parser<String> colon = str(":").surround(ignorable);
    private static final Parser<String> dot = str(".").surround(ignorable);
    private static final Parser<String> lp = str("(").surround(ignorable);
    private static final Parser<String> lp_fatal = withFatal(lp, "expect '('");
    private static final Parser<String> rp = str(")").surround(ignorable);
    private static final Parser<String> rp_fatal = withFatal(rp, "expect ')'");
    private static final Parser<String> lb = str("{").surround(ignorable);
    private static final Parser<String> lb_fatal = withFatal(lb, "expect '{'");
    private static final Parser<String> rb = str("}").surround(ignorable);
    private static final Parser<String> rb_fatal = withFatal(rb, "expect '}'");
    private static final Parser<String> ls = str("[").surround(ignorable);
    private static final Parser<String> rs = str("]").surround(ignorable);
    private static final Parser<String> rs_fatal = withFatal(rs, "expect ']'");
    private static final Parser<String> add = str("+").notFollow(strs("+", "=")).surround(ignorable);
    private static final Parser<String> sub = str("-").notFollow(strs("-", "=")).surround(ignorable);
    private static final Parser<String> mul = str("*").notFollow(str("=")).surround(ignorable);
    private static final Parser<String> div = str("/").notFollow(str("=")).surround(ignorable);
    private static final Parser<String> rem = str("%").surround(ignorable);
    private static final Parser<String> gt = str(">").notFollow(str("=")).surround(ignorable);
    private static final Parser<String> get = str(">=").surround(ignorable);
    private static final Parser<String> lt = str("<").notFollow(str("=")).surround(ignorable);
    private static final Parser<String> let = str("<=").surround(ignorable);
    private static final Parser<String> equ = str("==").surround(ignorable);
    private static final Parser<String> neq = str("!=").surround(ignorable);
    private static final Parser<String> and = str("&&").surround(ignorable);
    private static final Parser<String> or = str("||").surround(ignorable);
    private static final Parser<String> not = str("!").notFollow(str("=")).surround(ignorable);
    private static final Parser<String> arrow = str("=>").surround(ignorable);
    private static final Parser<String> inc = str("++").surround(ignorable);
    private static final Parser<String> dec = str("--").surround(ignorable);
    private static final Parser<String> addAssign = str("+=").surround(ignorable);
    private static final Parser<String> subAssign = str("-=").surround(ignorable);
    private static final Parser<String> mulAssign = str("*=").surround(ignorable);
    private static final Parser<String> divAssign = str("/=").surround(ignorable);
    private static final Parser<String> assignOp = oneOf(assign, addAssign, subAssign, mulAssign, divAssign);

    private static final Parser<?> identifierPart = oneOf(digit, alpha, underline).many1();

    // 关键字
    private static final Parser<String> import_ = str("import").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> var_ = str("var").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> if_ = str("if").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> else_ = str("else").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> for_ = str("for").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> while_ = str("while").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> break_ = str("break").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> continue_ = str("continue").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> return_ = str("return").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> func_ = str("func").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> null_ = str("null").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> try_ = str("try").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> catch_ = str("catch").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> catch_fatal = withFatal(catch_, "expect 'catch'");
    private static final Parser<String> finally_ = str("finally").notFollow(identifierPart).surround(ignorable);
    private static final Parser<String> throw_ = str("throw").notFollow(identifierPart).surround(ignorable);

    private static final Parser<String> keywords = oneOf(
            import_, var_, if_, else_, for_, while_,
            break_, continue_, return_, func_, null_,
            try_, catch_, finally_, throw_
    );

    // 标识符
    private static final Parser<String> identifier = skip(not(keywords))
            .and(oneOf(alpha, underline))
            .and(oneOf(digit, alpha, underline).many())
            .map(p -> p.first() + join(p.second()))
            .surround(ignorable);
    private static final Parser<String> identifier_fatal = withFatal(identifier, "expect identifier");

    // 前向引用
    private static final Parser<Statement> lazyStmt = lazy(() -> ByxScriptParser.stmt);
    private static final Parser<List<Statement>> stmts = lazyStmt.skip(semi.opt()).many();
    private static final Parser<Expr> lazyPrimaryExpr = lazy(() -> ByxScriptParser.primaryExpr);
    private static final Parser<Expr> lazyExpr = lazy(() -> ByxScriptParser.expr);
    private static final Parser<Expr> lazyExpr_fatal = withFatal(lazyExpr, "invalid expression");

    // 代码块
    private static final Parser<Statement> block = skip(lb).and(stmts).skip(rb_fatal).map(Block::new);
    private static final Parser<Statement> block_fatal = skip(lb_fatal).and(stmts).skip(rb_fatal).map(Block::new);

    // 表达式

    // 整数字面量
    private static final Parser<Expr> integerLiteral = integer
            .map(s -> new Literal(new IntegerValue(Integer.parseInt(s))));

    // 浮点数字面量
    private static final Parser<Expr> doubleLiteral = decimal
            .map(s -> new Literal(new DoubleValue(Double.parseDouble(s))));

    // 字符串字面量
    private static final Parser<Expr> stringLiteral = string
            .map(s -> new Literal(new StringValue(s)));

    // 布尔值字面量
    private static final Parser<Expr> boolLiteral = bool
            .map(s -> new Literal(BoolValue.of(Boolean.parseBoolean(s))));

    // undefined字面量
    private static final Parser<Expr> nullLiteral = null_.map(r -> new Literal(NullValue.INSTANCE));

    private static final Parser<List<String>> idList = oneOf(
            identifier.and(skip(comma).and(identifier_fatal).many()).map(ByxScriptParser::mapToList),
            empty(Collections.emptyList())
    );

    // 函数字面量
    private static final Parser<List<String>> singleParamList = identifier.map(List::of);
    private static final Parser<List<String>> multiParamList = skip(lp).and(idList).skip(rp);
    private static final Parser<List<String>> lambdaParamList = singleParamList.or(multiParamList);
    private static final Parser<Expr> callableLiteral = lambdaParamList.skip(arrow).and(oneOf(
            skip(lb).and(stmts).skip(rb_fatal).map(Block::new),
            lazyExpr.map(Return::new)
    )).map(p -> new CallableLiteral(p.first(), p.second()));

    // 对象字面量
    private static final Parser<Pair<String, Expr>> fieldPair = oneOf(
            identifier.skip(colon).and(lazyExpr),
            identifier.skip(lp).and(idList).skip(rp_fatal).and(block_fatal)
                    .map(p -> new Pair<>(p.first().first(), new CallableLiteral(p.first().second(), p.second()))),
            identifier.map(id -> new Pair<>(id, new Var(id)))
    );
    private static final Parser<List<Pair<String, Expr>>> fieldList = fieldPair
            .and(skip(comma).and(fieldPair).many())
            .map(ByxScriptParser::mapToList)
            .opt(Collections.emptyList());
    private static final Parser<Expr> objLiteral = skip(lb).and(fieldList).skip(rb_fatal)
            .map(ps -> new ObjectLiteral(ps.stream().collect(Collectors.toMap(Pair::first, Pair::second))));

    private static final Parser<List<Expr>> exprList = lazyExpr
            .and(skip(comma).and(lazyExpr_fatal).many())
            .map(ByxScriptParser::mapToList)
            .opt(Collections.emptyList());

    // 列表字面量
    private static final Parser<Expr> listLiteral = skip(ls)
            .and(exprList)
            .skip(rs_fatal)
            .map(ListLiteral::new);

    // 变量
    private static final Parser<Expr> var = identifier.map(Var::new);

    // 下标
    private static final Parser<Expr> subscript = skip(ls).and(lazyExpr).skip(rs_fatal);

    // 字段访问
    private static final Parser<String> fieldAccess = skip(dot).and(identifier_fatal);

    // 调用列表
    private static final Parser<List<Expr>> callList = skip(lp)
            .and(exprList)
            .skip(rp_fatal);

    // 表达式

    private static final Parser<Expr> bracketExpr = skip(lp).and(lazyExpr).skip(rp);
    private static final Parser<Expr> negExpr = skip(sub).and(lazyPrimaryExpr).map(e -> new UnaryExpr(UnaryOp.Neg, e));
    private static final Parser<Expr> notExpr = skip(not).and(lazyPrimaryExpr).map(e -> new UnaryExpr(UnaryOp.Not, e));

    private static final Parser<Expr> primaryExpr = oneOf(
            doubleLiteral,
            integerLiteral,
            stringLiteral,
            boolLiteral,
            nullLiteral,
            callableLiteral,
            var,
            objLiteral,
            listLiteral,
            bracketExpr,
            negExpr,
            notExpr
    ).and(alt(callList, fieldAccess, subscript).many()).map(ByxScriptParser::buildPrimaryExpr);
    private static final Parser<Expr> multiplicativeExpr = primaryExpr.and(oneOf(mul, div, rem).and(withFatal(primaryExpr, "invalid expression")).many())
            .map(ByxScriptParser::buildBinaryExpr);
    private static final Parser<Expr> additiveExpr = multiplicativeExpr.and(oneOf(add, sub).and(withFatal(multiplicativeExpr, "invalid expression")).many())
            .map(ByxScriptParser::buildBinaryExpr);
    private static final Parser<Expr> relationalExpr = additiveExpr.and(oneOf(let, lt, get, gt, equ, neq).and(withFatal(additiveExpr, "invalid expression")).many())
            .map(ByxScriptParser::buildBinaryExpr);
    private static final Parser<Expr> andExpr = relationalExpr.and(and.and(withFatal(relationalExpr, "invalid expression")).many())
            .map(ByxScriptParser::buildBinaryExpr);
    private static final Parser<Expr> expr = andExpr.and(or.and(withFatal(andExpr, "invalid expression")).many())
            .map(ByxScriptParser::buildBinaryExpr);
    private static final Parser<Expr> expr_fatal = withFatal(expr, "invalid expression");

    // 语句

    // 变量声明
    private static final Parser<Statement> varDeclare = skip(var_)
            .and(identifier_fatal)
            .skip(assign_fatal)
            .and(expr_fatal)
            .map(p -> new VarDeclare(p.first(), p.second()));

    // 函数声明
    private static final Parser<List<String>> paramList = oneOf(
            expect(rp, Collections.emptyList()),
            identifier_fatal.and(skip(comma).and(identifier_fatal).many()).map(ByxScriptParser::mapToList)
    );
    private static final Parser<Statement> funcDeclare = skip(func_)
            .and(identifier_fatal)
            .skip(lp_fatal)
            .and(paramList)
            .skip(rp_fatal.and(lb_fatal))
            .and(stmts)
            .skip(rb_fatal)
            .map(p -> {
                String functionName = p.first().first();
                List<String> params = p.first().second();
                Statement body = new Block(p.second());
                return new VarDeclare(functionName, new CallableLiteral(params, body));
            });

    private static final Parser<Expr> assignable = identifier.and(alt(subscript, fieldAccess).many())
            .map(ByxScriptParser::buildAssignable);

    // 赋值语句
    private static final Parser<Statement> assignStmt = assignable.and(assignOp).and(expr)
            .map(ByxScriptParser::buildAssignStatement);

    // 自增语句
    private static final Parser<Statement> preInc = skip(inc).and(assignable)
            .map(e -> new Assign(e, new BinaryExpr(BinaryOp.Add, e, new Literal(new IntegerValue(1)))));
    private static final Parser<Statement> postInc = assignable.skip(inc)
            .map(e -> new Assign(e, new BinaryExpr(BinaryOp.Add, e, new Literal(new IntegerValue(1)))));

    // 自减语句
    private static final Parser<Statement> preDec = skip(dec).and(assignable)
            .map(e -> new Assign(e, new BinaryExpr(BinaryOp.Sub, e, new Literal(new IntegerValue(1)))));
    private static final Parser<Statement> postDec = assignable.skip(dec)
            .map(e -> new Assign(e, new BinaryExpr(BinaryOp.Sub, e, new Literal(new IntegerValue(1)))));

    // if语句
    private static final Parser<Pair<Expr, Statement>> ifPart =
            skip(if_.and(lp_fatal))
            .and(expr_fatal)
            .skip(rp_fatal)
            .and(block_fatal);
    private static final Parser<List<Pair<Expr, Statement>>> elseifPart =
            skip(else_.and(if_).and(lp_fatal))
            .and(expr_fatal)
            .skip(rp_fatal)
            .and(block_fatal)
            .many();
    private static final Parser<Statement> elsePart =
            skip(else_)
            .and(block_fatal)
            .opt(new Block(Collections.emptyList()));
    private static final Parser<Statement> ifStmt = ifPart.and(elseifPart).and(elsePart)
            .map(ByxScriptParser::buildIfNode);

    // for语句
    private static final Parser<Statement> forStmt =
            skip(for_.and(lp))
            .and(lazyStmt)
            .skip(semi_fatal)
            .and(expr)
            .skip(semi_fatal)
            .and(lazyStmt)
            .skip(rp_fatal)
            .and(block_fatal)
            .map(ByxScriptParser::buildForNode);

    // while语句
    private static final Parser<Statement> whileStmt =
            skip(while_.and(lp_fatal))
            .and(expr)
            .skip(rp_fatal)
            .and(block_fatal)
            .map(p -> new While(p.first(), p.second()));

    // break语句
    private static final Parser<Statement> breakStmt = break_.value(Break.INSTANCE);

    // continue语句
    private static final Parser<Statement> continueStmt = continue_.value(Continue.INSTANCE);

    // return语句
    private static final Parser<Statement> returnStmt = skip(return_).and(expr.opt(new Literal(NullValue.INSTANCE)))
            .map(Return::new);

    // try-catch-finally语句
    private static final Parser<Statement> tryPart = skip(try_).and(block_fatal);
    private static final Parser<Pair<String, Statement>> catchPart =
            skip(catch_fatal.and(lp_fatal))
            .and(identifier)
            .skip(rp_fatal)
            .and(block_fatal);
    private static final Parser<Statement> finallyPart = skip(finally_).and(block_fatal);
    private static final Parser<Statement> tryStmt =
            tryPart
            .and(catchPart)
            .and(finallyPart.opt(new Block(Collections.emptyList())))
            .map(ByxScriptParser::buildTryNode);

    // throw语句
    private static final Parser<Statement> throwStmt = skip(throw_).and(expr).map(Throw::new);

    // 表达式语句
    private static final Parser<Statement> exprStmt = expr.map(ExprStatement::new);

    private static final Parser<Statement> stmt = oneOf(
            varDeclare,
            funcDeclare,
            ifStmt,
            forStmt,
            whileStmt,
            breakStmt,
            continueStmt,
            returnStmt,
            block,
            tryStmt,
            throwStmt,
            preInc,
            preDec,
            assignStmt,
            postInc,
            postDec,
            exprStmt
    );

    // 导入声明
    private static final Parser<String> importName = oneOf(digit, alpha, underline, ch('/')).many1().surround(ignorable)
            .map(ByxScriptParser::join);
    private static final Parser<List<String>> imports = skip(import_).and(importName).many();

    // 程序
    private static final Parser<Program> program = imports.and(stmts).end()
            .map(p -> new Program(p.first(), p.second()));

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    @SuppressWarnings("unchecked")
    private static Expr buildPrimaryExpr(Pair<Expr, List<Object>> p) {
        Expr e = p.first();
        for (Object o : p.second()) {
            if (o instanceof List) {
                e = new Call(e, (List<Expr>) o);
            } else if (o instanceof String) {
                e = new FieldAccess(e, (String) o);
            } else if (o instanceof Expr) {
                e = new Subscript(e, (Expr) o);
            }
        }
        return e;
    }

    private static Expr buildBinaryExpr(Pair<Expr, List<Pair<String, Expr>>> r) {
        Expr expr = r.first();
        for (Pair<String, Expr> p : r.second()) {
            String op = p.first();
            switch (op) {
                case "+" -> expr = new BinaryExpr(BinaryOp.Add, expr, p.second());
                case "-" -> expr = new BinaryExpr(BinaryOp.Sub, expr, p.second());
                case "*" -> expr = new BinaryExpr(BinaryOp.Mul, expr, p.second());
                case "/" -> expr = new BinaryExpr(BinaryOp.Div, expr, p.second());
                case "%" -> expr = new BinaryExpr(BinaryOp.Rem, expr, p.second());
                case ">" -> expr = new BinaryExpr(BinaryOp.GreaterThan, expr, p.second());
                case ">=" -> expr = new BinaryExpr(BinaryOp.GreaterEqualThan, expr, p.second());
                case "<" -> expr = new BinaryExpr(BinaryOp.LessThan, expr, p.second());
                case "<=" -> expr = new BinaryExpr(BinaryOp.LessEqualThan, expr, p.second());
                case "==" -> expr = new BinaryExpr(BinaryOp.Equal, expr, p.second());
                case "!=" -> expr = new BinaryExpr(BinaryOp.NotEqual, expr, p.second());
                case "&&" -> expr = new BinaryExpr(BinaryOp.And, expr, p.second());
                case "||" -> expr = new BinaryExpr(BinaryOp.Or, expr, p.second());
                default -> throw new ByxScriptRuntimeException("unknown binary operator: " + op);
            }
        }
        return expr;
    }

    private static Expr buildAssignable(Pair<String, List<Object>> p) {
        Expr e = new Var(p.first());
        for (Object o : p.second()) {
            if (o instanceof Expr) {
                e = new Subscript(e, (Expr) o);
            } else if (o instanceof String) {
                e = new FieldAccess(e, (String) o);
            }
        }
        return e;
    }

    private static Statement buildAssignStatement(Pair<Pair<Expr, String>, Expr> p) {
        Expr lhs = p.first().first();
        Expr rhs = p.second();
        String op = p.first().second();
        return switch (op) {
            case "=" -> new Assign(lhs, rhs);
            case "+=" -> new Assign(lhs, new BinaryExpr(BinaryOp.Add, lhs, rhs));
            case "-=" -> new Assign(lhs, new BinaryExpr(BinaryOp.Sub, lhs, rhs));
            case "*=" -> new Assign(lhs, new BinaryExpr(BinaryOp.Mul, lhs, rhs));
            case "/=" -> new Assign(lhs, new BinaryExpr(BinaryOp.Div, lhs, rhs));
            default -> throw new RuntimeException("invalid assign expression: " + op);
        };
    }

    private static <T> Parser<T> withFatal(Parser<T> p, String msg) {
        return p.fatal(e -> {
            Pair<Integer, Integer> rowAndCol = calculateRowAndCol(e.getInput(), e.getIndex());
            return new ByxScriptParseException(rowAndCol.first(), rowAndCol.second(), msg);
        });
    }

    private static <T> List<T> mapToList(Pair<T, List<T>> p) {
        List<T> list = new ArrayList<>();
        list.add(p.first());
        list.addAll(p.second());
        return list;
    }

    private static If buildIfNode(Pair<Pair<Pair<Expr, Statement>, List<Pair<Expr, Statement>>>, Statement> p) {
        List<Pair<Expr, Statement>> cases = new ArrayList<>();
        cases.add(new Pair<>(p.first().first().first(), p.first().first().second()));
        for (Pair<Expr, Statement> pp : p.first().second()) {
            cases.add(new Pair<>(pp.first(), pp.second()));
        }
        Statement elseBranch = p.second();
        return new If(cases, elseBranch);
    }

    private static For buildForNode(Pair<Pair<Pair<Statement, Expr>, Statement>, Statement> p) {
        Statement init = p.first().first().first();
        Expr cond = p.first().first().second();
        Statement update = p.first().second();
        Statement body = p.second();
        return new For(init, cond, update, body);
    }

    private static Try buildTryNode(Pair<Pair<Statement, Pair<String, Statement>>, Statement> p) {
        Statement tryBranch = p.first().first();
        String catchVar = p.first().second().first();
        Statement catchBranch = p.first().second().second();
        Statement finallyBranch = p.second();
        return new Try(tryBranch, catchVar, catchBranch, finallyBranch);
    }

    private static Pair<Integer, Integer> calculateRowAndCol(String s, int index) {
        int row = 1;
        int col = 1;
        for (int i = 0; i < index; ++i) {
            if (s.charAt(i) == '\n') {
                row++;
                col = 1;
            } else {
                col++;
            }
        }
        return new Pair<>(row, col);
    }

    /**
     * 将脚本解析为抽象语法树
     * @param script 脚本字符串
     * @return 抽象语法树
     */
    public static Program parse(String script) throws ByxScriptParseException {
        try {
            return program.parse(script);
        } catch (ByxScriptParseException e) {
            throw e;
        } catch (InternalParseException e) {
            Pair<Integer, Integer> rowAndCol = calculateRowAndCol(e.getInput(), e.getIndex());
            throw new ByxScriptParseException(rowAndCol.first(), rowAndCol.second(), "unknown parse exception");
        } catch (Exception e) {
            throw new ByxScriptParseException("unknown parse exception: " + e.getMessage());
        }
    }
}
