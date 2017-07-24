package com.edin.hunter.main;


import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.matcher.BackwardStageMatcher;
import com.edin.hunter.matcher.BaseMatcher;
import com.edin.hunter.matcher.StageMatcher;
import com.edin.hunter.runner.BaseRunner;
import com.edin.hunter.runner.CRunner;
import com.edin.hunter.ui.GraphViewer;
import com.edin.hunter.ui.RemoteGraphViewer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Created by dude on 6/16/17.
 */
public class Main {


    public static void main(String[] args){


        BaseRunner programRun = null;
        try {
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/other/parallel_jacobi_iteration.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/simple_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/other/parallel_jacobi_iteration.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/statefullA_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");

        } catch (Exception e) {
            e.printStackTrace();
        }
//        programRun.run("/Users/dude/edin/msc/hunter/TestPrograms/other/matrix_addition_input", "/tmp/matrix_addition_output");
//        programRun.run("/Users/dude/edin/msc/hunter/TestPrograms/other/matrix_multiplication_input", "/tmp/matrix_multiplication_output");

//        programRun.run("5", "2", "3");

        programRun.run("4", "6");

        programRun.getDataFlowGraph().removeNode(0);

//        produceConsumerRunner.run("20", "");





//        GraphViewer viewer = new GraphViewer(programRun.getDataFlowGraph());
//        viewer.periodicUpdateView(100);
//
//        viewer.displayGraph();



        BaseMatcher matcher = new BackwardStageMatcher(programRun.getDataFlowGraph());
        RemoteGraphViewer viewer = new RemoteGraphViewer(matcher.getGraph());
        viewer.startServer();
        matcher.detect();


//        LatticeGenerator generator = new LatticeGenerator(5 , 8);
//        generator.generateLattice();
//        generator.displayLattice();

    }

}
