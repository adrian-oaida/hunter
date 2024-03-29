package com.edin.hunter.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.edin.hunter.graph.DirectedGraph.ATTR_COLOR;

/**
 * Created by dude on 7/6/17.
 */
public class Edge {

    private Node source;
    private Node target;

    private int id;
    private Map<String, String> attributes = new HashMap<>();
    private DirectedGraph region;


    public Edge(Node source, Node target){
        this.id = EdgeIdGenerator.getNextId();
        this.source = source;
        this.target = target;
        this.attributes.put(ATTR_COLOR, "black");
    }

    public Node getSource(){
        return source;
    }
    public Node getTarget(){
        return target;
    }

    public Map<String, String> getAttributeMap(){
        return attributes;
    }
    public void copyAttributesFrom(Edge edge) {
        this.attributes.putAll(edge.attributes);
    }
    public String getAttribute(String attributeName){
        return attributes.get(attributeName);
    }
    public void setAttribute(String attributeName, String attributeValue){
        attributes.put(attributeName, attributeValue);
    }
    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;

        if(obj instanceof Edge){
            Edge edge = (Edge)obj;
            return this.id == edge.id && this.source.equals(edge.source) && this.target.equals(edge.target);
        }

        return false;
    }
    private int hashCode = 0;
    @Override
    public int hashCode(){
        if(hashCode == 0){
            hashCode = 1;
            hashCode = hashCode * 17 + id;
            hashCode = hashCode * 31 + this.source.hashCode();
            hashCode = hashCode * 31 + this.target.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString(){
        return source.getId() + " -> " + target.getId();
    }

    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"source\": ").append("\"").append(source).append("\",");
        sb.append("\"target\": ").append("\"").append(target).append("\",");
        sb.append("\"value\": ").append(new Random().nextInt());

        for(Map.Entry<String, String> attribute : attributes.entrySet()){
            sb.append(",");
            sb.append("\"").append(attribute.getKey()).append("\": ");
            sb.append("\"").append(attribute.getValue().replace(" ", "").replace("\\","").replace("\"", "'")).append("\"");
        }
        sb.append("}");

        return sb.toString();
    }
}
class EdgeIdGenerator{
    private static Integer nextId = new Integer(0);

    public static synchronized int getNextId(){
        nextId++;
        return nextId;
    }

}