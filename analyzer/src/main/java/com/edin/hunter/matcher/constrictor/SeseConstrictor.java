package com.edin.hunter.matcher.constrictor;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;
import com.edin.hunter.matcher.constrictor.AgglomerationConstrictor;
import com.edin.hunter.runner.BaseRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeseConstrictor extends AgglomerationConstrictor {


    public SeseConstrictor(BaseRunner runner) {
        super(runner);
    }

    @Override
    public int eliminateAgglomeration() {
        int initialDFNodeNumber = dataFlowGraph.getNodeCount();
        boolean[] visited = new boolean[dataFlowGraph.getMaxNodeId() + 1];
        boolean hasConstricted = false;
        List<Node> sisoNodes = new ArrayList<>();
        for(Node node : dataFlowGraph){
            if(node.getOutDegree() == 1 && node.getInDegree() == 1){
                //we have a posible siso
                sisoNodes.add(node);
            }
        }
        List<Set<Node>> regionsList = new ArrayList<>();
        for(Node node : sisoNodes){
            if(!visited[node.getId()]){
                Set<Node> region = new HashSet<>();
                Node tmp = node;
                while(tmp.getOutDegree() == 1 && tmp.getInDegree() == 1 && !visited[tmp.getId()]){
                    region.add(tmp);
                    visited[tmp.getId()] = true;
                    tmp = tmp.getOutgoingEdges().get(0).getTarget();

                }
                tmp = node;
                while(tmp.getOutDegree() == 1 && tmp.getInDegree() == 1 && !visited[tmp.getId()]){
                    region.add(tmp);
                    visited[tmp.getId()] = true;
                    tmp = tmp.getIncomingEdges().get(0).getSource();
                }
                regionsList.add(region);
                //run to forward nodes
                //run to backward nodes
            }
        }
        //in this stage we have to order the region
        //but first we will just colour it
        int color = 0;
//        System.out.printf("got %d regions \n", regionsList.size());
        //get the first and last node, and create an edge between the parent and child of the region

        for(Set<Node> region : regionsList){
            Node parent = null, child = null;
            for(Node node : region){
                if(!region.contains(node.getIncomingEdges().get(0).getSource())){
                    parent = node.getIncomingEdges().get(0).getSource();
                }
                if(!region.contains(node.getOutgoingEdges().get(0).getTarget())){
                    child = node.getOutgoingEdges().get(0).getTarget();
                }
//                node.setAttribute("color", colorArray[color]);
            }

            if(parent != null && child != null){
                for(Node node: region){
                    dataFlowGraph.removeNode(node);
                }
                Edge edge = parent.addEdgeTo(child);
//                edge.setAttribute("color", colorArray[color]);
            }
            color++;
        }
        return initialDFNodeNumber - dataFlowGraph.getNodeCount();

    }

}
