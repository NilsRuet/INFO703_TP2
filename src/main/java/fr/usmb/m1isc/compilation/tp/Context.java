package fr.usmb.m1isc.compilation.tp;

import java.util.ArrayList;

public class Context {
    private ArrayList<String> identifiers;

    public Context(){
        this.identifiers = new ArrayList<>();
    }

    public boolean createIdentifier(String identifier){
        return false;
    }

    public boolean identifierExists(String identifier){
        return false;
    }

}
