package byx.script.interpreter;

import byx.script.common.FastException;

/**
 * 执行break语句时会抛出该异常
 * 外层的For节点和While节点捕获该异常之后执行相应操作
 */
public class BreakException extends FastException {
}
