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

/**
 * Created by dude on 7/7/17.
 */
public class GraphViewer {
    private Graph graph;
    private JPanel graphPanel;
    private JFrame graphFrame;
    private Image graphImage;
    private Dimension graphImageSize;
    public GraphViewer(Graph graph){
        this.graph = graph;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] devices = ge.getScreenDevices();
        GraphicsDevice currentDisplayDevice = ge.getDefaultScreenDevice();
        if(devices.length == 2)
            currentDisplayDevice = devices[1];

        graphImageSize = new Dimension((currentDisplayDevice.getDefaultConfiguration().getBounds().width/2),
                (currentDisplayDevice.getDefaultConfiguration().getBounds().height)/2);
        graphPanel = new JPanel();
        graphPanel.setSize(graphImageSize);


        this.updateView();

    }
    public void updateView(){
        String graphAsDot = graph.toDotString();

        System.out.println(graphAsDot);
        List<String> command = new ArrayList<>();
        command.add("dot");
        command.add("-Tpng");


        ProcessBuilder pb = new ProcessBuilder(command);
        System.out.println(pb.command());
        pb.redirectErrorStream();

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

        graphPanel.removeAll();

        ImageIcon image = new ImageIcon(graphImage.getScaledInstance(graphImageSize.width, graphImageSize.height, Image.SCALE_FAST));

        System.out.println(graphImageSize);
        graphPanel.add(new JLabel(image));

    }
    public JPanel getGraphPanel(){
        return graphPanel;
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
        graphFrame.setLocation(0, 0);
            graphFrame.pack();
        graphFrame.setVisible(true);
        System.out.println("should have displayed");

    }
}
