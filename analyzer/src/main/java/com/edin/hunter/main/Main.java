package com.edin.hunter.main;


import com.edin.hunter.matcher.PatternMatcher;
import com.edin.hunter.matcher.constrictor.DIINConstrictor;
import com.edin.hunter.matcher.constrictor.DINConstrictor;
import com.edin.hunter.matcher.constrictor.MCINConstrictor;
import com.edin.hunter.matcher.detector.OLMDetector;
import com.edin.hunter.matcher.detector.SLMDetector;
import com.edin.hunter.runner.BaseRunner;
import com.edin.hunter.runner.CRunner;
import com.edin.hunter.ui.GraphViewer;
import com.edin.hunter.ui.RemoteGraphViewer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by dude on 6/16/17.
 */
public class Main {


    public static void main(String[] args) throws IOException {
        String projectBase = "/Users/dude/edin/msc/hunter/";
        String sampleSuiteBase = projectBase + "SampleSuite/";


        String sampleName = "pipeline/pipeline_K.c";


        BaseRunner programRun = null;
        try {
            programRun = new CRunner(sampleSuiteBase + sampleName, sampleSuiteBase + "tools/", System.out);
            programRun.setInstructionsAsLabes(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        programRun.augmentDataFlowNodes(true);
//        OLM detector = null;

                programRun.run("8", "4");

                PatternMatcher matcher = new PatternMatcher(programRun);
                System.out.println(matcher.match(new SLMDetector(programRun), new DINConstrictor(programRun)));
//        programRun.getDataFlowGraph().removeNode(0);
//        GraphViewer viewer = new GraphViewer(detector.getDynamicCallGraph());
//        viewer.displayGraph();
//        programRun.getDataFlowGraph().removeNode(0);

//        GraphViewer viewer2 = new GraphViewer(programRun.getDataFlowGraph());
//        viewer2.displayGraph();
        RemoteGraphViewer graphViewer = new RemoteGraphViewer(programRun.getDataFlowGraph(), programRun.getDataFlowGraph());
        graphViewer.startServer();
//
//        execWriter.close();
//        analysisWriter.close();
////        LatticeGenerator generator = new LatticeGenerator(4, 8);
//        generator.generateLattice();
//        generator.displayLattice();
    }

}
