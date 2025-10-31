package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        variableTypes.add(new HashMap<>()); // Global scope
        checkStylesheet(ast.root);

    }


    private void checkStylesheet(Stylesheet sheet) {
        for (ASTNode child : sheet.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                checkStyleRule((Stylerule) child);
            }
        }
    }

    private void checkStyleRule(Stylerule rule) {
        variableTypes.add(new HashMap<>());
        for (ASTNode child : rule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
            if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            }
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }
        }
        variableTypes.removeLast();
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        Expression expression = variableAssignment.expression;
        ExpressionType type = getExpressionType(expression);

        if (type == ExpressionType.UNDEFINED) {
            variableAssignment.setError("Cannot determine type of expression for variable " + variableAssignment.name.name);
            return;
        }

        if (expression instanceof VariableReference) {
            VariableReference ref = (VariableReference) expression;
            if (getVariableType(ref.name) == ExpressionType.UNDEFINED) {
                variableAssignment.setError("Variable " + ref.name + " is not defined.");
                return;
            }
        }

        HashMap<String, ExpressionType> currentScope = variableTypes.peekLast();
        // Kijk of een varianele al gedclareerd is
        if (currentScope.containsKey(variableAssignment.name.name)) {
            variableAssignment.setError("Variable " + variableAssignment.name.name + " is already declared in this scope.");
        } else {
            currentScope.put(variableAssignment.name.name, type);
        }
    }

    private void checkIfClause(IfClause ifClause) {
        Expression condition = (Expression) ifClause.getChildren().get(0);

        if (condition instanceof VariableReference) {
            VariableReference varRef = (VariableReference) condition;
            ExpressionType type = getVariableType(varRef.name);
            if (type == ExpressionType.UNDEFINED) {
                ifClause.setError("Variable '" + varRef.name + "' is not defined.");
            } else if (type != ExpressionType.BOOL) {
                ifClause.setError("If-statement condition must be of type boolean, but variable '" + varRef.name + "' is of type " + type);
            }
        } else if (!(condition instanceof BoolLiteral)) {
            ifClause.setError("If-statement condition must be a boolean literal or a boolean variable.");
        }

    }

    private ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (expression instanceof VariableReference) {
            return getVariableType(((VariableReference) expression).name);
        } else if (expression instanceof Operation) {
            Operation op = (Operation) expression;
            ExpressionType leftType = getExpressionType(op.left);
            ExpressionType rightType = getExpressionType(op.right);

            if (op instanceof AddOperation || op instanceof SubtractOperation) {
                if (leftType == ExpressionType.PIXEL && rightType == ExpressionType.PIXEL) {
                    return ExpressionType.PIXEL;
                }
                if (leftType == ExpressionType.PERCENTAGE && rightType == ExpressionType.PERCENTAGE) {
                    return ExpressionType.PERCENTAGE;
                }
            } else if (op instanceof MultiplyOperation) {
                if ((leftType == ExpressionType.PIXEL && rightType == ExpressionType.SCALAR)) {
                    return ExpressionType.PIXEL;
                }
                if ((leftType == ExpressionType.SCALAR && rightType == ExpressionType.PIXEL)) {
                    return ExpressionType.PIXEL;
                }
                if ((leftType == ExpressionType.PERCENTAGE && rightType == ExpressionType.SCALAR)) {
                    return ExpressionType.PERCENTAGE;
                }
                if ((leftType == ExpressionType.SCALAR && rightType == ExpressionType.PERCENTAGE)) {
                    return ExpressionType.PERCENTAGE;
                }
            }
        }
        return ExpressionType.UNDEFINED;
    }

    private void checkDeclaration(Declaration declaration) {
        ExpressionType expressionType = getExpressionType(declaration.expression);

        if (declaration.property.name.equals("width") || declaration.property.name.equals("height")) {
            if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE) {
                declaration.setError("Property '" + declaration.property.name + "' expects a pixel or percentage value, but got " + expressionType);
            }
        } else if (declaration.property.name.equals("color") || declaration.property.name.equals("background-color")) {
            if (expressionType != ExpressionType.COLOR) {
                declaration.setError("Property '" + declaration.property.name + "' expects a color value, but got " + expressionType);
            }
        } else {
            declaration.setError("Property '" + declaration.property.name + "' is not a recognized property.");
        }
    }

    private ExpressionType getVariableType(String variableName) {
        for (int i = variableTypes.size() - 1; i >= 0; i--) {
            HashMap<String, ExpressionType> scope = variableTypes.get(i);
            if (scope.containsKey(variableName)) {
                return scope.get(variableName);
            }
        }
        return ExpressionType.UNDEFINED;
    }
}
