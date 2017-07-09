package com.edin.hunter.main;


import com.edin.hunter.matcher.LatticeGenerator;
import com.edin.hunter.matcher.Matcher;
import com.edin.hunter.runner.BaseRunner;
import com.edin.hunter.runner.CRunner;
import com.edin.hunter.runner.Graph;
import com.edin.hunter.ui.GraphViewer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dude on 6/16/17.
 */
public class Main {


    public static void main(String[] args){


        BaseRunner simplePipelineRunner = null,
                statefullAPipelineRunner = null,
                statefullBPipelineRunner = null,
                produceConsumerRunner = null;
        try {
            statefullBPipelineRunner = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/statefullC_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");

        } catch (Exception e) {
            e.printStackTrace();
        }
        statefullBPipelineRunner.run("3", "6");
        statefullBPipelineRunner.getDataFlowGraph().removeNode(0);
//
////        produceConsumerRunner.run("20", "");
//
//        Map<String, Graph> graphList = new HashMap<>();
//
//
//        graphList.put("Stateful B pipeline", statefullBPipelineRunner.getDataFlowGraph());


//        for(Map.Entry<String, Graph> g : graphList.entrySet()) {
//
//            Matcher matcher = new Matcher(g.getValue());
//

        GraphViewer viewer = new GraphViewer(statefullBPipelineRunner.getDataFlowGraph());
        viewer.periodicUpdateView(250);
        viewer.displayGraph();

        Matcher matcher = new Matcher(statefullBPipelineRunner.getDataFlowGraph());


        matcher.detect();



        LatticeGenerator generator = new LatticeGenerator(3 , 6);
        generator.generateLattice();
        generator.displayLattice();


    }
}
