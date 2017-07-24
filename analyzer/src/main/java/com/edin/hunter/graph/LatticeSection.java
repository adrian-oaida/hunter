package com.edin.hunter.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dude on 7/18/17.
 */
public class LatticeSection implements Iterable<Node>{

    private List<Node> nodeList;
    public LatticeSection(){
        nodeList = new ArrayList<>();
    }

    public void addNode(Node node){
        nodeList.add(node);
    }

    @Override
    public Iterator<Node> iterator() {
        return nodeList.iterator();
    }
}
