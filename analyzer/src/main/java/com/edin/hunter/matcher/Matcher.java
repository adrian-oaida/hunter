package com.edin.hunter.matcher;

import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.TarjanStronglyConnectedComponents;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import java.util.*;

/**
 * Created by dude on 6/25/17.
 */
public class Matcher {
    private Graph graph;

    /*
    * @param graph a dataflow graph
    *
    * */
    public Matcher(Graph graph){
        this.graph = graph;
        markUpGraph();
    }
    public Graph getGraph(){


        return graph;
    }
    /*
    *This returns a new graph based on the existent graph but with pipeline stage and latices highlighted
    *
    *
    * @return
    * */
    public Graph detect(){
        detectLatice();
        detectPipelineStages();

        return graph;
    }
    //TODO lattice highlighter
    //latice definition
    private boolean detectLatice(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        APSP apsp = new APSP();
        apsp.init(graph);
        apsp.setDirected(true);
        apsp.setWeightAttributeName("weight");
        apsp.compute();

        APSP.APSPInfo info = start.getAttribute(APSP.APSPInfo.ATTRIBUTE_NAME);
        System.out.println(info.getMaximumLength());
        System.out.println(info.getMinimumLength());
        System.out.println(info.getShortestPathTo(finish.getId()));

//        Path path = info.getShortestPathTo(finish.getId());
//        for(Edge e : path.getEachEdge()){
//            e.setAttribute("ui.class", "visited");
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
//        }
        System.out.println("Clustering coeficient " + Toolkit.averageClusteringCoefficient(graph));
        System.out.println("Average degree " + Toolkit.averageDegree(graph));
        System.out.println("Degree average deviation " + Toolkit.degreeAverageDeviation(graph));
        System.out.println("Density " + Toolkit.density(graph));
        System.out.println("Diameter " + Toolkit.diameter(graph));

        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");
//        dijkstra.init(graph);
//        dijkstra.setSource(start);
//        dijkstra.compute();
//        for(Path p : dijkstra.getAllPaths(finish)){
//            List<Edge> edgePath = p.getEdgePath();
//            graph.removeEdge(edgePath.get(edgePath.size() - 1));
//            break;
//        }
//        dijkstra.clear();
        dijkstra.init(graph);
        dijkstra.setSource(start);
        dijkstra.compute();

        for(Path p : dijkstra.getAllPaths(finish)){
            System.out.println("Path length " + p.getNodeCount());
            for(Edge e: p.getEdgePath()){
                if(e.getAttribute("ui.class") != "visited"){
                    e.setAttribute("ui.class", "visited");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        }
//        TarjanStronglyConnectedComponents tscc = new TarjanStronglyConnectedComponents();
//        tscc.init(graph);
//        tscc.compute();
//
//        for (Node n : graph.getEachNode())
//            n.addAttribute("label",""+ n.getAttribute(tscc.getSCCIndexAttribute()));

//        for(Edge e: dijkstra.getTreeEdges()){
//            e.setAttribute("ui.class", "visited");
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
//
//        }

        return false;
    }

    private boolean detectPipelineStages(){
        //look for the maximum number of subgraphs that have the same structure and link to each other
        //

        return false;
    }
    private Node start;
    private Node finish;

    private void markUpGraph(){
        for(Node n : graph){
            if(n.getInDegree() == 0){
                start =  n;
                n.setAttribute("ui.class", "root");
            }
            if(n.getOutDegree() == 0){
                finish = n;
                n.setAttribute("ui.class", "leaf");
            }
            if(n.getEdgeToward(n) != null){
                if(n.getInDegree() - 1 == 0){
                    n.setAttribute("ui.class", "root");
                }
                if(n.getOutDegree() - 1 == 0){
                    n.setAttribute("ui.class", "leaf");
                }
            }
        }
    }
}
