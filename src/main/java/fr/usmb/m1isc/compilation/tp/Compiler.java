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
            case IDENTIFIER:
                return compileIdentifier(t);
            case INTEGER:
                return "int";
            case NEGATIVE:
            case PLUS:
            case MINUS:
            case MULT:
            case DIV:
            case MOD:
            case SEMI:
            case LET:
            default:
                break;
        }
        return "";
    }

    private String compileIdentifier(LambadaTree t){
        return "";
    }
}
