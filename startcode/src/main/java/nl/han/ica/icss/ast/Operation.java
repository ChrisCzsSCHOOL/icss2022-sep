package nl.han.ica.icss.ast;

import java.util.ArrayList;

public abstract class Operation extends Expression {

    public Expression left;
    public Expression right;

    @Override
    public ArrayList<ASTNode> getChildren() {
        ArrayList<ASTNode> children = new ArrayList<>();
        if(left != null)
            children.add(left);
        if(right != null)
            children.add(right);
        return children;
    }

    @Override
    public ASTNode addChild(ASTNode child) {
        if(left == null) {
            left = (Expression) child;
        } else if(right == null) {
            right = (Expression) child;
        }
        return this;
    }
}
