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
import java.util.function.Supplier;

public class Interpreter implements StatementVisitor<Void>, ExpressionVisitor<JocksValue> {

    public Interpreter() {
        // Type checking
        _symbolTable.createVariable("is_nil", new IsType<>("is_nil", JocksNil.class));
        _symbolTable.createVariable("is_bool", new IsType<>("is_bool", JocksBool.class));
        _symbolTable.createVariable("is_number", new IsType<>("is_number", JocksNumber.class));
        _symbolTable.createVariable("is_string", new IsType<>("is_string", JocksString.class));
        _symbolTable.createVariable("is_instance", new IsType<>("is_instance", JocksInstance.class));
        _symbolTable.createVariable("is_function", new IsType<>("is_function", JocksFunction.class));
        _symbolTable.createVariable("is_class", new IsType<>("is_class", JocksClass.class));

        // Maths
        _symbolTable.createVariable("abs", new Abs());
        _symbolTable.createVariable("floor", new Floor());
        _symbolTable.createVariable("pow", new Pow());

        // Strings
        _symbolTable.createVariable("to_string", new ToString());

        // Global Object class which all other classes are descendants of.
        _symbolTable.createVariable(
                "Object",
                new JocksClass("Object", null, new HashMap<>()));
    }

    public void visitAll(List<Statement> statements) {
        for (final var statement : statements) {
            visit(statement);
            if (_isReturning || _isThrowing) {
                break;
            }
        }
    }

    @Override
    public Void visit(Statement statement) {
        return statement.accept(this);
    }

    @Override
    public Void visitClassDeclaration(ClassDeclaration statement) {
        final var superClassIdentifier = statement.getSuperClass().isPresent()
                ? statement.getSuperClass().get().getText()
                : "Object";
        final var superClass = JocksValue.cast(_symbolTable.getVariable(superClassIdentifier), JocksClass.class)
                .orElseThrow(() -> _exceptionFactory.createExceptionWithFileAndLine(
                        statement.getIdentifier().getFile(),
                        statement.getIdentifier().getLine(),
                        "The identifier '%s' is not a class and can not be derived from.",
                        superClassIdentifier));

        pushSymbolTable();
        _symbolTable.createVariable(
                "super",
                _symbolTable.getVariable(superClass.getIdentifier()));

        final var methods = new HashMap<String, JocksFunction>();
        for (final var methodDeclaration : statement.getMethods()) {
            final var methodName = methodDeclaration.getIdentifier().getText();
            methods.put(
                    methodName,
                    funDeclarationToJocksFunction(
                            statement.getIdentifier().getText() + "." + methodName,
                            methodDeclaration));
        }

        popSymbolTable();

        final var identifier = statement.getIdentifier().getText();
        _symbolTable.createVariable(
                identifier,
                new JocksClass(identifier, superClass, methods));

        return null;
    }

    @Override
    public Void visitFunDeclaration(FunDeclaration statement) {
        _symbolTable.createVariable(
                statement.getIdentifier().getText(),
                funDeclarationToJocksFunction(
                        statement.getIdentifier().getText(),
                        statement));

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
        final var conditionResult = JocksValue.cast(visit(statement.getCondition()), JocksBool.class)
                .orElseThrow(() -> _exceptionFactory.createExceptionWithoutFileOrLine(
                        "If/else statement condition did not evaluate to type 'bool'."));
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
        final Supplier<JocksBool> evaluateCondition = () ->
                JocksValue.cast(visit(statement.getCondition()), JocksBool.class)
                        .orElseThrow(() -> _exceptionFactory.createExceptionWithoutFileOrLine(
                                "While statement condition did not evaluate to type 'bool'."));

        while (evaluateCondition.get() == JocksBool.Truthy) {
            visit(statement.getSubStatement());
            if (_isReturning || _isThrowing) {
                break;
            }
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
            return JocksValue.cast(visit(condition.get()), JocksBool.class)
                    .orElseThrow(() -> _exceptionFactory.createExceptionWithoutFileOrLine(
                            "For statement condition did not evaluate to type 'bool'."));
        };

        pushSymbolTable();
        if (statement.getInitializer().isPresent()) {
            visit(statement.getInitializer().get());
        }
        while (evaluateCondition.get() == JocksBool.Truthy) {
            visit(statement.getSubStatement());
            if (_isReturning || _isThrowing) {
                break;
            }
            if (statement.getIncrement().isPresent()) {
                visit(statement.getIncrement().get());
            }
        }
        popSymbolTable();

        return null;
    }

