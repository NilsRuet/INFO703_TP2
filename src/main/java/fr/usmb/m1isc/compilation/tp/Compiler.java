package fr.usmb.m1isc.compilation.tp;

import java.util.ArrayList;

public class Compiler {
    private LambadaTree _lambadaTree;

    public Compiler(LambadaTree lambadaTree){
        _lambadaTree = lambadaTree;
    }

    public String compile(){
        String dataSegment = buildDataSegment(_lambadaTree);
        String code = compileTree(_lambadaTree);
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
            default:
                return "";
        }
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
                "push eax\n";
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
