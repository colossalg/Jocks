package com.colossalg.visitors;

import com.colossalg.expression.*;
import com.colossalg.statement.*;

public class PrettyPrinter implements StatementVisitor<String>, ExpressionVisitor<String> {

    @Override
    public String visit(Statement statement) {
        return statement.accept(this);
    }

    @Override
    public String visitClassDeclaration(ClassDeclaration statement) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(getIndentationString());
        stringBuilder.append("class ");
        stringBuilder.append(statement.getIdentifier().getText());
        if (statement.getSuperClass().isPresent()) {
            stringBuilder.append(" < ");
            stringBuilder.append(statement.getSuperClass().get().getText());
        }
        stringBuilder.append('\n');
        stringBuilder.append(getIndentationString());
        stringBuilder.append("{\n");
        stringBuilder.append('\n');
        incrementIndentation();
        for (final var method : statement.getMethods()) {
            stringBuilder.append(visit(method));
        }
        decrementIndentation();
        stringBuilder.append("}\n");
        stringBuilder.append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String visitFunDeclaration(FunDeclaration statement) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(getIndentationString());
        stringBuilder.append("fun ");
        stringBuilder.append(statement.getIdentifier().getText());
        stringBuilder.append('(');
        final var parameters = statement.getParameters();
        if (!parameters.isEmpty()) {
            stringBuilder.append(parameters.getFirst().getText());
            for (int i = 1; i < parameters.size(); i++) {
                stringBuilder.append(", ");
                stringBuilder.append(parameters.get(i).getText());
            }
        }
        stringBuilder.append(")\n");
        stringBuilder.append(getIndentationString());
        stringBuilder.append("{\n");
        incrementIndentation();
        for (final var subStatement : statement.getStatements()) {
            stringBuilder.append(visit(subStatement));
        }
        decrementIndentation();
        stringBuilder.append(getIndentationString());
        stringBuilder.append("}\n");
        stringBuilder.append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String visitVarDeclaration(VarDeclaration statement) {
        return getIndentationString() +
                String.format(
                        "var %s = %s;\n",
                        statement.getIdentifier().getText(),
                        visit(statement.getExpression()));
    }

