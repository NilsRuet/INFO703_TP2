package fr.usmb.m1isc.compilation.tp;

public class LambadaTree {

    public enum NodeType {
        SEMI, LET, WHILE, IF, ELSE, ERROR, NOT, AND, OR, EQ, LT, LTE, PLUS, MINUS, MULT, DIV, MOD,
        NEGATIVE, OUT, IN, NIL, INTEGER, IDENTIFIER
    }

    private LambadaTree left;
    private LambadaTree right;
    private NodeType type;
    private Object rac;

    public LambadaTree getLeft() {
        return left;
    }

    public LambadaTree getRight() {
        return right;
    }

    public NodeType getType() {
        return type;
    }

    public Object getRac() {
        return rac;
    }

    public LambadaTree(LambadaTree left, NodeType t, Object rac, LambadaTree right) {
        this.left = left;
        this.right = right;
        this.rac = rac;
        this.type = t;
    }

    public LambadaTree(LambadaTree left, NodeType t, LambadaTree right) {
        this(left, t, null, right);
    }

    public LambadaTree(NodeType t, Object rac){
        this(null,t, rac, null);
    }

    public LambadaTree(NodeType t){
        this(t, null);
    }

    @Override
    public String toString(){
        return toString("");
    }

    public String toString(String prefix){
        String res = "";
        String decal = "   ";
        if(right != null){
            res += right.toString(prefix+decal) + "\n";
        }
        res += prefix + racToString();
        if(left != null){
            res += "\n" + left.toString(prefix+decal);
        }
        return res;
    }

    private String racToString(){
        if(rac == null){
            return type.toString();
        } else {
            return rac.toString();
        }
    }
}
