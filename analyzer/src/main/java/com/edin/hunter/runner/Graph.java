package com.edin.hunter.runner;

import java.util.*;

/**
 * Created by dude on 7/6/17.
 */
public class Graph implements Iterable<Node>{
    private String name;
    private Map<Integer, Node> nodeMap = new HashMap<>();
    private int maxNodeId = 0;

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

    public int getNodeCount(){
        return nodeMap.values().size();
    }
    public int getMaxNodeId(){
        return maxNodeId;
    }
    public Node getOrAddNode(int nodeId){
        if(nodeId > maxNodeId){
            maxNodeId = nodeId;
        }
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

            sb.append(node.toString());
            sb.append(" [");
                for(Map.Entry<String, String> attribute : node.getAttributeMap().entrySet()){
                    sb.append(attribute.getKey()); sb.append("="); sb.append(attribute.getValue());
                    sb.append(",");
                }
            sb.append("]");
            sb.append(";\n");
            for(Edge edge : node.getOutgoingEdges()){
                sb.append(edge.getSource());
                sb.append(" -> ");
                sb.append(edge.getTarget());
                sb.append(" [");
                for(Map.Entry<String, String> attribute : edge.getAttributeMap().entrySet()){
                    sb.append(attribute.getKey()); sb.append("="); sb.append(attribute.getValue());
                    sb.append(",");
                }
                sb.append("]");

                sb.append(";\n");
            }
        }

        sb.append("\n}");

        return sb.toString();
    }
}
