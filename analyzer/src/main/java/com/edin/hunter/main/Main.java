package com.edin.hunter.main;

import com.edin.hunter.matcher.Matcher;
import com.edin.hunter.runner.BaseRunner;
import com.edin.hunter.runner.CRunner;
import com.edin.hunter.runner.Graph;

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
//            statefullAPipelineRunner = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/statefullA_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
            statefullBPipelineRunner = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/statefullC_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            simplePipelineRunner = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/simple_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            r = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/sorting/serial_bublesort.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//        r.run("/Users/dude/edin/msc/hunter/TestPrograms/sorting/random_sort_input", "/Users/dude/edin/msc/hunter/TestPrograms/sorting/random_sort_output");

//            produceConsumerRunner = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/other/producer_consumer.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");

        } catch (Exception e) {
            e.printStackTrace();
        }
//        simplePipelineRunner.run("5", "5");
//        statefullAPipelineRunner.run("5", "5");
        statefullBPipelineRunner.run("3", "6");

//        produceConsumerRunner.run("20", "");

        Map<String, Graph> graphList = new HashMap<>();

        statefullBPipelineRunner.getDataFlowGraph().removeNode(0);


//        graphList.put("Simple pipeline", simplePipelineRunner.getDataFlowGraph());
//        graphList.put("Stateful A pipeline", statefullAPipelineRunner.getDataFlowGraph());
        graphList.put("Stateful B pipeline", statefullBPipelineRunner.getDataFlowGraph());

//        graphList.put("Simple pipeline call graph", simplePipelineRunner.getDynamicCallGraph());
//        graphList.put("Stateful A pipeline call graph", statefullAPipelineRunner.getDynamicCallGraph());
//        graphList.put("Stateful B pipeline call graph", statefullBPipelineRunner.getDynamicCallGraph());

//        graphList.put("Producer Consumer Dataflow", produceConsumerRunner.getDataFlowGraph());
//
//        graphList.put("Producer Consumer Dynamic Call flow", produceConsumerRunner.getDynamicCallGraph());
//        graphList.put("Producer Consumer Static Call flow", produceConsumerRunner.getStaticCallGraph());


        AtomicInteger counter = new AtomicInteger(graphList.size());
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();
        GraphicsDevice currentDisplayDevice = ge.getDefaultScreenDevice();
        if(devices.length == 2)
            currentDisplayDevice = devices[0];

        int i = 0;

        for(Map.Entry<String, Graph> g : graphList.entrySet()) {

            Matcher matcher = new Matcher(g.getValue());

            JFrame frame = new JFrame(g.getKey());

            if(graphList.entrySet().size() == 1){
                frame.getContentPane().setSize((currentDisplayDevice.getDefaultConfiguration().getBounds().width), (currentDisplayDevice.getDefaultConfiguration().getBounds().height));
                frame.setSize((currentDisplayDevice.getDefaultConfiguration().getBounds().width), (currentDisplayDevice.getDefaultConfiguration().getBounds().height));

            }else{
                frame.getContentPane().setSize((currentDisplayDevice.getDefaultConfiguration().getBounds().width/2), (currentDisplayDevice.getDefaultConfiguration().getBounds().height/2));
                frame.setSize((currentDisplayDevice.getDefaultConfiguration().getBounds().width/2), (currentDisplayDevice.getDefaultConfiguration().getBounds().height/2));
            }
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setAutoRequestFocus(true);

            frame.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                    if(e.getKeyChar() == 'q'){
                        ((JFrame)e.getSource()).dispose();
                        if(counter.decrementAndGet() == 0)
                            System.exit(0);

                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
            frame.setLocation( currentDisplayDevice.getDefaultConfiguration().getBounds().x + (currentDisplayDevice.getDefaultConfiguration().getBounds().width/2) * ((i & 2) >> 1), currentDisplayDevice.getDefaultConfiguration().getBounds().y + (int)(currentDisplayDevice.getDefaultConfiguration().getBounds().height/2) * (i & 0x11));
//            frame.pack();
            frame.setVisible(true);
            i++;
            matcher.detect();
        }




    }
}
