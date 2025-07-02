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
import java.util.Optional;
import java.util.function.Supplier;

public class Interpreter implements StatementVisitor<Void>, ExpressionVisitor<JocksValue> {

    public Interpreter() {
        // Type checking
        _symbolTable.createVariable("isNil", new IsType<>(JocksNil.class));
        _symbolTable.createVariable("isBool", new IsType<>(JocksBool.class));
        _symbolTable.createVariable("isNumber", new IsType<>(JocksNumber.class));
        _symbolTable.createVariable("isString", new IsType<>(JocksString.class));
        _symbolTable.createVariable("isInstance", new IsType<>(JocksInstance.class));
        _symbolTable.createVariable("isFunction", new IsType<>(JocksFunction.class));
        _symbolTable.createVariable("isClass", new IsType<>(JocksClass.class));

        // Maths
        _symbolTable.createVariable("floor", new Floor());
        _symbolTable.createVariable("pow", new Pow());

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
        _symbolTable.createVariable(
                "Object",
                new JocksClass("Object", "Object", new HashMap<>()));
    }

    @Override
    public Void visit(Statement statement) {
        return statement.accept(this);
    }

    @Override
    public Void visitClassDeclaration(ClassDeclaration statement) {
        final var identifier = statement.getIdentifier().getText();
        final var superClass = statement.getSuperClass().isPresent()
                ? statement.getSuperClass().get().getText()
                : "Object";

        pushSymbolTable();
        _symbolTable.createVariable(
                "super",
                _symbolTable.getVariable(superClass));

        final var methods = new HashMap<String, JocksFunction>();
        for (final var methodDeclaration : statement.getMethods()) {
            methods.put(
                    methodDeclaration.getIdentifier().getText(),
                    funDeclarationToJocksFunction(methodDeclaration));
        }

        popSymbolTable();

        _symbolTable.createVariable(
                statement.getIdentifier().getText(),
                new JocksClass(
                        identifier,
                        superClass,
                        methods));

        return null;
    }

    @Override
    public Void visitFunDeclaration(FunDeclaration statement) {
        _symbolTable.createVariable(
                statement.getIdentifier().getText(),
                funDeclarationToJocksFunction(statement));

        return null;
    }

    @Override
    public Void visitVarDeclaration(VarDeclaration statement) {
        _symbolTable.createVariable(
                statement.getIdentifier().getText(),
                visit(statement.getExpression()));

        return null;
    }

    @Override
    public Void visitIfElseStatement(IfElseStatement statement) {
        final var conditionResult = JocksValue.cast(visit(statement.getCondition()), JocksBool.class);
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
        final Supplier<JocksBool> evaluateCondition = () -> {
            return JocksValue.cast(visit(statement.getCondition()), JocksBool.class);
        };

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
            return JocksValue.cast(visit(condition.get()), JocksBool.class);
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
        // TODO - Need to have a think about this.
        //        Is it a bit ugly for the function invocation to evaluate the return statement itself?
        //        It does simplify the control flow, as if we do it there we can just pop right out the
        //        function, whereas we lack some context if we do the evaluation here in another method.
        //        On the other hand, I want to support try/catch/finally blocks eventually and that will
        //        require a way to jump around in the call stack, so maybe the mechanisms used there can
        //        be shared.
        // TODO - Need to suss out how to actually handle run time errors.
        //        Need a way to throw/catch errors within the language itself.
        throw new UnsupportedOperationException("Can't directly evaluate return statement.");
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
        final var operatorType = expression.getOperator().getType();
        final var shortCircuitValue = switch (operatorType) {
            case TokenType.AND -> JocksBool.Falsey;
            case TokenType.OR  -> JocksBool.Truthy;
            default -> throw new IllegalStateException("Invalid logical operator type '" + operatorType.name() + "'.");
        };

        final var lftSubExpressionResult = JocksValue.cast(visit(expression.getLftSubExpression()), JocksBool.class);
        if (lftSubExpressionResult == shortCircuitValue) {
            return lftSubExpressionResult;
        }

        return JocksValue.cast(visit(expression.getRgtSubExpression()), JocksBool.class);
    }

    @Override
    public JocksValue visitBinaryExpression(BinaryExpression expression) {
        final var operatorType = expression.getOperator().getType();
        final var lftSubExpressionResult = visit(expression.getLftSubExpression());
        final var rgtSubExpressionResult = visit(expression.getRgtSubExpression());
        return switch (operatorType) {
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
            default -> throw new IllegalStateException("Invalid binary operator type '" + operatorType.name() + "'.");
        };
    }

    @Override
    public JocksValue visitUnaryExpression(UnaryExpression expression) {
        final var subExpressionResult = visit(expression.getSubExpression());
        final var operatorType = expression.getOperator().getType();
        return switch (operatorType) {
            case TokenType.BANGS -> subExpressionResult.not();
            case TokenType.ADD -> subExpressionResult.add();
            case TokenType.SUB -> subExpressionResult.sub();
            default -> throw new IllegalStateException("Invalid unary operator type '" + operatorType.name() + "'.");
        };
    }

