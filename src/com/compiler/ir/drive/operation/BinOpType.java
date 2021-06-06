package com.compiler.ir.drive.operation;

public enum BinOpType {
    ADD("add"),
    SUB("sub"),
    MOD("srem"),
    DIV("sdiv"),
    GE("icmp sge"),
    LE("icmp sle"),
    GT("icmp sgt"),
    LT("icmp slt"),
    NE("icmp ne"),
    EQ("icmp eq"),
    AND("and"),
    OR("or"),
    MUL("mul");

    BinOpType(String code) {
        this.code = code;
    }

    public String toCode() {
        return code;
    }

    private String code;
}
