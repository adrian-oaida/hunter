package com.edin.hunter.ui;

import com.edin.hunter.runner.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by dude on 7/7/17.
 */
public class GraphViewer {
    private Graph graph;
    private JPanel graphPanel;
    private JFrame graphFrame;
    private Dimension graphImageSize;
    private GraphicsDevice currentDisplayDevice;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;


    public GraphViewer(Graph graph){
        this.graph = graph;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] devices = ge.getScreenDevices();
        currentDisplayDevice = ge.getDefaultScreenDevice();
        if(devices.length == 2)
            currentDisplayDevice = devices[0];

        graphImageSize = new Dimension((currentDisplayDevice.getDefaultConfiguration().getBounds().width/2),
                (currentDisplayDevice.getDefaultConfiguration().getBounds().height)/2);
        graphPanel = new JPanel();
        graphPanel.setSize(graphImageSize);

        scheduler = Executors.newScheduledThreadPool(1);

        scheduler.schedule(new GraphUIUpdater(graph, graphPanel), 0, TimeUnit.MILLISECONDS);

    }

    public JPanel getGraphPanel(){
        return graphPanel;
    }

    private class GraphUIUpdater implements Runnable{
        private Graph graph;
        private JPanel panel;
        public GraphUIUpdater(Graph graph, JPanel panel){
            this.graph = graph;
            this.panel = panel;
        }
        @Override
        public void run() {
            String graphAsDot = graph.toDotString();

            //System.out.println(graphAsDot);
            List<String> command = new ArrayList<>();
            command.add("dot");
            command.add("-Tpng");


            ProcessBuilder pb = new ProcessBuilder(command);
            //System.out.println(pb.command());
            pb.redirectErrorStream();
            Image graphImage = null;
            try {

                Process p = pb.start();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

                writer.write(graphAsDot);
                writer.close();

                graphImage = ImageIO.read(p.getInputStream());
                p.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            panel.removeAll();

            ImageIcon image = new ImageIcon(graphImage.getScaledInstance(graphImageSize.width, graphImageSize.height, Image.SCALE_FAST));

            panel.add(new JLabel(image));
            panel.updateUI();
        }
    }

    public void periodicUpdateView(int millis){
        if(scheduledFuture != null){
            scheduledFuture.cancel(false);
        }
        scheduledFuture = scheduler.scheduleAtFixedRate(new GraphUIUpdater(graph, graphPanel), 0, millis, TimeUnit.MILLISECONDS);
    }
    public void displayGraph(){


        graphFrame = new JFrame(graph.getName());
        graphFrame.add(graphPanel);

        graphFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphFrame.setAutoRequestFocus(true);


        graphFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

                if(e.getKeyChar() == 'q'){
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

        graphFrame.getContentPane().setSize(graphImageSize);
        graphFrame.setSize(graphImageSize);
        graphFrame.setLocation(currentDisplayDevice.getDefaultConfiguration().getBounds().x, currentDisplayDevice.getDefaultConfiguration().getBounds().y);
//        graphFrame.pack();
        graphFrame.setVisible(true);
        System.out.println("should have displayed");

    }
}
