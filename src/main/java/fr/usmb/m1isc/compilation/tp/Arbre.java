package fr.usmb.m1isc.compilation.tp;

import jdk.nashorn.internal.runtime.regexp.joni.constants.NodeType;

public class Arbre {

    public enum TypeNoeud{
        SEMI, LET, WHILE, IF, ELSE, ERROR, NOT, AND, OR, EQ, LT, LTE, PLUS, MINUS, MULT, DIV, MOD,
        NEGATIVE, OUT, IN, NIL, INTEGER, IDENTIFIER
    }

    private Arbre gauche;
    private Arbre droite;
    private TypeNoeud type;
    private Object rac;

    public Arbre(Arbre gauche, TypeNoeud t, Object rac, Arbre droite) {
        this.gauche = gauche;
        this.droite = droite;
        this.rac = rac;
        this.type = t;
    }

    public Arbre(Arbre gauche, TypeNoeud t, Arbre droite) {
        this(gauche, t, null, droite);
    }

    public Arbre(TypeNoeud t, Object rac){
        this(null,t, rac, null);
    }

    public Arbre(TypeNoeud t){
        this(null, t);
    }

    @Override
    public String toString(){
        return toString("");
    }

    public String toString(String prefix){
        String res = "";
        String decal = "   ";
        if(droite != null){
            res += droite.toString(prefix+decal) + "\n";
        }
        res += prefix + racToString();
        if(gauche != null){
            res += "\n" + gauche.toString(prefix+decal);
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
