package com.edin.hunter.main;

import com.edin.hunter.runner.BaseRunner;
import com.edin.hunter.runner.CRunner;
import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;


import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        BaseRunner r = null;
        try {
            r = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/statefullA_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            r = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/pipeline/simple_pipeline.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");
//            r = new CRunner("/Users/dude/edin/msc/hunter/TestPrograms/sorting/serial_bublesort.c", "/Users/dude/edin/msc/hunter/TestPrograms/tools/");

        } catch (Exception e) {
            e.printStackTrace();
        }
        r.run("2", "6");
//        r.run("/Users/dude/edin/msc/hunter/TestPrograms/sorting/random_sort_input", "/Users/dude/edin/msc/hunter/TestPrograms/sorting/random_sort_output");

        Map<String, Graph> graphList = new HashMap<>();

        graphList.put("Data Flow Graph", r.getDataFlowGraph());
        graphList.put("Dynamic Call Graph", r.getDynamicCallGraph());
        graphList.put("Static Call Graph",r.getStaticCallGraph());
        AtomicInteger counter = new AtomicInteger(graphList.size());

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] devices = ge.getScreenDevices();
        GraphicsDevice currentDisplayDevice = ge.getDefaultScreenDevice();
        if(devices.length == 2)
            currentDisplayDevice = devices[1];

        int i = 0;


        for(Map.Entry<String, Graph> g : graphList.entrySet()) {

            final Graph graph = g.getValue();

            JFrame frame = new JFrame(g.getKey());

            Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            // Let the layout work ...
            viewer.disableAutoLayout();
            // Do some work ...
            viewer.enableAutoLayout();

            ViewPanel view = viewer.addDefaultView(false);

            view.setSize((currentDisplayDevice.getDefaultConfiguration().getBounds().width/2), (currentDisplayDevice.getDefaultConfiguration().getBounds().height/2));
            view.resizeFrame((currentDisplayDevice.getDefaultConfiguration().getBounds().width/2), (currentDisplayDevice.getDefaultConfiguration().getBounds().height/2));
            frame.getContentPane().add(view);
            frame.getContentPane().setSize((currentDisplayDevice.getDefaultConfiguration().getBounds().width/2), (currentDisplayDevice.getDefaultConfiguration().getBounds().height/2));

            frame.setSize((currentDisplayDevice.getDefaultConfiguration().getBounds().width/2), (currentDisplayDevice.getDefaultConfiguration().getBounds().height/2));
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
        }




    }
}
