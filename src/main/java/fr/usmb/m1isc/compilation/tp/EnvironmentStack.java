package fr.usmb.m1isc.compilation.tp;

import java.util.ArrayList;
import java.util.Stack;

public class EnvironmentStack {
    private Stack<ArrayList<String>> _environments;

    public EnvironmentStack(){
        _environments = new Stack<>();
        pushEnvironment();
    }

    public void pushEnvironment(){
        _environments.push(new ArrayList<>());
    }

    public void popEnvironment(){
        _environments.pop();
    }

    public boolean createIdentifier(String identifier){
        if(identifierExists(identifier)) return false;
        _environments.peek().add(identifier);
        return true;
    }

    public boolean identifierExists(String identifier){
        for (ArrayList<String> layer : _environments) {
            if(layer.contains(identifier)) return true;
        }
        return false;
    }
}
