package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        checkStylesheet(ast.root);

    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
    }

    private void checkStylesheet(Stylesheet sheet) {
        for (ASTNode child : sheet.getChildren()) {
            if (child instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) child);
            }
            else if (child instanceof Stylerule) {
                checkStyleRule((Stylerule) child);
            }
        }
    }

    private void checkStyleRule(Stylerule rule) {
        for (ASTNode child : rule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
            if (child instanceof IfClause){
                checkIfClause((IfClause) child);
            }
        }
    }

    private void checkIfClause(IfClause child) { // TODO check op BoolLiteral
    }

    private void checkDeclaration(Declaration declaration) {
        if (declaration.property.name.equals("width") || declaration.property.name.equals("height")) {
            if (!(declaration.expression instanceof PercentageLiteral) && !(declaration.expression instanceof PixelLiteral)) {
                declaration.setError("Only percentages or pixels allowed");

            }
        } else if (declaration.property.name.equals("color") || declaration.property.name.equals("background-color")) {
            if (!(declaration.expression instanceof ColorLiteral)) { // TODO nog checken op value van bijv variable
                declaration.setError("Color, only colors allowed");
            }
        } else {
            declaration.setError("Property has to be width, height, color or background-color");
        }
    }


}
