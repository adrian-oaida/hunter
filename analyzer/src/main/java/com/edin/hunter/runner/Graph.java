package com.edin.hunter.runner;

import java.util.*;

/**
 * Created by dude on 7/6/17.
 */
public class Graph implements Iterable<Node>{
    private String name;
    private Map<Integer, Node> nodeMap = new HashMap<>();

    public Graph(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void removeNode(Node node){
        nodeMap.remove(node.getId());
        node.disconnect();
    }
    public void removeNode(int nodeId){
        removeNode(nodeMap.get(nodeId));
    }
    public Node getOrAddNode(int nodeId){
        Node node;
        if(nodeMap.containsKey(nodeId)){
            node = nodeMap.get(nodeId);
        }else{
            node = new Node(nodeId, this);
            nodeMap.put(nodeId, node);
        }
        return node;
    }

    @Override
    public Iterator<Node> iterator() {
        return nodeMap.values().iterator();
    }

    public String toDotString(){
        StringBuilder sb = new StringBuilder();
        sb.append("digraph "); sb.append(name.replace(" ", "")); sb.append(" {\n");
        for(Node node : nodeMap.values()){
            for(Edge edge : node.getOutgoingEdges()){
                sb.append(edge.getSource());
                sb.append(" -> ");
                sb.append(edge.getTarget());
                sb.append(";\n");
            }
        }

        sb.append("\n}");

        return sb.toString();
    }
}
