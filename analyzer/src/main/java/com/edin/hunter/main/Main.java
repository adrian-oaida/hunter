package com.edin.hunter.main;


import com.edin.hunter.matcher.PatternMatcher;
import com.edin.hunter.matcher.constrictor.PopularNodesConstrictor;
import com.edin.hunter.matcher.detector.CornerStructuralDetector;
import com.edin.hunter.runner.BaseRunner;
import com.edin.hunter.runner.CRunner;
import com.edin.hunter.ui.RemoteGraphViewer;

import java.util.NoSuchElementException;

/**
 * Created by dude on 6/16/17.
 */
public class Main {


    public static void main(String[] args){


        BaseRunner programRun = null;
        try {
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/other/parallel_jacobi_iteration.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/simple_pipelinstatefe.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/mapper/serial_map_reduce_A.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/other/parallel_jacobi_iteration.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/other/wavefront_A.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");

//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/other/parallel_matrix_multiplication.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
              programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/pipeline_D.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            programRun = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/sorting/serial_bublesort.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");

        } catch (Exception e) {
            e.printStackTrace();
        }
        programRun.augmentDataFlowNodes(true);
//        programRun.setInstructionsAsLabes(true);

//        programRun.run("/Users/dude/edin/msc/hunter/TestPrograms/other/matrix_addition_input", "/tmp/matrix_addition_output");
//        programRun.run("/Users/dude/edin/msc/hunter/TestPrograms/other/matrix_multiplication_input", "/tmp/matrix_multiplication_output");
//        programRun.run("/Users/dude/edin/msc/hunter/TestPrograms/other/map_input_no_obstacle", "/tmp/map_output");


//        programRun.run("/Users/dude/edin/msc/hunter/TestPrograms/sorting/desc_ordered_sort_input", "/tmp/sorting_output");

//        programRun.run("8", "4", "5");

        programRun.run("5", "8");
//        programRun.run("3", "2");
//

        PatternMatcher matcher = new PatternMatcher();
        RemoteGraphViewer graphViewer = new RemoteGraphViewer(programRun.getDataFlowGraph().clone(), programRun.getDataFlowGraph());

        matcher.match(new CornerStructuralDetector(programRun), new PopularNodesConstrictor(programRun));

        graphViewer.startServer();


    }

}
