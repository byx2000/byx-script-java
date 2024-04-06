package byx.script.core.parser.parserc;

/**
 * 封装解析结果
 *
 * @param <R> 解析结果类型
 */
public record ParseResult<R>(R result, Cursor before, Cursor remain) {}
