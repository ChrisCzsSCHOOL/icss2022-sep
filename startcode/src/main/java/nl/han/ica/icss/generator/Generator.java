package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.*;

import java.util.stream.Collectors;

public class Generator {

    private final String INDENT = "  ";

    public String generate(AST ast) {
        StringBuilder builder = new StringBuilder();

        generateStylesheet(ast.root, builder, 0);

        return builder.toString();
    }

    private void indent(StringBuilder builder, int level) {
        for (int i = 0; i < level; i++) {
            builder.append(INDENT);
        }
    }

    private void generateStylesheet(Stylesheet stylesheet, StringBuilder builder, int level) {
        for (ASTNode node : stylesheet.body) {
            if (node instanceof Stylerule) {
                generateStylerule((Stylerule) node, builder, level);
            }
        }
    }

    private void generateStylerule(Stylerule stylerule, StringBuilder builder, int level) {
        String selectorsString = stylerule.selectors.stream()
                .map(Selector::toString)
                .collect(Collectors.joining(", "));

        builder.append(selectorsString).append(" {\n");

        int nextLevel = level + 1;

        for (ASTNode node : stylerule.body) {
            if (node instanceof Declaration) {
                generateDeclaration((Declaration) node, builder, nextLevel);
            }
        }

        indent(builder, level);
        builder.append("}\n\n");
    }

    private void generateDeclaration(Declaration declaration, StringBuilder builder, int level) {
        indent(builder, level);
        builder.append(declaration.property.name)
                .append(": ")
                .append(generateLiteral((Literal) declaration.expression))
                .append(";\n");
    }

    private String generateLiteral(Literal literal) {
        if (literal instanceof ColorLiteral) {
            return ((ColorLiteral) literal).value;
        } else if (literal instanceof PixelLiteral) {
            return ((PixelLiteral) literal).value + "px";
        } else if (literal instanceof PercentageLiteral) {
            return ((PercentageLiteral) literal).value + "%";
        } else if (literal instanceof ScalarLiteral) {
            return String.valueOf(((ScalarLiteral) literal).value);
        }
        throw new IllegalArgumentException("Kan geen CSS genereren voor een expressie van het type: " + literal.getClass().getSimpleName());
    }
}