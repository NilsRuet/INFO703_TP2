package fr.usmb.m1isc.compilation.tp;

import java.util.ArrayList;

public class Compiler {
    public String code;
    private int _idCounter;

    public Compiler(LambadaTree lambadaTree){
        code = compile(lambadaTree);
    }

    private String compile(LambadaTree tree){
        _idCounter = 0;
        String dataSegment = buildDataSegment(tree);
        String code = compileTree(tree);
        String res =
                "DATA SEGMENT\n"
                +dataSegment
                +"DATA ENDS\n"
                +"CODE SEGMENT\n"
                +code
                +"CODE ENDS";
        return res;
    }

    private String buildDataSegment(LambadaTree tree){
        Context ctx = new Context(tree);
        ArrayList<String> variables = ctx.allVariables();
        if(variables == null) return null;

        StringBuilder res = new StringBuilder();
        for (String identifier : variables){
            res.append(identifier).append(" DD\n");
        }
        return res.toString();
    }

    private String compileTree(LambadaTree t){
        LambadaTree.NodeType racType = t.getType();
        switch (racType){
            case SEMI:
                return compileSemi(t);
            case LET:
                return compileLet(t);
            case IN:
                return compileIn(t);
            case OUT:
                return compileOut(t);
            case IDENTIFIER:
                return compileIdentifier(t);
            case INTEGER:
                return compileInteger(t);
            case NEGATIVE:
                return compileNegative(t);
            case PLUS:
                return compilePlus(t);
            case MINUS:
                return compileMinus(t);
            case MULT:
                return compileMult(t);
            case DIV:
                return compileDiv(t);
            case MOD:
                return compileMod(t);
            case WHILE:
                return compileWhile(t);
            case IF:
                return compileIf(t);
            case NOT:
                return compileNot(t);
            case AND:
                return compileAnd(t);
            case OR:
                return compileOr(t);
            case EQ:
                return compileEq(t);
            case LT:
                return compileLT();
            case LTE:
                return compileLTE();
            default:
                return "";
        }
    }

    private String compileLTE() {
        return "";
    }

    private String compileLT() {
        return "";
    }

    private String compileEq(LambadaTree t) {
        _idCounter++;
        String id = Integer.toString(_idCounter);
        return compileTree(t.getLeft())+
                compileTree(t.getRight())+
                "pop eax\n"+
                "pop ebx\n"+
                "sub eax, ebx\n"+
                "jnz eq_false_"+id+"\n"+
                "push 1\n"+
                "jmp eq_end_"+id+"\n"+
                "eq_false_"+id+":\n"+
                "push 0\n"+
                "eq_end_"+id+":\n";
    }

    private String compileOr(LambadaTree t) {
        return "";
    }

    private String compileAnd(LambadaTree t) {
        return "";
    }

    private String compileNot(LambadaTree t) {
        return "";
    }

    private String compileIf(LambadaTree t) {
        _idCounter++;
        String id = Integer.toString(_idCounter);
        return compileTree(t.getLeft())+
                "pop eax\n"+
                "jz if_false_"+id+"\n"+
                compileTree(t.getRight().getLeft())+
                "jmp if_end_"+id+"\n"+
                "if_false_"+id+":\n"+
                compileTree(t.getRight().getRight())+
                "if_end_"+id+":\n";
    }

    private String compileWhile(LambadaTree t) {
        return "";
    }

    private String compileSemi(LambadaTree t){
        return compileTree(t.getLeft())+compileTree(t.getRight());
    }

    private String compileLet(LambadaTree t){
        return compileTree(t.getRight()) +
                "pop eax\n" +
                String.format("mov %s,eax\n", t.getLeft().getRac());
    }

    private String compileIn(LambadaTree t){
        return "in eax\n"
                +"push eax\n";
    }

    private String compileOut(LambadaTree t){
        return  compileTree(t.getRight())+
                "pop eax\n" +
                "out eax\n";
    }

    private String compileIdentifier(LambadaTree t){
        return String.format("mov eax,%s\n", t.getRac()) +
                "push eax\n";
    }

    private String compileInteger(LambadaTree t){
        return String.format("mov eax,%s\n", t.getRac()) +
                "push eax\n";
    }

    private String compileNegative(LambadaTree t){
        return compileTree(t.getRight()) +
                "pop eax\n" +
                "mul eax, -1\n" +
                "push eax\n";
    }

    private String compilePlus(LambadaTree t){
        return compileTree(t.getLeft())+
                compileTree(t.getRight())+
                "pop eax\n"+
                "pop ebx\n"+
                "add eax, ebx\n"+
                "push eax\n";
    }

    private String compileMinus(LambadaTree t){
        return compileTree(t.getLeft())+
                compileTree(t.getRight())+
                "pop eax\n"+
                "pop ebx\n"+
                "sub eax, ebx\n"+
                "push eax\n";
    }

    private String compileMult(LambadaTree t){
        return compileTree(t.getLeft())+
                compileTree(t.getRight())+
                "pop eax\n"+
                "pop ebx\n"+
                "mul eax, ebx\n"+
                "push eax\n";
    }

    private String compileDiv(LambadaTree t){
        return compileTree(t.getLeft())+
                compileTree(t.getRight())+
                "pop eax\n"+
                "pop ebx\n"+
                "div ebx, eax\n"+
                "push ebx\n";
    }

    private String compileMod(LambadaTree t){
        return compileTree(t.getLeft())+
                compileTree(t.getRight())+
                "pop ebx\n"+
                "pop eax\n"+
                "mov ecx, eax\n"+
                "div eax, ebx\n"+
                "mul eax, ebx\n"+
                "sub ecx, eax\n"+
                "push ecx\n";
    }
}
