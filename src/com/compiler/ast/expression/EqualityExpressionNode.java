package com.compiler.ast.expression;

import com.compiler.ast.AstNode;
import com.compiler.ast.expression.ExpressionNode;

import java.util.List;

public class EqualityExpressionNode extends BinaryOperationExpressionNode {
    private final EqualityType type;
    private final ExpressionNode first;
    private final ExpressionNode second;

    public EqualityExpressionNode(EqualityType type, ExpressionNode first, ExpressionNode second) {
        this.type = type;
        this.first = first;
        this.second = second;
    }

    public EqualityType getType() {
        return type;
    }

    public ExpressionNode getFirst() {
        return first;
    }

    @Override
    public String astDebug(int shift) {
        return SHIFT.repeat(shift) + type + ":\n" +
                first.astDebug(shift + 1) + "\n" +
                second.astDebug(shift + 1);
    }

    public enum EqualityType {EQ, NE}

    @Override
    public List<AstNode> getChildren() {
        return List.of(first, second);
    }
}
