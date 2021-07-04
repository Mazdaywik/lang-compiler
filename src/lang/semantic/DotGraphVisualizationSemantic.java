package lang.semantic;

import lang.ast.ArrayTypeNode;
import lang.ast.AstNode;
import lang.ast.BasicTypeNode;
import lang.ast.FileNode;
import lang.ast.FunctionNode;
import lang.ast.ObjectTypeNode;
import lang.ast.TypeNode;
import lang.ast.expression.ArrayConstructorExpressionNode;
import lang.ast.expression.ConditionalExpressionNode;
import lang.ast.expression.ExpressionNode;
import lang.ast.expression.VariableExpressionNode;
import lang.ast.expression.binary.AdditiveExpressionNode;
import lang.ast.expression.binary.AssigmentExpressionNode;
import lang.ast.expression.binary.EqualityExpressionNode;
import lang.ast.expression.binary.LogicalAndExpressionNode;
import lang.ast.expression.binary.LogicalOrExpressionNode;
import lang.ast.expression.binary.MultiplicativeExpressionNode;
import lang.ast.expression.binary.RelationalExpressionNode;
import lang.ast.expression.consts.BoolConstantExpressionNode;
import lang.ast.expression.consts.FloatConstantExpressionNode;
import lang.ast.expression.consts.IntConstantExpressionNode;
import lang.ast.expression.consts.NullConstantExpressionNode;
import lang.ast.expression.unary.postfix.ArrayAccessExpressionNode;
import lang.ast.expression.unary.postfix.FieldAccessExpressionNode;
import lang.ast.expression.unary.postfix.FunctionCallExpressionNode;
import lang.ast.expression.unary.postfix.PostfixDecrementSubtractionExpressionNode;
import lang.ast.expression.unary.postfix.PostfixIncrementAdditiveExpressionNode;
import lang.ast.expression.unary.postfix.PostfixIncrementMultiplicativeExpressionNode;
import lang.ast.expression.unary.prefix.CastExpressionNode;
import lang.ast.expression.unary.prefix.PrefixDecrementSubtractionExpressionNode;
import lang.ast.expression.unary.prefix.PrefixIncrementAdditiveExpressionNode;
import lang.ast.expression.unary.prefix.PrefixIncrementMultiplicativeExpressionNode;
import lang.ast.statement.BreakStatementNode;
import lang.ast.statement.ClassStatementNode;
import lang.ast.statement.CompoundStatementNode;
import lang.ast.statement.ConstructorDefinitionNode;
import lang.ast.statement.ContinueStatementNode;
import lang.ast.statement.DeclarationStatementNode;
import lang.ast.statement.ElifStatementNode;
import lang.ast.statement.ElseStatementNode;
import lang.ast.statement.EmptyStatementNode;
import lang.ast.statement.ExpressionStatementNode;
import lang.ast.statement.FunctionDefinitionNode;
import lang.ast.statement.IfElseStatementNode;
import lang.ast.statement.IfStatementNode;
import lang.ast.statement.InterfaceStatementNode;
import lang.ast.statement.ReturnStatementNode;
import lang.ast.statement.StatementNode;
import lang.ast.statement.WhileStatementNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DotGraphVisualizationSemantic {

    private final List<FileNode> fileNodes;
    private int count;

    private final Map<AstNode, String> nodeToField = new HashMap<>();
    private final Map<AstNode, AstNode> nodeToNode = new HashMap<>();
    private String currentStruct;

    public DotGraphVisualizationSemantic(List<FileNode> fileNodes) {
        this.fileNodes = fileNodes;
    }

    public String dotVisualization() {
        StringBuilder builder = new StringBuilder();

        builder.append("digraph G { \n" +
                "graph [layout = neato,landscape=false, overlap = FALSE," +
                " outputorder = edgesfirst, splines = curved]\n");

        fileNodes.forEach(f -> builder.append(fileNodeVisualization(f)));

        builder.append("\n");

        for (Map.Entry<AstNode, AstNode> entry : nodeToNode.entrySet()) {
//            builder.append("\t" + nodeToField.get(entry.getKey()) + " -> " + nodeToField.get(entry.getValue()) + ";\n");
        }

        builder.append("\n}");

        return builder.toString();
    }

    public String fileNodeVisualization(FileNode f) {
        StringBuilder builder = new StringBuilder();

        String name = "\"" + "struct_" + f.getPath() + "\"";

        currentStruct = name;

        builder.append("\tnode [shape=record];\n");
        builder
                .append("\t")
                .append(name)
                .append(" [label=\"{")
                .append(f.getPath().replaceAll("\\\\", "\\\\\\\\"))
                .append("|");

        builder.append(f.getStatementNodes().stream()
                .map(this::statementNodeVisualization)
                .collect(Collectors.joining("|")));

        builder
                .append("}\"];");

        return builder.toString();
    }

    private String statementNodeVisualization(StatementNode node) {
        if (node instanceof BreakStatementNode) {
            return breakNodeVisualization((BreakStatementNode) node);
        } else if (node instanceof ClassStatementNode) {
            return classNodeVisualization((ClassStatementNode) node);
        } else if (node instanceof CompoundStatementNode) {
            return compoundNodeVisualization((CompoundStatementNode) node);
        } else if (node instanceof ContinueStatementNode) {
            return continueNodeVisualization((ContinueStatementNode) node);
        } else if (node instanceof DeclarationStatementNode) {
            return declarationNodeVisualisation((DeclarationStatementNode) node);
        } else if (node instanceof IfElseStatementNode) {
            return ifElseNodeVisualization((IfElseStatementNode) node);
        } else if (node instanceof InterfaceStatementNode) {
            return interfaceNodeVisualization((InterfaceStatementNode) node);
        } else if (node instanceof ReturnStatementNode) {
            return returnNodeVisualization((ReturnStatementNode) node);
        } else if (node instanceof WhileStatementNode) {
            return whileNodeVisualization((WhileStatementNode) node);
        } else if (node instanceof FunctionDefinitionNode) {
            return functionDefinitionNodeVisualization((FunctionDefinitionNode) node);
        } else if (node instanceof EmptyStatementNode) {
            return "";
        } else if (node instanceof ExpressionStatementNode) {
            return expressionNodeVisualization(((ExpressionStatementNode) node).getExpressionNode());
        } else if (node instanceof ConstructorDefinitionNode) {
            return constructorNodeVisualization((ConstructorDefinitionNode) node);
        } else if (node instanceof IfStatementNode) {
            return ifNodeVisualization((IfStatementNode) node);
        } else if (node instanceof ElifStatementNode) {
            return elifNodeVisualization((ElifStatementNode) node);
        } else if (node instanceof ElseStatementNode) {
            return elseNodeVisualization((ElseStatementNode) node);
        } else {
            throw new IllegalArgumentException("");
        }
    }

    private String expressionNodeVisualization(ExpressionNode expressionNode) {
        StringBuilder builder = new StringBuilder();
        if (expressionNode instanceof AssigmentExpressionNode) {
            AssigmentExpressionNode assigmentExpressionNode = (AssigmentExpressionNode) expressionNode;

            ExpressionNode left = assigmentExpressionNode.getLeft();
            ExpressionNode right = assigmentExpressionNode.getRight();

            builder.append("{");
            builder.append(expressionNodeVisualization(left));
            builder.append("|");
            builder.append("=");
            builder.append("|");
            builder.append(expressionNodeVisualization(right));
            builder.append("}");
        } else if (expressionNode instanceof ConditionalExpressionNode) {
            ConditionalExpressionNode conditionalExpressionNode = (ConditionalExpressionNode) expressionNode;

            ExpressionNode cond = conditionalExpressionNode.getConditionNode();
            ExpressionNode then = conditionalExpressionNode.getThenNode();
            ExpressionNode els = conditionalExpressionNode.getElseNode();

            builder.append("{ COND_EXPR | {");
            builder.append(expressionNodeVisualization(cond));
            builder.append("|");
            builder.append(expressionNodeVisualization(then));
            builder.append("|");
            builder.append(expressionNodeVisualization(els));
            builder.append("}}");
        } else if (expressionNode instanceof AdditiveExpressionNode) {
            AdditiveExpressionNode additiveExpressionNode = (AdditiveExpressionNode) expressionNode;

            ExpressionNode left = additiveExpressionNode.getLeft();
            ExpressionNode right = additiveExpressionNode.getRight();

            builder.append("{");
            builder.append(expressionNodeVisualization(left));
            builder.append("|");
            builder.append(additiveExpressionNode.getType().toString());
            builder.append("|");
            builder.append(expressionNodeVisualization(right));
            builder.append("}");
        } else if (expressionNode instanceof MultiplicativeExpressionNode) {
            MultiplicativeExpressionNode multiplicativeExpressionNode = (MultiplicativeExpressionNode) expressionNode;

            ExpressionNode left = multiplicativeExpressionNode.getLeft();
            ExpressionNode right = multiplicativeExpressionNode.getRight();

            builder.append("{");
            builder.append(expressionNodeVisualization(left));
            builder.append("|");
            builder.append(multiplicativeExpressionNode.getType().toString());
            builder.append("|");
            builder.append(expressionNodeVisualization(right));
            builder.append("}");
        } else if (expressionNode instanceof LogicalAndExpressionNode) {
            LogicalAndExpressionNode logicalAndExpressionNode = (LogicalAndExpressionNode) expressionNode;

            ExpressionNode left = logicalAndExpressionNode.getLeft();
            ExpressionNode right = logicalAndExpressionNode.getRight();

            builder.append("{");
            builder.append(expressionNodeVisualization(left));
            builder.append("|");
            builder.append("AND");
            builder.append("|");
            builder.append(expressionNodeVisualization(right));
            builder.append("}");
        } else if (expressionNode instanceof LogicalOrExpressionNode) {
            LogicalOrExpressionNode logicalOrExpressionNode = (LogicalOrExpressionNode) expressionNode;

            ExpressionNode left = logicalOrExpressionNode.getLeft();
            ExpressionNode right = logicalOrExpressionNode.getRight();

            builder.append("{");
            builder.append(expressionNodeVisualization(left));
            builder.append("|");
            builder.append("OR");
            builder.append("|");
            builder.append(expressionNodeVisualization(right));
            builder.append("}");
        } else if (expressionNode instanceof RelationalExpressionNode) {
            RelationalExpressionNode relationalExpressionNode = (RelationalExpressionNode) expressionNode;

            ExpressionNode left = relationalExpressionNode.getLeft();
            ExpressionNode right = relationalExpressionNode.getRight();

            builder.append("{");
            builder.append(expressionNodeVisualization(left));
            builder.append("|");
            builder.append(relationalExpressionNode.getType().toString());
            builder.append("|");
            builder.append(expressionNodeVisualization(right));
            builder.append("}");
        } else if (expressionNode instanceof EqualityExpressionNode) {
            EqualityExpressionNode equalityExpressionNode = (EqualityExpressionNode) expressionNode;

            ExpressionNode left = equalityExpressionNode.getLeft();
            ExpressionNode right = equalityExpressionNode.getRight();

            builder.append("{");
            builder.append(expressionNodeVisualization(left));
            builder.append("|");
            builder.append(equalityExpressionNode.getType().toString());
            builder.append("|");
            builder.append(expressionNodeVisualization(right));
            builder.append("}");
        } else if (expressionNode instanceof VariableExpressionNode) {
            VariableExpressionNode variableExpressionNode = (VariableExpressionNode) expressionNode;
            String name = ((VariableExpressionNode) expressionNode).getIdentifierNode().getName();
            builder.append(name);
        } else if (expressionNode instanceof BoolConstantExpressionNode) {
            BoolConstantExpressionNode boolConstantExpressionNode = (BoolConstantExpressionNode) expressionNode;
            builder.append(String.valueOf(boolConstantExpressionNode.getValue()));
        } else if (expressionNode instanceof IntConstantExpressionNode) {
            IntConstantExpressionNode intConstantExpressionNode = (IntConstantExpressionNode) expressionNode;
            builder.append(String.valueOf(intConstantExpressionNode.getValue()));
        } else if (expressionNode instanceof FloatConstantExpressionNode) {
            FloatConstantExpressionNode floatConstantExpressionNode = (FloatConstantExpressionNode) expressionNode;
            builder.append(String.valueOf(floatConstantExpressionNode.getValue()));
        } else if (expressionNode instanceof NullConstantExpressionNode) {
            NullConstantExpressionNode nullConstantExpressionNode = (NullConstantExpressionNode) expressionNode;
            builder.append("null");
        } else if (expressionNode instanceof ArrayConstructorExpressionNode) {
            ArrayConstructorExpressionNode arrayConstructorExpressionNode =
                    (ArrayConstructorExpressionNode) expressionNode;
            ExpressionNode sizeExpression = arrayConstructorExpressionNode.getSizeExpression();
            builder.append("{ARRAY_CONSTRUCT|" + typeNodeVisualization(arrayConstructorExpressionNode.getTypeNode())
                    + "|" + expressionNodeVisualization(sizeExpression) + " }");
        } else if (expressionNode instanceof FunctionCallExpressionNode) {
            FunctionCallExpressionNode functionCallExpressionNode = (FunctionCallExpressionNode) expressionNode;

            ExpressionNode function = functionCallExpressionNode.getFunction();

            builder.append("{");
            builder.append(expressionNodeVisualization(function));
            builder.append("|{");
            builder.append(functionCallExpressionNode.getParameters().getList()
                    .stream()
                    .map(this::expressionNodeVisualization)
                    .collect(Collectors.joining("|")));
            builder.append("}}");
        } else if (expressionNode instanceof ArrayAccessExpressionNode) {
            ArrayAccessExpressionNode arrayAccessExpressionNode = (ArrayAccessExpressionNode) expressionNode;

            ExpressionNode array = arrayAccessExpressionNode.getArray();
            ExpressionNode argument = arrayAccessExpressionNode.getArgument();

            builder.append("{");
            builder.append(expressionNodeVisualization(array));
            builder.append("|");
            builder.append("ARRAY_ACCESS");
            builder.append("|");
            builder.append(expressionNodeVisualization(argument));
            builder.append("}");
        } else if (expressionNode instanceof FieldAccessExpressionNode) {
            FieldAccessExpressionNode fieldAccessExpressionNode = (FieldAccessExpressionNode) expressionNode;

            ExpressionNode left = fieldAccessExpressionNode.getLeft();
            ExpressionNode right = fieldAccessExpressionNode.getRight();

            builder.append("{");
            builder.append(expressionNodeVisualization(left));
            builder.append("|");
            builder.append("FIELD_ACCESS");
            builder.append("|");
            builder.append(expressionNodeVisualization(right));
            builder.append("}");
        } else if (expressionNode instanceof PostfixDecrementSubtractionExpressionNode) {
            PostfixDecrementSubtractionExpressionNode postfixDecrementSubtractionExpressionNode =
                    (PostfixDecrementSubtractionExpressionNode) expressionNode;

            ExpressionNode node = postfixDecrementSubtractionExpressionNode.getExpressionNode();
            builder.append("{ POST_DEC | " + expressionNodeVisualization(node) + " }");
        } else if (expressionNode instanceof PostfixIncrementAdditiveExpressionNode) {
            PostfixIncrementAdditiveExpressionNode postfixIncrementAdditiveExpressionNode =
                    (PostfixIncrementAdditiveExpressionNode) expressionNode;

            ExpressionNode node = postfixIncrementAdditiveExpressionNode.getExpressionNode();
            builder.append("{ POST_INC | " + expressionNodeVisualization(node) + " }");
        } else if (expressionNode instanceof PostfixIncrementMultiplicativeExpressionNode) {
            PostfixIncrementMultiplicativeExpressionNode postfixIncrementMultiplicativeExpressionNode =
                    (PostfixIncrementMultiplicativeExpressionNode) expressionNode;

            ExpressionNode node = postfixIncrementMultiplicativeExpressionNode.getExpressionNode();
            builder.append("{ POST_MUL | " + expressionNodeVisualization(node) + " }");
        } else if (expressionNode instanceof PrefixDecrementSubtractionExpressionNode) {
            PrefixDecrementSubtractionExpressionNode prefixDecrementSubtractionExpressionNode =
                    (PrefixDecrementSubtractionExpressionNode) expressionNode;

            ExpressionNode node = prefixDecrementSubtractionExpressionNode.getExpressionNode();
            builder.append("{ PRE_DEC | " + expressionNodeVisualization(node) + " }");
        } else if (expressionNode instanceof PrefixIncrementAdditiveExpressionNode) {
            PrefixIncrementAdditiveExpressionNode prefixIncrementAdditiveExpressionNode =
                    (PrefixIncrementAdditiveExpressionNode) expressionNode;

            ExpressionNode node = prefixIncrementAdditiveExpressionNode.getExpressionNode();
            builder.append("{ PRE_INC | " + expressionNodeVisualization(node) + " }");
        } else if (expressionNode instanceof PrefixIncrementMultiplicativeExpressionNode) {
            PrefixIncrementMultiplicativeExpressionNode prefixIncrementMultiplicativeExpressionNode =
                    (PrefixIncrementMultiplicativeExpressionNode) expressionNode;

            ExpressionNode node = prefixIncrementMultiplicativeExpressionNode.getExpressionNode();
            builder.append("{ PRE_MUL | " + expressionNodeVisualization(node) + " }");
        } else if (expressionNode instanceof CastExpressionNode) {
            CastExpressionNode castExpressionNode = (CastExpressionNode) expressionNode;

            ExpressionNode node = castExpressionNode.getExpressionNode();
            builder.append("{ CAST | " + expressionNodeVisualization(node) + " }");
        } else {
            throw new IllegalArgumentException("");
        }

        return builder.toString();
    }

    private String constructorNodeVisualization(ConstructorDefinitionNode node) {
        StringBuilder builder = new StringBuilder();
        count++;
        nodeToField.put(node, currentStruct + ":f" + count);

        nodeToNode.put(node, node.getClassStatementNode());

        builder
                .append("{<f" + count + "> constructor")
                .append("| {")
                .append(statementNodeVisualization(node.getStatementNode()))
                .append("}")
                .append("}");
        return builder.toString();
    }

    private String interfaceNodeVisualization(InterfaceStatementNode node) {
        StringBuilder builder = new StringBuilder();
        count++;
        nodeToField.put(node, currentStruct + ":f" + count);

        node.getExtendNodes()
                .forEach(c -> nodeToNode.put(c, node));

        builder
                .append("{{<f" + count + "> interface ")
                .append(node.getIdentifierNode().getName())
                .append("|{ ")
                .append("extends |{")
                .append(node.getExtendNodes().stream()
                        .map(i -> {
                            if (i instanceof ClassStatementNode) {
                                return ((ClassStatementNode) i).getIdentifierNode().getName();
                            } else if (i instanceof InterfaceStatementNode) {
                                return ((InterfaceStatementNode) i).getIdentifierNode().getName();
                            } else {
                                throw new IllegalArgumentException("");
                            }
                        })
                        .collect(Collectors.joining("|")))
                .append("}|")
                .append("statements |{")
                .append(node.getTranslationNode().getStatements().stream()
                        .map(this::statementNodeVisualization)
                        .collect(Collectors.joining("|")))
                .append("}}}}");
        return builder.toString();
    }

    private String ifElseNodeVisualization(IfElseStatementNode node) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("{ ifelse")
                .append("| {")
                .append(statementNodeVisualization(node.getIfStatementNode()))
                .append(node.getElifStatementNodes().stream()
                        .map(this::statementNodeVisualization)
                        .map(s -> "|" + s)
                        .collect(Collectors.joining()))
                .append(node.getElifStatementNodes() != null
                        ? ""
                        : ("|" + statementNodeVisualization(node.getElseStatementNode())))
                .append("}")
                .append("}");
        return builder.toString();
    }

    private String whileNodeVisualization(WhileStatementNode node) {
        StringBuilder builder = new StringBuilder();
        count++;
        nodeToField.put(node, currentStruct + ":f" + count);
        builder
                .append("{<f" + count + "> while")
                .append("| {")
                .append(expressionNodeVisualization(node.getConditionNode()))
                .append("|")
                .append(statementNodeVisualization(node.getBodyNode()))
                .append("}")
                .append("}");
        return builder.toString();
    }

    private String breakNodeVisualization(BreakStatementNode node) {
        StringBuilder builder = new StringBuilder();
        count++;
        nodeToField.put(node, currentStruct + ":f" + count);
        nodeToNode.put(node, node.getCycle());

        builder
                .append("{<f" + count + "> break }");
        return builder.toString();
    }

    private String continueNodeVisualization(ContinueStatementNode node) {
        StringBuilder builder = new StringBuilder();
        count++;
        nodeToField.put(node, currentStruct + ":f" + count);
        builder
                .append("{ <f" + count + "> continue }");
        return builder.toString();
    }

    private String returnNodeVisualization(ReturnStatementNode node) {
        StringBuilder builder = new StringBuilder();
        count++;
        nodeToField.put(node, currentStruct + ":f" + count);
        builder.append("{<f" + count + "> return");

        nodeToNode.put(node, node.getFunctionDefinitionNode());

        if (node.getExpressionNode() != null) {
            builder.append("| {")
                    .append(expressionNodeVisualization(node.getExpressionNode()))
                    .append("}");
        }

        builder.append("}");

        return builder.toString();
    }

    private String ifNodeVisualization(IfStatementNode node) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("{ if")
                .append("| {")
                .append(expressionNodeVisualization(node.getConditionNode()))
                .append("|")
                .append(statementNodeVisualization(node.getThenNode()))
                .append("}")
                .append("}");
        return builder.toString();
    }

    private String elifNodeVisualization(ElifStatementNode node) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("{ elif")
                .append("| {")
                .append(expressionNodeVisualization(node.getConditionNode()))
                .append("|")
                .append(statementNodeVisualization(node.getElseNode()))
                .append("}")
                .append("}");
        return builder.toString();
    }

    private String elseNodeVisualization(ElseStatementNode node) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("{else")
                .append("| {")
                .append(statementNodeVisualization(node.getElseNode()))
                .append("}")
                .append("}");
        return builder.toString();
    }

    private String compoundNodeVisualization(CompoundStatementNode node) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("{{")
                .append(node.getStatements().stream()
                        .map(this::statementNodeVisualization)
                        .collect(Collectors.joining("|")))
                .append("}}");
        return builder.toString();
    }

    private String declarationNodeVisualisation(DeclarationStatementNode node) {
        StringBuilder builder = new StringBuilder();

        builder
                .append("{declaration | {")
                .append(typeNodeVisualization(node.getTypeNode()))
                .append("|")
                .append(node.getIdentifierNode().getName());

        if (node.getExpressionNode() != null) {
            builder
                    .append("|" + expressionNodeVisualization(node.getExpressionNode()));
        }

        builder
                .append("}}");

        return builder.toString();
    }

    private String classNodeVisualization(ClassStatementNode node) {
        StringBuilder builder = new StringBuilder();
        count++;
        nodeToField.put(node, currentStruct + ":f" + count);

        node.getExtendNodes().forEach(c -> nodeToNode.put(c, node));

        builder
                .append("{{<f"+count+"> class ")
                .append(node.getIdentifierNode().getName())
                .append("|{ ")
                .append("extends |{")
                .append(node.getExtendNodes().stream()
                        .map(i -> {
                            if (i instanceof ClassStatementNode) {
                                return ((ClassStatementNode) i).getIdentifierNode().getName();
                            } else if (i instanceof InterfaceStatementNode) {
                                return ((InterfaceStatementNode) i).getIdentifierNode().getName();
                            }
                            return "";
                        })
                        .collect(Collectors.joining("|")))
                .append("}|")
                .append("statements |{")
                .append(node.getTranslationNode().getStatements().stream()
                        .map(this::statementNodeVisualization)
                        .collect(Collectors.joining("|")))
                .append("}}}}");
        return builder.toString();
    }

    private String functionDefinitionNodeVisualization(FunctionDefinitionNode node) {
        StringBuilder builder = new StringBuilder();
        count++;
        nodeToField.put(node, currentStruct + ":f" + count);

        builder
                .append("{<f"+count+"> function definition ")
                .append("|{ ")
                .append(node.getIdentifierNode().getName())
                .append("|")
                .append(typeNodeVisualization(node.getFunctionNode()))
                .append(node.getStatementNode() == null
                        ? ""
                        : ("|" + statementNodeVisualization(node.getStatementNode())))
                .append("}}");
        return builder.toString();
    }

    private String typeNodeVisualization(TypeNode typeNode) {
        StringBuilder builder = new StringBuilder();

        if (typeNode instanceof BasicTypeNode) {
            BasicTypeNode basicTypeNode = (BasicTypeNode) typeNode;
            builder.append(basicTypeNode.getType().toString());
        } else if (typeNode instanceof FunctionNode) {
            FunctionNode functionNode = (FunctionNode) typeNode;

            builder.append("{");

            if (!functionNode.getParametersNode().getParameters().isEmpty()) {
                builder.append("{");
                builder.append(functionNode.getParametersNode().getParameters().stream()
                        .map(parameterNode -> {
                            return "{" + parameterNode.getIdentifierNode().getName() + "| {" +
                                    typeNodeVisualization(parameterNode.getTypeNode()) + "}}";
                        })
                        .collect(Collectors.joining("|")));
                builder.append("}|");
            }

            builder.append(typeNodeVisualization(functionNode.getTypeNode()));
            builder.append("}");
        } else if (typeNode instanceof ObjectTypeNode) {
            ObjectTypeNode objectTypeNode = (ObjectTypeNode) typeNode;
            count++;
            nodeToField.put(objectTypeNode, currentStruct + ":" + count);
            nodeToNode.put(objectTypeNode, objectTypeNode.getDefinitionNode());
            builder.append("<f" + count + "> " + objectTypeNode.getIdentifierNode().getName());
        } else if (typeNode instanceof ArrayTypeNode) {
            ArrayTypeNode arrayTypeNode = (ArrayTypeNode) typeNode;
            builder.append("{ array | " + typeNodeVisualization(arrayTypeNode.getTypeNode()) + "}");
        }

        return builder.toString();
    }
}
