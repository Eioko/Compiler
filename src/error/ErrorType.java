package error;

public enum ErrorType {
    ILLEGAL_SYMBOL("a"),                  // a 非法符号
    REDEFINED_IDENTIFIER("b"),            // b 名字重定义
    UNDEFINED_IDENTIFIER("c"),            // c 未定义的名字
    ARGUMENT_COUNT_MISMATCH("d"),         // d 函数参数个数不匹配
    ARGUMENT_TYPE_MISMATCH("e"),          // e 函数参数类型不匹配
    INVALID_RETURN_IN_VOID_FUNCTION("f"), // f 无返回值的函数存在不匹配的return
    MISSING_RETURN_IN_NONVOID("g"),       // g 有返回值的函数缺少return语句
    ASSIGN_TO_CONST("h"),                 // h 不能改变常量的值
    MISSING_SEMICOLON("i"),               // i 缺少分号
    MISSING_RIGHT_PARENTHESIS("j"),       // j 缺少右小括号
    MISSING_RIGHT_BRACKET("k"),           // k 缺少右中括号
    PRINTF_FORMAT_ARG_MISMATCH("l"),      // l printf中格式字符与表达式个数不匹配
    LOOP_CONTROL_OUTSIDE_LOOP("m");       // m 在非循环块中使用break/continue

    private String value;

    ErrorType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
};