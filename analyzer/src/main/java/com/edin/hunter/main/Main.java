package com.edin.hunter.main;


import com.edin.hunter.matcher.BasicMatcher;
import com.edin.hunter.matcher.LatticeGenerator;
import com.edin.hunter.matcher.MatrixMatcher;
import com.edin.hunter.matcher.StageMatcher;
import com.edin.hunter.runner.BaseRunner;
import com.edin.hunter.runner.CRunner;
import com.edin.hunter.ui.GraphViewer;

/**
 * Created by dude on 6/16/17.
 */
public class Main {


    public static void main(String[] args){


        BaseRunner programRun = null;
        try {
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/other/parallel_jacobi_iteration.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/simple_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/statefullA_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");

        } catch (Exception e) {
            e.printStackTrace();
        }
//        programRun.run("10", "5", "6");

        programRun.run("4", "6");

        programRun.getDataFlowGraph().removeNode(0);

//        produceConsumerRunner.run("20", "");





        GraphViewer viewer = new GraphViewer(programRun.getDataFlowGraph());
        BasicMatcher matcher = new StageMatcher(programRun.getDataFlowGraph());

        viewer.periodicUpdateView(100);

        viewer.displayGraph();
        matcher.detect();




//        LatticeGenerator generator = new LatticeGenerator(5 , 8);
//        generator.generateLattice();
//        generator.displayLattice();

    }
}