    @Override
    public String visitIfElseStatement(IfElseStatement statement) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(getIndentationString());
        stringBuilder.append("if (");
        stringBuilder.append(visit(statement.getCondition()));
        stringBuilder.append(")\n");
        incrementIndentation();
        stringBuilder.append(visit(statement.getThenSubStatement()));
        decrementIndentation();
        if (statement.getElseSubStatement().isPresent()) {
            stringBuilder.append("else\n");
            incrementIndentation();
            stringBuilder.append(visit(statement.getElseSubStatement().get()));
            decrementIndentation();
        }
        return stringBuilder.toString();
    }

    @Override
    public String visitWhileStatement(WhileStatement statement) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(getIndentationString());
        stringBuilder.append("while (");
        stringBuilder.append(visit(statement.getCondition()));
        stringBuilder.append(")\n");
        incrementIndentation();
        stringBuilder.append(visit(statement.getSubStatement()));
        decrementIndentation();
        return stringBuilder.toString();
    }

    @Override
    public String visitForStatement(ForStatement statement) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(getIndentationString());
        stringBuilder.append("for (");
        if (statement.getInitializer().isPresent()) {
            stringBuilder.append(visit(statement.getInitializer().get()).trim()); // Already has trailing ';'
        } else {
            stringBuilder.append(';');
        }
        stringBuilder.append(' ');
        if (statement.getCondition().isPresent()) {
            stringBuilder.append(visit(statement.getCondition().get()));
        }
        stringBuilder.append("; ");
        if (statement.getIncrement().isPresent()) {
            stringBuilder.append(visit(statement.getIncrement().get()));
        }
        stringBuilder.append(")\n");
        incrementIndentation();
        stringBuilder.append(visit(statement.getSubStatement()));
        decrementIndentation();
        return stringBuilder.toString();
    }

    @Override
    public String visitBlockStatement(BlockStatement statement) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(getIndentationString());
        stringBuilder.append("{\n");
        incrementIndentation();
        for (final var subStatement : statement.getSubStatements()) {
            stringBuilder.append(visit(subStatement));
        }
        decrementIndentation();
        stringBuilder.append(getIndentationString());
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    @Override
    public String visitReturnStatement(ReturnStatement statement) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(getIndentationString());
        stringBuilder.append("return");
        if (statement.getSubExpression().isPresent()) {
            stringBuilder.append(' ');
            stringBuilder.append(visit(statement.getSubExpression().get()));
        }
        stringBuilder.append(";\n");
        return stringBuilder.toString();
    }

    @Override
    public String visitPrintStatement(PrintStatement statement) {
        return getIndentationString() +
                String.format(
                        "print %s;\n",
                        visit(statement.getSubExpression()));
    }

    @Override
    public String visitExpressionStatement(ExpressionStatement statement) {
        return getIndentationString() +
                String.format(
                        "%s;\n",
                        visit(statement.getSubExpression()));
    }

    @Override
    public String visit(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public String visitLogicalExpression(LogicalExpression expression) {
        return String.format(
                "%s %s %s",
                visit(expression.getLftSubExpression()),
                expression.getOperator().getText(),
                visit(expression.getRgtSubExpression()));
    }

    @Override
    public String visitBinaryExpression(BinaryExpression expression) {
        return String.format(
                "%s %s %s",
                visit(expression.getLftSubExpression()),
                expression.getOperator().getText(),
                visit(expression.getRgtSubExpression()));
    }

    @Override
    public String visitUnaryExpression(UnaryExpression expression) {
        return String.format(
                "%s%s",
                expression.getOperator().getText(),
                visit(expression.getSubExpression()));
    }

    @Override
    public String visitGroupingExpression(GroupingExpression expression) {
        return String.format("(%s)", visit(expression.getSubExpression()));
    }

    @Override
    public String visitDotExpression(DotExpression expression) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(visit(expression.getLhsExpression()));
        stringBuilder.append('.');
        stringBuilder.append(expression.getRhsIdentifier().getText());
        return stringBuilder.toString();
    }

    @Override
    public String visitFunInvocation(FunInvocation expression) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(visit(expression.getSubExpression()));
        stringBuilder.append('(');
        final var arguments = expression.getArguments();
        if (!arguments.isEmpty()) {
            stringBuilder.append(visit(arguments.getFirst()));
            for (int i = 1; i < arguments.size(); i++) {
                stringBuilder.append(", ");
                stringBuilder.append(visit(arguments.get(i)));
            }
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    @Override
    public String visitNewInvocation(NewInvocation expression) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append("new ");
        stringBuilder.append(expression.getIdentifier().getText());
        stringBuilder.append('(');
        final var arguments = expression.getArguments();
        if (!arguments.isEmpty()) {
            stringBuilder.append(visit(arguments.getFirst()));
            for (int i = 1; i < arguments.size(); i++) {
                stringBuilder.append(", ");
                stringBuilder.append(visit(arguments.get(i)));
            }
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    @Override
    public String visitVarAssignment(VarAssignment expression) {
        return String.format(
                "%s = %s",
                visit(expression.getLhsExpression()),
                visit(expression.getRhsExpression()));
    }

    @Override
    public String visitVarExpression(VarExpression expression) {
        return expression.getIdentifier().getText();
    }

    @Override
    public String visitLiteralExpression(LiteralExpression expression) {
        return expression.getToken().getText();
    }

    private void incrementIndentation() {
        _indentation++;
    }

    private void decrementIndentation() {
        _indentation--;
    }

    private String getIndentationString() {
        return " ".repeat(_indentation * 2);
    }

    private int _indentation = 0;
}