    @Override
    public Void visitTryCatchStatement(TryCatchStatement statement) {
        visit(statement.getTryStatement());

        if (_isThrowing) {
            pushSymbolTable();
            _symbolTable.createVariable(statement.getExceptionIdentifier().getText(), _thrownValue);
            _thrownValue = JocksNil.Instance;
            _isThrowing  = false;
            visit(statement.getCatchStatement());
            popSymbolTable();
        }

        return null;
    }

    @Override
    public Void visitThrowStatement(ThrowStatement statement) {
        final var thrownValue = visit(statement.getSubExpression());
        // It's possible that the sub-expression might throw itself.
        // In that case, the existing thrown value takes priority.
        if (_isThrowing) {
            return null;
        }

        _thrownValue = thrownValue;
        _isThrowing  = true;

        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement statement) {
        pushSymbolTable();
        visitAll(statement.getSubStatements());
        popSymbolTable();

        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement statement) {
        final var returnValue = statement.getSubExpression().isPresent()
                ? visit(statement.getSubExpression().get())
                : JocksNil.Instance;
        if (_isThrowing) {
            return null;
        }

        _returnValue = returnValue;
        _isReturning = true;

        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement statement) {
        final var subExpressionResult = visit(statement.getSubExpression());
        if (_isThrowing) {
            return null;
        }

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
            default -> throw _exceptionFactory.createExceptionWithFileAndLine(
                    operator.getFile(),
                    operator.getLine(),
                    "Invalid logical operator type '" + operator.getType().name() + "'.");
        };

        final var lftSubExpressionResult = JocksValue.cast(visit(expression.getLftSubExpression()), JocksBool.class)
                .orElseThrow(() -> _exceptionFactory.createExceptionWithFileAndLine(
                        operator.getFile(),
                        operator.getLine(),
                        "Left sub expression of '%s' expression did not evaluate to type 'bool.",
                        expression.getOperator().getText()));

        if (_isThrowing) {
            return JocksNil.Instance;
        }

        if (lftSubExpressionResult == shortCircuitValue) {
            return lftSubExpressionResult;
        }

        final var rgtSubExpressionResult = JocksValue.cast(visit(expression.getRgtSubExpression()), JocksBool.class)
                .orElseThrow(() -> _exceptionFactory.createExceptionWithFileAndLine(
                        operator.getFile(),
                        operator.getLine(),
                        "Right sub expression of '%s' expression did not evaluate to type 'bool.",
                        expression.getOperator().getText()));

        if (_isThrowing) {
            return JocksNil.Instance;
        }

