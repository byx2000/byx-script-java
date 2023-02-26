# ByxScript

ByxScript是一门类似JavaScript的函数式动态类型编程语言，具有以下特点：

* 支持整数、浮点数、布尔值、字符串、列表、函数、对象、`undefined`八种基本数据类型
* 支持if选择语句、for循环、while循环、异常处理等常用流程控制语句
* 支持闭包、高阶函数、运算符重载等高级特性

## 基本数据类型

|数据类型|字面量|
|---|---|
|整数|`123`|
|浮点数|`3.14`|
|布尔值|`true` `false`|
|字符串|`'hello world!'`|
|列表|`[1, 2, 3.14, 'hello']`|
|函数|`(a, b) => a + b`|
|对象|`{name: 'byx', age: 21}`|
|未定义|`undefined`|

## 变量定义

```javascript
var i = 123
var d = 3.14
var b1 = true
var b2 = false
var s = 'hello'
var list = [1, 2, 3.14, 'hello']
var fun = (a, b) => a + b
var obj = {
    name: 'byx',
    age: 21,
    scores: [87, 95, 81]
}
```

## 函数定义

```javascript
function fib(n) {
    if (n == 1 || n == 2) {
        return 1
    }
    return fib(n - 1) + fib(n - 2)
}
```

## if语句

```javascript
var score = 72
if (85 < score && score <= 100) {
    println('excellent')
} else if (75 < score && score <= 85) {
    println('good')
} else if (60 < score && score <= 75) {
    println('pass')
} else {
    println('failed')
}
```

## while循环

```javascript
var s = 0
var i = 1
while (i <= 100) {
    s += i
    i++
}
println(s) // 5050
```

## for循环

```javascript
var s = 0
for (var i = 1; i <= 100; ++i) {
    s += i
}
println(s) //5050
```

## 异常处理

```javascript
try {
    println('try')
    throw 'exception'
} catch (e) {
    println('catch', e)
} finally {
    println('finally')
}
```

## 运算符重载

```javascript
function Vector2(x, y) {
    return {
        x, y,
        _add(v) {
            return Vector2(x + v.x, y + v.y)
        },
        _sub(v) {
            return Vector2(x - v.x, y - v.y)
        },
        _mul(v) {
            return x * v.x + y * v.y
        },
        _div(a) {
            return Vector2(x / a, y / a)
        }
    }
}

var v1 = Vector2(3, 5)
var v2 = Vector2(4, -7)

var v3 = v1 + v2
println(v3.x, v3.y) // 7 -2

var v4 = v1 - v2
println(v4.x, v4.y) // -1 12

var v5 = v1 * v2
println(v5) // -23

var v6 = v1 / 2.0
println(v6.x, v6.y) // 1.5 2.5
```

## 闭包应用

```javascript
function Counter(init) {
    var cnt = init
    return {
        current: () => cnt,
        inc() {cnt++},
        dec() {cnt--}
    }
}

var c1 = Counter(100)
println(c1.current()) // 100
c1.inc()
println(c1.current()) // 101
c1.inc()
println(c1.current()) // 102
c1.dec()
println(c1.current()) // 101
c1.dec()
println(c1.current()) // 100

var c2 = Counter(200)
println(c2.current()) // 200
c2.inc()
println(c2.current()) // 201
c2.inc()
println(c2.current()) // 202
c2.dec()
println(c2.current()) // 201
c2.dec()
println(c2.current()) // 200
```