    @Override
    public JocksValue visitGroupingExpression(GroupingExpression expression) {
        return visit(expression.getSubExpression());
    }

    @Override
    public JocksValue visitDotExpression(DotExpression expression) {
        final var lhsExpressionResult = visit(expression.getLhsExpression());
        if ((lhsExpressionResult instanceof JocksInstance instance)) {
            var property = instance.getProperty(expression.getRhsIdentifier().getText());
            if (property.isPresent()) {
                return property.get();
            } else {
                JocksClass currClass = null;
                JocksClass nextClass = JocksValue.cast(_symbolTable.getVariable(instance.getClassName()), JocksClass.class);
                while (currClass != nextClass) {
                    currClass = nextClass;
                    nextClass = JocksValue.cast(_symbolTable.getVariable(currClass.getSuperClass()), JocksClass.class);
                    final var method = currClass.getMethod(expression.getRhsIdentifier().getText());
                    if (method.isPresent()) {
                        return new BoundMethod(instance, method.get());
                    }
                }
            }
        } else if ((lhsExpressionResult instanceof JocksClass jclass)) {
            JocksClass currClass = null;
            JocksClass nextClass = jclass;
            while (currClass != nextClass) {
                currClass = nextClass;
                nextClass = JocksValue.cast(_symbolTable.getVariable(currClass.getSuperClass()), JocksClass.class);
                final var method = currClass.getMethod(expression.getRhsIdentifier().getText());
                if (method.isPresent()) {
                    return method.get();
                }
            }
        } else {
            // TODO - Need to suss out how to actually handle run time errors.
            //        Need a way to throw/catch errors within the language itself.
            throw new IllegalStateException("Expression referenced did not evaluate to an instance or class.");
        }

        // TODO - Need to suss out how to actually handle run time errors.
        //        Need a way to throw/catch errors within the language itself.
        throw new IllegalStateException("Some shit went wrong");
    }

    @Override
    public JocksValue visitFunInvocation(FunInvocation expression) {
        final var invoked = JocksValue.cast(
                visit(expression.getSubExpression()),
                JocksFunction.class);

        if (invoked.getArity() != expression.getArguments().size()) {
            // TODO - Need to suss out how to actually handle run time errors.
            //        Need a way to throw/catch errors within the language itself.
            throw new IllegalStateException("Number of parameters does not match number of arguments provided.");
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
                        .getVariable(expression.getIdentifier().getText()),
                JocksClass.class);

        final var instance = invoked.createInstance();

        final var initMethod = invoked.getMethod("__init__").orElseThrow();
        if (initMethod.getArity() != expression.getArguments().size() + 1) {
            // TODO - Need to suss out how to actually handle run time errors.
            //        Need a way to throw/catch errors within the language itself.
            throw new IllegalStateException("Number of parameters does not match number of arguments provided.");
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
            final var instance = JocksValue.cast(visit(lhsDotExpression.getLhsExpression()), JocksInstance.class);
            final var property = lhsDotExpression.getRhsIdentifier().getText();
            instance.setProperty(
                    property,
                    rhsResult);
        } else if (expression.getLhsExpression() instanceof VarExpression lhsVarExpression) {
            final var identifier = lhsVarExpression.getIdentifier().getText();
            _symbolTable
                    .getAncestor(expression.getSymbolTableDepth())
                    .setVariable(identifier, rhsResult);
        }

        return rhsResult;
    }

    @Override
    public JocksValue visitVarExpression(VarExpression expression) {
        return _symbolTable
                .getAncestor(expression.getSymbolTableDepth())
                .getVariable(expression.getIdentifier().getText());
    }

    @Override
    public JocksValue visitLiteralExpression(LiteralExpression expression) {
        final var literalType = expression.getToken().getType();
        return switch (literalType) {
            case TokenType.STRING -> new JocksString((String)expression.getToken().getLiteral());
            case TokenType.NUMBER -> new JocksNumber((Double)expression.getToken().getLiteral());
            case TokenType.TRUE  -> JocksBool.Truthy;
            case TokenType.FALSE -> JocksBool.Falsey;
            case TokenType.NIL -> JocksNil.Instance;
            default -> throw new IllegalStateException("Invalid literal type '" + literalType.name() + "'.");
        };
    }

    public SymbolTable getSymbolTable() {
        return _symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        _symbolTable = symbolTable;
    }

    private JocksFunction funDeclarationToJocksFunction(FunDeclaration statement) {
        return new JocksUserLandFunction(
                statement.getParameters().stream().map(Token::getText).toList(),
                statement.getStatements(),
                _symbolTable,
                this);
    }

    private void pushSymbolTable() {
        _symbolTable = new SymbolTable(_symbolTable);
    }

    private void popSymbolTable() {
        _symbolTable = _symbolTable.getParent();
    }

    private SymbolTable _symbolTable = new SymbolTable(null);
}
