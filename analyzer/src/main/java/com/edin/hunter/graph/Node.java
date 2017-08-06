package com.edin.hunter.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.edin.hunter.graph.DirectedGraph.ATTR_INSTRUCTION;

/**
 * Created by dude on 7/6/17.
 */
public class Node {
    public enum InstructionType
    {
        ASSIGNMENT, ITERATIVE
    }
    private int id;
    protected DirectedGraph graph;

    private List<Edge> outgoingEdges = new ArrayList<>();
    private List<Edge> incomingEdges = new ArrayList<>();

    private List<Node> neighbouringNodes = new ArrayList<>();
    private Map<String, String> attributes = new HashMap<>();

    private List<Node> associatedNodes = new ArrayList<>();
    private InstructionType instructionType;

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public List<Node> getAssociatedNodes(){
        return associatedNodes;
    }

    public void associateWithNode(Node node){
        if(!associatedNodes.contains(node)){
            associatedNodes.add(node);
        }
    }
    protected Node(int id, DirectedGraph graph){
        this.id = id;
        this.graph = graph;
        this.attributes.put("color", "blue");
    }

    public Map<String, String> getAttributeMap(){
        return attributes;
    }



    protected void disconnect(){
        for(Edge edge : outgoingEdges){
            edge.getTarget().incomingEdges.remove(edge);
            edge.getTarget().neighbouringNodes.remove(this);
        }
        for(Edge edge : incomingEdges){
            edge.getSource().outgoingEdges.remove(edge);
            edge.getSource().neighbouringNodes.remove(this);
        }
    }

    public String getAttribute(String attributeName){
        return attributes.get(attributeName);
    }

    public void setAttribute(String attributeName, String attributeValue){
        attributes.put(attributeName, attributeValue);
        if(attributes.containsKey(ATTR_INSTRUCTION)){
            //
            String instruction = attributes.get(ATTR_INSTRUCTION);

            if(instruction.startsWith("for") ||
                    instruction.startsWith("while") ||
                    instruction.startsWith("do")){
                this.instructionType = InstructionType.ITERATIVE;
            }
            if(instruction.split("=").length == 2){
                this.instructionType = InstructionType.ASSIGNMENT;
            }
        }
    }

    public void copyAttributesFrom(Node node) {
        this.attributes.putAll(node.attributes);
    }

    public int getId(){
        return id;
    }

    public List<Edge> getAllEdges(){
        return Stream.concat(outgoingEdges.stream(), incomingEdges.stream()).collect(Collectors.toList());
    }

    public List<Edge> getIncomingEdges(){
        return incomingEdges;
    }
    public List<Edge> getOutgoingEdges(){
        return outgoingEdges;
    }

    public int getInDegree(){
        return incomingEdges.size();
    }

    public int getOutDegree(){
        return outgoingEdges.size();
    }

    public List<Edge> getEdgesTowards(Node node){
        return outgoingEdges.stream().filter(edge -> edge.getTarget() == node).collect(Collectors.toList());
    }
    public int getOutwardDegreeToward(Node node){
        return outgoingEdges.stream().mapToInt(edge -> edge.getTarget() == node ? 1 : 0).sum();

    }
    public List<Edge> getEdgesFrom(Node node){
        return incomingEdges.stream().filter(edge -> edge.getSource() == node).collect(Collectors.toList());
    }

    public List<Node> getNeighbouringNodes(){
        return neighbouringNodes;
    }

    public boolean isNeighbourOf(Node node){
        return neighbouringNodes.contains(node);
    }

    public void removeEdge(Edge edge) {
        if(edge.getTarget() == this && edge.getSource() == this){
            outgoingEdges.remove(edge);
            incomingEdges.remove(edge);
        }else if(edge.getSource() == this){

            if(getOutwardDegreeToward(edge.getTarget()) == 1){
                edge.getTarget().neighbouringNodes.remove(this);
                neighbouringNodes.remove(edge.getTarget());
            }
            edge.getTarget().incomingEdges.remove(edge);
            outgoingEdges.remove(edge);
        }
    }

    public Edge addEdgeTo(Node node){
        Edge edge = new Edge(this, node);
        neighbouringNodes.add(node);
        outgoingEdges.add(edge);

        node.incomingEdges.add(edge);

        node.neighbouringNodes.add(this);

        return edge;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj instanceof Node){
            Node node = (Node) obj;
            if(node.id == this.id)
                return true;

        }
        return false;
    }
    @Override
    public int hashCode(){
        int hash = 1;
        hash = hash * 17 + id;
        return hash;
    }
    @Override
    public String toString(){
        return "" + this.id;
    }


    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        sb.append("\"id\": ");sb.append("\"");sb.append(this.id);sb.append("\"");

        for(Map.Entry<String, String> attribute : attributes.entrySet()){
            sb.append(",");
            sb.append("\"").append(attribute.getKey()).append("\": ");
            sb.append("\"").append(attribute.getValue().replace(" ", "").replace("\\","").replace("\"", "'")).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }


}
