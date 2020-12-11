package fr.usmb.m1isc.compilation.tp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

public class Context {
    private ArrayList<String> _identifiers;
    private EnvironmentStack _environments;
    public Context(LambadaTree tree){
        this._identifiers = buildContext(tree);
    }

    public ArrayList<String> allVariables() {
        return _identifiers;
    }

    private ArrayList<String> buildContext(LambadaTree tree){
        _identifiers = new ArrayList<>();
        _environments = new EnvironmentStack();
        if(!build(tree)){
            _identifiers = null;
        }
        return _identifiers;
    }

    // Builds the identifier list and checks if any identifier is referenced without being declared
    private boolean build(LambadaTree tree){
        Stack<BuildAction> buildOrder = new Stack<>();
        buildOrder.add(new BuildAction(tree));
        while(buildOrder.size() > 0){
            BuildAction currentAction = buildOrder.pop();
            switch(currentAction.actionType){
                case Tree:
                    if(!buildTree(currentAction.treeToBuild, buildOrder))return false;
                    break;
                case Push:
                    _environments.pushEnvironment();
                    break;
                case Pop:
                    _environments.popEnvironment();
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private boolean buildTree(LambadaTree currentTree, Stack<BuildAction> buildOrder){
        if(currentTree==null) return true;
        switch (currentTree.getType()){
            case ERROR:
                System.err.println("Syntax error.");
                return false;
            case LET:
                if(!buildLet(currentTree)) return false;
                buildOrder.push(new BuildAction(currentTree.getRight()));
                break;
            case WHILE:
                buildOrder.push(new BuildAction(BuildType.Pop));
                buildOrder.push(new BuildAction(currentTree.getRight()));
                buildOrder.push(new BuildAction(BuildType.Push));
                buildOrder.push(new BuildAction(currentTree.getLeft()));
                break;
            case IF:
                buildOrder.push(new BuildAction(currentTree.getLeft()));
                LambadaTree elseTree = currentTree.getRight();

                buildOrder.push(new BuildAction(BuildType.Pop));
                buildOrder.push(new BuildAction(elseTree.getRight()));
                buildOrder.push(new BuildAction(BuildType.Push));

                buildOrder.push(new BuildAction(BuildType.Pop));
                buildOrder.push(new BuildAction(elseTree.getRight()));
                buildOrder.push(new BuildAction(BuildType.Push));

                buildOrder.push(new BuildAction(currentTree.getLeft()));
                break;
            case IDENTIFIER:
                String identifier = (String)currentTree.getRac();
                if(!_environments.identifierExists(identifier)){
                    System.err.println("Use of undeclared identifier : "+identifier);
                    return false;
                }
                break;
            default:
                buildOrder.push(new BuildAction(currentTree.getRight()));
                buildOrder.push(new BuildAction(currentTree.getLeft()));
                break;
        }
        return true;
    }

    private boolean buildLet(LambadaTree tree){
        String identifier = (String)tree.getLeft().getRac();
        if(!_identifiers.contains(identifier)){
            _identifiers.add(identifier);
            _environments.createIdentifier(identifier);
        }
        return true;
    }

    private static class BuildAction{
        public BuildType actionType;
        public LambadaTree treeToBuild;

        public BuildAction(BuildType action){
            actionType = action;
        }

        public BuildAction(LambadaTree tree){
            this(BuildType.Tree);
            treeToBuild = tree;
        }
    }

    private enum BuildType {Tree, Push, Pop}
}