        return rgtSubExpressionResult;
    }

    @Override
    public JocksValue visitBinaryExpression(BinaryExpression expression) {
        final var operator = expression.getOperator();
        final var lftSubExpressionResult = visit(expression.getLftSubExpression());
        if (_isThrowing) {
            return JocksNil.Instance;
        }
        final var rgtSubExpressionResult = visit(expression.getRgtSubExpression());
        if (_isThrowing) {
            return JocksNil.Instance;
        }
        try {
            // If a user defined operator overload is being called, then update the call stack
            // entry info list so any errors triggered within will have good diagnostics
            // messages for debugging.
            if (lftSubExpressionResult instanceof JocksInstance instance) {
                final var methodName = String.format(
                        "%s.%s",
                        instance.getJClass().getIdentifier(),
                        JocksInstance.binaryOperatorTypeToMethodString(operator.getType()));
                pushCallStackEntryInfo(methodName, operator.getFile(), operator.getLine());
            }
            final var result = switch (operator.getType()) {
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
                default -> throw _exceptionFactory.createExceptionWithFileAndLine(
                        operator.getFile(),
                        operator.getLine(),
                        "Invalid binary operator type '" + operator.getType().name() + "'.");
            };
            if (lftSubExpressionResult instanceof JocksInstance) {
                popCallStackEntryInfo();
            }
            return _isThrowing
                    ? JocksNil.Instance
                    : result;
        } catch (UnsupportedOperationException ex) {
            // Add localization and re-throw.
            throw _exceptionFactory.createExceptionWithFileAndLine(operator.getFile(), operator.getLine(), ex.getMessage());
        }
    }

    @Override
    public JocksValue visitUnaryExpression(UnaryExpression expression) {
        final var operator = expression.getOperator();
        final var subExpressionResult = visit(expression.getSubExpression());
        if (_isThrowing) {
            return JocksNil.Instance;
        }
        try {
            // If a user defined operator overload is being called, then update the call stack
            // entry info list so any errors triggered within will have good diagnostics
            // messages for debugging.
            if (subExpressionResult instanceof JocksInstance instance) {
                final var methodName = String.format(
                        "%s.%s",
                        instance.getJClass().getIdentifier(),
                        JocksInstance.unaryOperatorTypeToMethodString(operator.getType()));
                pushCallStackEntryInfo(methodName, operator.getFile(), operator.getLine());
            }
            final var result = switch (operator.getType()) {
                case TokenType.BANGS -> subExpressionResult.not();
                case TokenType.ADD -> subExpressionResult.add();
                case TokenType.SUB -> subExpressionResult.sub();
                default -> throw _exceptionFactory.createExceptionWithFileAndLine(
                        operator.getFile(),
                        operator.getLine(),
                        "Invalid unary operator type '" + operator.getType().name() + "'.");
            };
            if (subExpressionResult instanceof JocksInstance) {
                popCallStackEntryInfo();
            }
            return _isThrowing
                    ? JocksNil.Instance
                    : result;
        } catch (UnsupportedOperationException ex) {
            // Add localization and re-throw.
            throw _exceptionFactory.createExceptionWithFileAndLine(operator.getFile(), operator.getLine(), ex.getMessage());
        }
    }

    @Override
    public JocksValue visitGroupingExpression(GroupingExpression expression) {
        return visit(expression.getSubExpression());
    }

    @Override
    public JocksValue visitDotExpression(DotExpression expression) {
        final var lhsExpressionResult = visit(expression.getLhsExpression());
        final var rhsIdentifier = expression.getRhsIdentifier();

        if (_isThrowing) {
            return JocksNil.Instance;
        }

        if ((lhsExpressionResult instanceof JocksInstance instance)) {
            // Return the property if it exists, otherwise return the method if it exists.
            return instance
                    .getProperty(rhsIdentifier.getText())
                    .or(() -> instance.getMethod(rhsIdentifier.getText()))
                    .orElseThrow(() -> _exceptionFactory.createExceptionWithFileAndLine(
                            rhsIdentifier.getFile(),
                            rhsIdentifier.getLine(),
                            "Couldn't find property or method '" + rhsIdentifier.getText() + "' on instance."));
        }

        if ((lhsExpressionResult instanceof JocksClass jClass)) {
            // Return the method if it exists.
            return jClass.getMethodRecursive(rhsIdentifier.getText())
                    .orElseThrow(() -> _exceptionFactory.createExceptionWithFileAndLine(
                            rhsIdentifier.getFile(),
                            rhsIdentifier.getLine(),
                            "Couldn't find method '%s' on class '%s'.",
                            rhsIdentifier.getText(),
                            jClass.getIdentifier()));
        }

        throw _exceptionFactory.createExceptionWithFileAndLine(
                rhsIdentifier.getFile(),
                rhsIdentifier.getLine(),
                "Left sub expression of '.' expression did not evaluate to an instance or class.");
    }

    @Override
    public JocksValue visitFunInvocation(FunInvocation expression) {
        final var subExpressionResult = visit(expression.getSubExpression());
        if (_isThrowing) {
            return JocksNil.Instance;
        }

        final var invoked = JocksValue.cast(subExpressionResult, JocksFunction.class)
                .orElseThrow(() -> _exceptionFactory.createExceptionWithFileAndLine(
                        expression.getFile(),
                        expression.getLine(),
                        "Sub expression did not evaluate to a function which can be invoked."));

        final var funcArity = invoked.getArity();
        final var exprArity = expression.getArguments().size();
        if (funcArity != exprArity) {
            throw _exceptionFactory.createExceptionWithFileAndLine(
                    expression.getFile(),
                    expression.getLine(),
                    "Number of parameters (%d) did not match what was expected (%d).",
                    funcArity,
                    exprArity);
        }

        final var argumentResults = new ArrayList<JocksValue>();
        for (final var argumentExpression : expression.getArguments()) {
            argumentResults.add(visit(argumentExpression));
            if (_isThrowing) {
                return JocksNil.Instance;
            }
        }

        pushCallStackEntryInfo(invoked.getName(), expression.getFile(), expression.getLine());
        final var result = invoked.call(argumentResults);
        popCallStackEntryInfo();

        return _isThrowing
                ? JocksNil.Instance
                : result;
    }

    @Override
    public JocksValue visitNewInvocation(NewInvocation expression) {
        final var invoked = JocksValue.cast(
                _symbolTable
                        .getAncestor(expression.getSymbolTableDepth())
                        .getVariable(expression.getIdentifier().getText()),
                JocksClass.class)
                .orElseThrow(() -> _exceptionFactory.createExceptionWithFileAndLine(
                        expression.getFile(),
                        expression.getLine(),
                        "The identifier '%s' is not a class from which a new instance can be instantiated.",
                        expression.getIdentifier().getText()));

        final var instance = invoked.createInstance();

        final var initMethod = invoked.getMethod("__init__").orElseThrow();
        final var initArity  = initMethod.getArity();
        final var exprArity  = expression.getArguments().size() + 1; // Leading instance parameter - implicitly passed.
        if (initArity != exprArity) {
            throw _exceptionFactory.createExceptionWithFileAndLine(
                    expression.getFile(),
                    expression.getLine(),
                    "Number of parameters (%d) did not match what was expected (%d).",
                    initArity,
                    exprArity);
        }

        final var argumentResults = new ArrayList<JocksValue>();
        argumentResults.add(instance);
        for (final var argumentExpression : expression.getArguments()) {
            argumentResults.add(visit(argumentExpression));
            if (_isThrowing) {
                return JocksNil.Instance;
            }
        }

        pushCallStackEntryInfo(initMethod.getName(), expression.getFile(), expression.getLine());
        initMethod.call(argumentResults);
        popCallStackEntryInfo();

        return _isThrowing
                ? JocksNil.Instance
                : instance;
    }

    @Override
    public JocksValue visitVarAssignment(VarAssignment expression) {
        final var rhsResult = visit(expression.getRhsExpression());
        if (_isThrowing) {
            return JocksNil.Instance;
        }
        if (expression.getLhsExpression() instanceof DotExpression lhsDotExpression) {
            final var lhsResult = visit(lhsDotExpression.getLhsExpression());
            if (_isThrowing) {
                return JocksNil.Instance;
            }
            final var instance = JocksValue.cast(lhsResult, JocksInstance.class)
                    .orElseThrow(() -> _exceptionFactory.createExceptionWithFileAndLine(
                            lhsDotExpression.getRhsIdentifier().getFile(),
                            lhsDotExpression.getRhsIdentifier().getLine(),
                            "The left sub expression did not evaluate to an instance during '.' assignment expression."));
            final var property = lhsDotExpression.getRhsIdentifier().getText();
            instance.setProperty(property, rhsResult);
        } else if (expression.getLhsExpression() instanceof VarExpression lhsVarExpression) {
            _symbolTable
                    .getAncestor(lhsVarExpression.getSymbolTableDepth())
                    .setVariable(lhsVarExpression.getIdentifier().getText(), rhsResult);
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
        final var literalToken = expression.getToken();
        return switch (literalToken.getType()) {
            case TokenType.STRING -> new JocksString((String)expression.getToken().getLiteral());
            case TokenType.NUMBER -> new JocksNumber((Double)expression.getToken().getLiteral());
            case TokenType.TRUE  -> JocksBool.Truthy;
            case TokenType.FALSE -> JocksBool.Falsey;
            case TokenType.NIL -> JocksNil.Instance;
            default -> throw _exceptionFactory.createExceptionWithFileAndLine(
                    literalToken.getFile(),
                    literalToken.getLine(),
                    "Invalid literal type '" + literalToken.getType().name() + "'.");
        };
    }

    public JocksValue executeUserLandFunction(JocksUserLandFunction function, List<JocksValue> arguments) {
        final var oldSymbolTable = _symbolTable;
        @SuppressWarnings("UnnecessaryLocalVariable") // I prefer the consistency afforded below by this.
        final var newSymbolTable = new SymbolTable(function.getSymbolTable(), _exceptionFactory);

        _symbolTable = newSymbolTable;
        _returnValue = JocksNil.Instance;
        _isReturning = false;

        for (int i = 0; i < function.getParameters().size(); i++) {
            _symbolTable.createVariable(function.getParameters().get(i), arguments.get(i));
        }

        visitAll(function.getStatements());
        final var result = _returnValue;

        _symbolTable = oldSymbolTable;
        _returnValue = JocksNil.Instance;
        _isReturning = false;

        return result;
    }

    private void pushCallStackEntryInfo(String name, String line, int file) {
        _callStackEntryInfo.addLast(String.format("%s at %s:%d", name, line, file));
    }

    private void popCallStackEntryInfo() {
        _callStackEntryInfo.removeLast();
    }

    private JocksFunction funDeclarationToJocksFunction(String functionName, FunDeclaration statement) {
        return new JocksUserLandFunction(
                functionName,
                statement.getParameters().stream().map(Token::getText).toList(),
                statement.getStatements(),
                _symbolTable,
                this);
    }

    private void pushSymbolTable() {
        _symbolTable = new SymbolTable(_symbolTable, _exceptionFactory);
    }

    private void popSymbolTable() {
        _symbolTable = _symbolTable.getParent();
    }

    private final List<String> _callStackEntryInfo = new ArrayList<>();
    private final ExceptionFactory _exceptionFactory = new ExceptionFactory(() -> _callStackEntryInfo);
    private SymbolTable _symbolTable = new SymbolTable(null, _exceptionFactory);
    private JocksValue _returnValue = JocksNil.Instance;
    private boolean _isReturning = false;
    private JocksValue _thrownValue = JocksNil.Instance;
    private boolean _isThrowing = false;
}
