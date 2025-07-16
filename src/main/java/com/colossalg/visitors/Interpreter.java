package com.colossalg.visitors;

import com.colossalg.Token;
import com.colossalg.TokenType;
import com.colossalg.builtin.functions.*;
import com.colossalg.builtin.functions.maths.*;
import com.colossalg.dataTypes.*;
import com.colossalg.dataTypes.classes.*;
import com.colossalg.dataTypes.functions.*;
import com.colossalg.dataTypes.primitives.*;
import com.colossalg.expression.*;
import com.colossalg.statement.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class Interpreter implements StatementVisitor<Void>, ExpressionVisitor<JocksValue> {

    public static IllegalStateException createException(String module, String file, int line, String what) {
        return new IllegalStateException(String.format("[%s] - (%s:%d) - %s.", module, file, line, what));
    }

    public Interpreter() {
        // Type checking
        registerInternalVariable("isNil", new IsType<>(JocksNil.class));
        registerInternalVariable("isBool", new IsType<>(JocksBool.class));
        registerInternalVariable("isNumber", new IsType<>(JocksNumber.class));
        registerInternalVariable("isString", new IsType<>(JocksString.class));
        registerInternalVariable("isInstance", new IsType<>(JocksInstance.class));
        registerInternalVariable("isFunction", new IsType<>(JocksFunction.class));
        registerInternalVariable("isClass", new IsType<>(JocksClass.class));

        // Maths
        registerInternalVariable("floor", new Floor());
        registerInternalVariable("pow", new Pow());

        // Global Object class which all other classes are descendants of.
        // TODO - I need to have a little bit more of a think here.
        //        I've made all classes and store their super classes as a string.
        //        This simplifies some things a bit:
        //            - It means all classes are guaranteed to have a super class.
        //              The root "Object" class can refer to itself trivially. It
        //              would be possible if an object reference were used instead,
        //              but an additional setter would be required for the super
        //              class as I don't think it's possible to point to itself
        //              in the constructor (without some hacky check for "Object").
        //            - Resolving of the classes becomes trivial.
        //        There are some significant drawbacks, however:
        //            - Classes become dynamically scoped. This is partially
        //              resolved by restricting them to being declared at the
        //              global scope, but that itself is a trade-off.
        //            - It's going to be slower to look the super classes up by
        //              name rather than accessing them through an object reference.
        registerInternalVariable(
                "Object",
                new JocksClass(
                        createInternalIdentifier("Object"),
                        createInternalIdentifier("Object"),
                        new HashMap<>()));
    }

    public void visitAll(List<Statement> statements) {
        for (final var statement : statements) {
            visit(statement);
        }
    }

    @Override
    public Void visit(Statement statement) {
        return statement.accept(this);
    }

    @Override
    public Void visitClassDeclaration(ClassDeclaration statement) {
        final var identifier = statement.getIdentifier();
        final var superClass = statement.getSuperClass()
                .orElse(createInternalIdentifier("Object"));

        pushSymbolTable();
        _symbolTable.createVariable(
                createInternalIdentifier("super"),
                _symbolTable.getVariable(superClass));

        final var methods = new HashMap<String, JocksFunction>();
        for (final var methodDeclaration : statement.getMethods()) {
            methods.put(
                    methodDeclaration.getIdentifier().getText(),
                    funDeclarationToJocksFunction(methodDeclaration));
        }

        popSymbolTable();

        _symbolTable.createVariable(
                identifier,
                new JocksClass(identifier, superClass, methods));

        return null;
    }

    @Override
    public Void visitFunDeclaration(FunDeclaration statement) {
        _symbolTable.createVariable(
                statement.getIdentifier(),
                funDeclarationToJocksFunction(statement));

        return null;
    }

    @Override
    public Void visitVarDeclaration(VarDeclaration statement) {
        _symbolTable.createVariable(
                statement.getIdentifier(),
                visit(statement.getExpression()));

        return null;
    }

    @Override
    public Void visitIfElseStatement(IfElseStatement statement) {
        final var conditionResult = JocksValue.cast(
                visit(statement.getCondition()),
                JocksBool.class);
        if (conditionResult == JocksBool.Truthy) {
            visit(statement.getThenSubStatement());
        } else if (statement.getElseSubStatement().isPresent()) {
            visit(statement.getElseSubStatement().get());
        }

        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement statement) {
        // Helper lambda to evaluate condition, checking that type is JocksBool, etc.
        final Supplier<JocksBool> evaluateCondition = () -> JocksValue.cast(
                visit(statement.getCondition()),
                JocksBool.class);

        while (evaluateCondition.get() == JocksBool.Truthy) {
            visit(statement.getSubStatement());
        }

        return null;
    }

    @Override
    public Void visitForStatement(ForStatement statement) {
        // Helper lambda to evaluate condition, checking that type is JocksBool, etc.
        final Supplier<JocksBool> evaluateCondition = () -> {
            final var condition = statement.getCondition();
            if (condition.isEmpty()) {
                return JocksBool.Truthy;
            }
            return JocksValue.cast(
                    visit(condition.get()),
                    JocksBool.class);
        };

        pushSymbolTable();
        if (statement.getInitializer().isPresent()) {
            visit(statement.getInitializer().get());
        }
        while (evaluateCondition.get() == JocksBool.Truthy) {
            visit(statement.getSubStatement());
            if (statement.getIncrement().isPresent()) {
                visit(statement.getIncrement().get());
            }
        }
        popSymbolTable();

        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement statement) {
        pushSymbolTable();
        for (final var subStatement : statement.getSubStatements()) {
            visit(subStatement);
        }
        popSymbolTable();

        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement statement) {
        // TODO - This currently doesn't support return statements which are
        //        a child of another statement (if / while / for / block).
        throw createException(
                "Interpreter",
                statement.getFile(),
                statement.getLine(),
                "Can't directly evaluate return statement");
    }

    @Override
    public Void visitPrintStatement(PrintStatement statement) {
        final var subExpressionResult = visit(statement.getSubExpression());
        System.out.println(subExpressionResult.str());
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        visit(statement.getSubExpression());
        return null;
    }

    @Override
    public JocksValue visit(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public JocksValue visitLogicalExpression(LogicalExpression expression) {
        final var operator = expression.getOperator();
        final var shortCircuitValue = switch (operator.getType()) {
            case TokenType.AND -> JocksBool.Falsey;
            case TokenType.OR  -> JocksBool.Truthy;
            default -> throw createException(
                    "Interpreter",
                    operator.getFile(),
                    operator.getLine(),
                    "Invalid logical operator type '" + operator.getType().name() + "'");
        };

        final var lftSubExpressionResult = JocksValue.cast(
                visit(expression.getLftSubExpression()),
                JocksBool.class);

        if (lftSubExpressionResult == shortCircuitValue) {
            return lftSubExpressionResult;
        }

        return JocksValue.cast(
                visit(expression.getRgtSubExpression()),
                JocksBool.class);
    }

    @Override
    public JocksValue visitBinaryExpression(BinaryExpression expression) {
        final var operator = expression.getOperator();
        final var lftSubExpressionResult = visit(expression.getLftSubExpression());
        final var rgtSubExpressionResult = visit(expression.getRgtSubExpression());
        return switch (operator.getType()) {
            case TokenType.EQUAL_EQUAL -> lftSubExpressionResult.equal(rgtSubExpressionResult);
            case TokenType.BANGS_EQUAL -> lftSubExpressionResult.notEqual(rgtSubExpressionResult);
            case TokenType.LESS_THAN -> lftSubExpressionResult.lessThan(rgtSubExpressionResult);
            case TokenType.LESS_THAN_OR_EQUAL -> lftSubExpressionResult.lessThanOrEqual(rgtSubExpressionResult);
            case TokenType.MORE_THAN -> lftSubExpressionResult.moreThan(rgtSubExpressionResult);
            case TokenType.MORE_THAN_OR_EQUAL -> lftSubExpressionResult.moreThanOrEqual(rgtSubExpressionResult);
            case TokenType.ADD -> lftSubExpressionResult.add(rgtSubExpressionResult);
            case TokenType.SUB -> lftSubExpressionResult.sub(rgtSubExpressionResult);
            case TokenType.MUL -> lftSubExpressionResult.mul(rgtSubExpressionResult);
            case TokenType.DIV -> lftSubExpressionResult.div(rgtSubExpressionResult);
            default -> throw createException(
                    "Interpreter",
                    operator.getFile(),
                    operator.getLine(),
                    "Invalid binary operator type '" + operator.getType().name() + "'");
        };
    }

    @Override
    public JocksValue visitUnaryExpression(UnaryExpression expression) {
        final var operator = expression.getOperator();
        final var subExpressionResult = visit(expression.getSubExpression());
        return switch (operator.getType()) {
            case TokenType.BANGS -> subExpressionResult.not();
            case TokenType.ADD -> subExpressionResult.add();
            case TokenType.SUB -> subExpressionResult.sub();
            default -> throw createException(
                    "Interpreter",
                    operator.getFile(),
                    operator.getLine(),
                    "Invalid unary operator type '" + operator.getType().name() + "'");
        };
    }

    @Override
    public JocksValue visitGroupingExpression(GroupingExpression expression) {
        return visit(expression.getSubExpression());
    }

    @Override
    public JocksValue visitDotExpression(DotExpression expression) {
        final var lhsExpressionResult = visit(expression.getLhsExpression());
        final var rhsIdentifier = expression.getRhsIdentifier();

        if ((lhsExpressionResult instanceof JocksInstance instance)) {
            return findPropertyOrMethod(instance, rhsIdentifier.getText())
                    .orElseThrow(() -> createException(
                            "Interpreter",
                            rhsIdentifier.getFile(),
                            rhsIdentifier.getLine(),
                            "Couldn't find property or method '" + rhsIdentifier.getText() + "' on instance"));
        }

        if ((lhsExpressionResult instanceof JocksClass jClass)) {
            return findMethod(jClass, rhsIdentifier.getText())
                    .orElseThrow(() -> createException(
                            "Interpreter",
                            rhsIdentifier.getFile(),
                            rhsIdentifier.getLine(),
                            "Couldn't find property or method '" + rhsIdentifier.getText() + "' on class"));
        }

        throw createException(
                "Interpreter",
                rhsIdentifier.getFile(),
                rhsIdentifier.getLine(),
                "Expression referenced did not evaluate to an instance or class");
    }

    @Override
    public JocksValue visitFunInvocation(FunInvocation expression) {
        final var invoked = JocksValue.cast(
                visit(expression.getSubExpression()),
                JocksFunction.class);

        final var funcArity = invoked.getArity();
        final var exprArity = expression.getArguments().size();
        if (funcArity != exprArity) {
            throw createException(
                    "Interpreter",
                    expression.getFile(),
                    expression.getLine(),
                    String.format("Number of parameters (%d) did not match what was expected (%d)", funcArity, exprArity));
        }

        final var argumentResults = new ArrayList<JocksValue>();
        for (final var argumentExpression : expression.getArguments()) {
            argumentResults.add(visit(argumentExpression));
        }

        return invoked.call(argumentResults);
    }

    @Override
    public JocksValue visitNewInvocation(NewInvocation expression) {
        final var invoked = JocksValue.cast(
                _symbolTable
                        .getAncestor(expression.getSymbolTableDepth())
                        .getVariable(expression.getIdentifier()),
                JocksClass.class);

        final var instance = invoked.createInstance();

        final var initMethod = invoked.getMethod("__init__").orElseThrow();
        final var initArity  = initMethod.getArity();
        final var exprArity  = expression.getArguments().size() + 1; // Leading instance parameter - implicitly passed.
        if (initArity != exprArity) {
            throw createException(
                    "Interpreter",
                    expression.getFile(),
                    expression.getLine(),
                    String.format("Number of parameters (%d) did not match what was expected (%d)", initArity, exprArity));
        }

        final var argumentResults = new ArrayList<JocksValue>();
        argumentResults.add(instance);
        for (final var argumentExpression : expression.getArguments()) {
            argumentResults.add(visit(argumentExpression));
        }

        initMethod.call(argumentResults);

        return instance;
    }

    @Override
    public JocksValue visitVarAssignment(VarAssignment expression) {
        final var rhsResult = visit(expression.getRhsExpression());
        if (expression.getLhsExpression() instanceof DotExpression lhsDotExpression) {
            final var instance = JocksValue.cast(
                    visit(lhsDotExpression.getLhsExpression()),
                    JocksInstance.class);
            final var property = lhsDotExpression.getRhsIdentifier().getText();
            instance.setProperty(property, rhsResult);
        } else if (expression.getLhsExpression() instanceof VarExpression lhsVarExpression) {
            _symbolTable
                    .getAncestor(lhsVarExpression.getSymbolTableDepth())
                    .setVariable(lhsVarExpression.getIdentifier(), rhsResult);
        }

        return rhsResult;
    }

    @Override
    public JocksValue visitVarExpression(VarExpression expression) {
        return _symbolTable
                .getAncestor(expression.getSymbolTableDepth())
                .getVariable(expression.getIdentifier());
    }

    @Override
    public JocksValue visitLiteralExpression(LiteralExpression expression) {
        final var literalToken = expression.getToken();
        return switch (literalToken.getType()) {
            case TokenType.STRING -> new JocksString((String)expression.getToken().getLiteral());
            case TokenType.NUMBER -> new JocksNumber((Double)expression.getToken().getLiteral());
            case TokenType.TRUE  -> JocksBool.Truthy;
            case TokenType.FALSE -> JocksBool.Falsey;
            case TokenType.NIL -> JocksNil.Instance;
            default -> throw createException(
                    "Interpreter",
                    literalToken.getFile(),
                    literalToken.getLine(),
                    "Invalid literal type '" + literalToken.getType().name() + "'");
        };
    }

    public SymbolTable getSymbolTable() {
        return _symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        _symbolTable = symbolTable;
    }

    private static Token createInternalIdentifier(String identifier) {
        // TODO - Revisit this mechanism.
        //        I like keeping the SymbolTable interface clean, but making tokens here
        //        instead of during parse time feels wrong. Perhaps what I need to do is
        //        have the builtin functions and classes return a token for their identifier.
        return new Token(TokenType.IDENTIFIER, null, identifier, "", -1);
    }

    private void registerInternalVariable(String identifier, JocksValue value) {
        _symbolTable.createVariable(createInternalIdentifier(identifier), value);
    }

    private JocksFunction funDeclarationToJocksFunction(FunDeclaration statement) {
        return new JocksUserLandFunction(
                statement.getParameters(),
                statement.getStatements(),
                _symbolTable,
                this);
    }

    private Optional<JocksValue> findPropertyOrMethod(JocksInstance instance, String propertyOrMethodName) {
        final var property = instance.getProperty(propertyOrMethodName);
        return property.or(() -> findMethod(instance, propertyOrMethodName));
    }

    private Optional<JocksFunction> findMethod(JocksInstance instance, String methodName) {
        return findMethod(instance.getClassName(), methodName)
                .map((method) -> new BoundMethod(instance, method));
    }

    private Optional<JocksFunction> findMethod(JocksClass jClass, String methodName) {
        return findMethod(jClass.getIdentifier(), methodName);
    }

    private Optional<JocksFunction> findMethod(Token className, String methodName) {
        final var jClass = JocksValue.cast(_symbolTable.getVariable(className), JocksClass.class);
        return jClass.getIdentifier().equals(jClass.getSuperClass())
                ? jClass.getMethod(methodName)
                : jClass.getMethod(methodName)
                    .or(() -> findMethod(jClass.getSuperClass(), methodName));
    }

    private void pushSymbolTable() {
        _symbolTable = new SymbolTable(_symbolTable);
    }

    private void popSymbolTable() {
        _symbolTable = _symbolTable.getParent();
    }

    private SymbolTable _symbolTable = new SymbolTable(null);
}
