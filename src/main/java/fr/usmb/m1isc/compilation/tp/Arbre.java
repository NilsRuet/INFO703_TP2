package fr.usmb.m1isc.compilation.tp;

public class Arbre {

    private Arbre gauche;
    private Arbre droite;
    private String rac;

    public Arbre(String rac){
        gauche = null;
        droite = null;
        this.rac = rac;
    }

    public Arbre(Arbre gauche, String rac, Arbre droite) {
        this.gauche = gauche;
        this.droite = droite;
        this.rac = rac;
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
        res += prefix + rac.toString();
        if(gauche != null){
            res += "\n" + gauche.toString(prefix+decal);
        }
        return res;
    }
}
