package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();

        variableValues.addFirst(new HashMap<>());

        processBody(ast.root.body);

        variableValues.removeFirst();
    }

    private void processBody(ArrayList<ASTNode> body) {
        int i = 0;
        while (i < body.size()) {
            ASTNode node = body.get(i);

            if (node instanceof VariableAssignment) {
                VariableAssignment assignment = (VariableAssignment) node;

                Literal value = evaluateExpression(assignment.expression);
                String name = assignment.name.name;
                variableValues.getFirst().put(name, value);

                body.remove(i);

            } else if (node instanceof Stylerule) {
                Stylerule stylerule = (Stylerule) node;

                variableValues.addFirst(new HashMap<>());
                processBody(stylerule.body);
                variableValues.removeFirst();

                i++;

            } else if (node instanceof IfClause) {

                IfClause ifClause = (IfClause) node;
                Literal condition = evaluateExpression(ifClause.conditionalExpression);

                if (((BoolLiteral) condition).value == true) {

                    variableValues.addFirst(new HashMap<>());
                    processBody(ifClause.body);
                    variableValues.removeFirst();

                    body.remove(i);
                    body.addAll(i, ifClause.body);

                } else {

                    if (ifClause.getElseClause() != null) {
                        ElseClause elseClause = ifClause.getElseClause();

                        variableValues.addFirst(new HashMap<>());
                        processBody(elseClause.body);
                        variableValues.removeFirst();

                        body.remove(i);
                        body.addAll(i, elseClause.body);

                    } else {
                        body.remove(i);
                    }
                }

            } else if (node instanceof Declaration) {
                applyDeclaration((Declaration) node);
                i++;
            } else {
                i++;
            }
        }
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evaluateExpression(node.expression);
    }

    private Literal evaluateExpression(Expression expression) {
        if (expression instanceof Literal) {
            return (Literal) expression;
        }
        if (expression instanceof VariableReference) {
            VariableReference ref = (VariableReference) expression;
            return getVariableValue(ref.name);
        }
        if (expression instanceof Operation) {
            return evaluateOperation((Operation) expression);
        }
        throw new IllegalArgumentException("Unknown expression type: " + expression.getClass().getSimpleName());
    }

    private Literal getVariableValue(String name) {
        for (HashMap<String, Literal> scope : variableValues) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        throw new IllegalStateException("Variable " + name + " not found in any scope during evaluation.");
    }

    private Literal evaluateOperation(Operation operation) {
        Literal left = evaluateExpression(operation.left);
        Literal right = evaluateExpression(operation.right);

        if (operation instanceof MultiplyOperation) {
            return evaluateMultiplication(left, right);
        } else if (operation instanceof AddOperation) {
            return evaluateAddition(left, right);
        } else if (operation instanceof SubtractOperation) {
            return evaluateSubtraction(left, right);
        }

        throw new IllegalArgumentException("Unknown operation type: " + operation.getClass().getSimpleName());
    }

    private Literal evaluateMultiplication(Literal left, Literal right) {

        if (left instanceof ScalarLiteral) {
            int scalarValue = ((ScalarLiteral) left).value;

            if (right instanceof PixelLiteral) {
                int result = scalarValue * ((PixelLiteral) right).value;
                return new PixelLiteral(result);

            } else if (right instanceof PercentageLiteral) {
                int result = scalarValue * ((PercentageLiteral) right).value;
                return new PercentageLiteral(result);

            } else if (right instanceof ScalarLiteral) {
                int result = scalarValue * ((ScalarLiteral) right).value;
                return new ScalarLiteral(result);
            }
        } else if (right instanceof ScalarLiteral) {
            int scalarValue = ((ScalarLiteral) right).value;

            if (left instanceof PixelLiteral) {
                int result = ((PixelLiteral) left).value * scalarValue;
                return new PixelLiteral(result);

            } else if (left instanceof PercentageLiteral) {
                int result = ((PercentageLiteral) left).value * scalarValue;
                return new PercentageLiteral(result);
            }
        }

        throw new IllegalArgumentException("Invalid operands for Multiplication.");
    }

    private Literal evaluateAddition(Literal left, Literal right) {

        if (left instanceof PixelLiteral) {
            if (right instanceof PixelLiteral) {
                int result = ((PixelLiteral) left).value + ((PixelLiteral) right).value;
                return new PixelLiteral(result);
            }
        } else if (left instanceof PercentageLiteral) {
            if (right instanceof PercentageLiteral) {
                int result = ((PercentageLiteral) left).value + ((PercentageLiteral) right).value;
                return new PercentageLiteral(result);
            }
        } else if (left instanceof ScalarLiteral) {
            if (right instanceof ScalarLiteral) {
                int result = ((ScalarLiteral) left).value + ((ScalarLiteral) right).value;
                return new ScalarLiteral(result);
            }
        }

        throw new IllegalArgumentException("Invalid operands for Addition: types must match (e.g., Pixel + Pixel).");
    }

    private Literal evaluateSubtraction(Literal left, Literal right) {

        if (left instanceof PixelLiteral) {
            if (right instanceof PixelLiteral) {
                int result = ((PixelLiteral) left).value - ((PixelLiteral) right).value;
                return new PixelLiteral(result);
            }
        } else if (left instanceof PercentageLiteral) {
            if (right instanceof PercentageLiteral) {
                int result = ((PercentageLiteral) left).value - ((PercentageLiteral) right).value;
                return new PercentageLiteral(result);
            }
        } else if (left instanceof ScalarLiteral) {
            if (right instanceof ScalarLiteral) {
                int result = ((ScalarLiteral) left).value - ((ScalarLiteral) right).value;
                return new ScalarLiteral(result);
            }
        }

        throw new IllegalArgumentException("Invalid operands for Subtraction: types must match (e.g., Pixel - Pixel).");
    }
}