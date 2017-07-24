package com.edin.hunter.graph;

import java.util.*;

/**
 * Created by dude on 7/6/17.
 */
public class DirectedGraph implements Iterable<Node>{
    private String name;
    protected Map<Integer, Node> nodeMap = new HashMap<>();
    protected int maxNodeId = 0;

    public DirectedGraph(String name){
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

    public Node getOrAddNode(String nodeId){
        return getOrAddNode(Integer.parseInt(nodeId));
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
    public String toJSON(){
        StringBuilder sb = new StringBuilder();
        List<Edge> edges = new ArrayList<>();

        sb.append("{");
        sb.append("\"nodes\": [");
        String prefix = "";
        for(Node node : nodeMap.values()){
            sb.append(prefix);
            prefix = ",";
            sb.append("{");

            sb.append("\"id\": ");sb.append("\"");sb.append(node.toString());sb.append("\"");

            for(Map.Entry<String, String> attribute : node.getAttributeMap().entrySet()){
                sb.append(",");
                sb.append("\"").append(attribute.getKey()).append("\": ");
                sb.append("\"").append(attribute.getValue()).append("\"");
            }
            sb.append("}");

            edges.addAll(node.getOutgoingEdges());
        }

        sb.append("],");

        prefix = "";
        sb.append("\"links\": [");
        for(Edge edge : edges){
            sb.append(prefix);
            prefix = ",";
            sb.append("{");
            sb.append("\"source\": ").append("\"").append(edge.getSource()).append("\",");
            sb.append("\"target\": ").append("\"").append(edge.getTarget()).append("\",");
            sb.append("\"color\": \"red\",");

            sb.append("\"value\": 2");

            sb.append("}");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}
