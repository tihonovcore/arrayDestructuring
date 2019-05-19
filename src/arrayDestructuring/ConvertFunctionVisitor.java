package arrayDestructuring;

import jdk.nashorn.internal.ir.*;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;

import java.util.*;

class ConvertFunctionVisitor extends NodeVisitor<LexicalContext> {
    /**
     * Constructor
     *
     * @param lc a custom lexical context
     */
    ConvertFunctionVisitor(LexicalContext lc) {
        super(lc);
    }

    private StringBuilder result = new StringBuilder();
    private StringBuilder indent = new StringBuilder();

    private Deque<BlockDefinition> blocks = new ArrayDeque<>();

    class Pair implements Comparable<Pair> {
        String name;
        int index;

        Pair(String name, int index) {
            this.name = name;
            this.index = index;
        }

        @Override
        public String toString() {
            return "{" + name + ", " + index + "}";
        }

        @Override
        public int compareTo(Pair other) {
            return index - other.index;
        }
    }

    StringBuilder getString() {
        return result;
    }

    private void append(String... s) {
        for (String value : s) {
            if (!blocks.isEmpty()) {
                if (blocks.getFirst().beforeFirstDefinition) {
                    blocks.getFirst().before.append(value);
                } else {
                    blocks.getFirst().after.append(value);
                }
            } else {
                result.append(value);
            }
        }
    }

    @Override
    public boolean enterFunctionNode(FunctionNode functionNode) {
        if (functionNode.getName().equals(":program")) {
            functionNode.getBody().accept(this);
            return false;
        }

        append(indent.toString(), "function ", functionNode.getName(), "(");

        List<IdentNode> parameters = functionNode.getParameters();
        for (int i = 0; i < parameters.size() - 1; i++) {
            append(parameters.get(i).getName());
            append(", ");
        }
        if (parameters.size() != 0) append(getLast(parameters).getName());

        append(") ");

        functionNode.getBody().accept(this);

        return false;
    }

    @Override
    public boolean enterLiteralNode(LiteralNode<?> literalNode) {
        if (literalNode.isNumeric()) {
            append(literalNode.getString());
        } else if (literalNode.isString()) {
            append("\"", literalNode.getString(), "\"");
        } else if (literalNode.isArray()) {
            append("[");
            List<Expression> list = literalNode.getElementExpressions();
            for (int i = 0; i < list.size() - 1; i++) {
                list.get(i).accept(this);
                append(", ");
            }
            if (list.size() != 0) getLast(list).accept(this);
            append("]");
        }
        return false;
    }

    @Override
    public boolean enterIndexNode(IndexNode indexNode) {
        append(indexNode.getBase().toString(false));
        append("[");
        indexNode.getIndex().accept(this);
        append("]");
        return false;
    }

    @Override
    public boolean enterBlock(Block block) {
        append("{\n");
        indent.append("    ");
        blocks.addFirst(new BlockDefinition());
        return true;
    }

    @Override
    public Node leaveBlock(Block block) {
        BlockDefinition current = Objects.requireNonNull(blocks.pollFirst());

        append(current.before.toString());
        append(destructuring(current.variables));
        if (!current.variables.isEmpty()) append("\n");
        append(current.after.toString());

        if (indent.length() > 0) {
            indent.delete(indent.length() - 4, indent.length());
        }
        append(indent.toString());
        append("}\n");
        return block;
    }

    private String destructuring(Map<String, List<Pair>> variables) {
        StringBuilder result = new StringBuilder();
        for (String key : variables.keySet()) {
            List<Pair> vars = variables.get(key);
            Collections.sort(vars);
            result.append(indent);
            result.append("var ");
            result.append("[");

            StringBuilder appendLater = new StringBuilder();

            int index = 0;
            for (int i = 0; i < vars.size(); i++) {
                Pair pair = vars.get(i);
                if (index == pair.index) {
                    result.append(pair.name);
                    if (i + 1 != vars.size()) result.append(", ");
                } else if (index < pair.index) {
                    result.append(", ");
                    i--;
                } else if (index > pair.index) {
                    appendLater.append(indent).append("var [");
                    appendLater.append(", ".repeat(Math.max(0, pair.index)));
                    appendLater.append(pair.name).append("] = ").append(key).append("\n");
                }
                index++;
            }
            result.append("] = ");
            result.append(key).append(";");
            result.append("\n").append(appendLater);
        }

        return result.toString();
    }

    @Override
    public boolean enterExpressionStatement(ExpressionStatement expressionStatement) {
        append(indent.toString());
        expressionStatement.getExpression().accept(this);
        append(";\n");
        return false;
    }

    @Override
    public boolean enterReturnNode(ReturnNode returnNode) {
        append(indent.toString(), "return ");
        returnNode.getExpression().accept(this);
        append(";\n");
        return false;
    }

    @Override
    public boolean enterUnaryNode(UnaryNode unaryNode) {
        append(unaryNode.tokenType() + "");
        unaryNode.getExpression().accept(this);
        return false;
    }

    @Override
    public boolean enterVarNode(VarNode varNode) {
        String name = varNode.getName().getName();
        if (varNode.getAssignmentSource() instanceof IndexNode) {
            IndexNode indexNode = (IndexNode) varNode.getAssignmentSource();
            String array = indexNode.getBase().toString(false);

            BlockDefinition current = blocks.getFirst();
            if (!current.variables.containsKey(array)) {
                current.variables.put(array, new ArrayList<>());
            }

            current.beforeFirstDefinition = false;
            current.variables.get(array).add(new Pair(name, Integer.parseInt(indexNode.getIndex().toString(false))));
        } else {
            if (!varNode.isFunctionDeclaration()) {
                append(indent.toString(), "var ");
                append(name, " = ");
            }
            varNode.getAssignmentSource().accept(this);

            if (!(varNode.getAssignmentSource() instanceof FunctionNode)) {
                append(";\n");
            }
        }
        return false;
    }

    @Override
    public boolean enterBinaryNode(BinaryNode binaryNode) {
        binaryNode.lhs().accept(this);
        append(" ", binaryNode.tokenType().toString(), " ");
        binaryNode.rhs().accept(this);
        return false;
    }

    @Override
    public boolean enterIdentNode(IdentNode identNode) {
        append(identNode.getName());
        return false;
    }

    @Override
    public boolean enterCallNode(CallNode callNode) {
        append(callNode.getFunction().toString(false));
        append("(");

        List<Expression> args = callNode.getArgs();
        for (int i = 0; i < args.size() - 1; i++) {
            args.get(i).accept(this);
            append(", ");
        }
        if (args.size() != 0) getLast(args).accept(this);

        append(")");
        return false;
    }

    private <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }
}
