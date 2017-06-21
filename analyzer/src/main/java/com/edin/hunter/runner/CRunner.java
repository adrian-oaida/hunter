package com.edin.hunter.runner;



import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.MultiNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dude on 6/16/17.
 */
public class CRunner extends BaseRunner {
    private static final String compileLibraries = " -lpthread -lm";
    private File executableFile;

    public CRunner(String pathToSource, String dependencyDirectory) throws Exception {
        super();

        StringBuilder compileSourceFilesSB = new StringBuilder();



        try {
            executableFile = File.createTempFile("runner", "run");
            executableFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }


        List<String> command = new ArrayList<>();
        command.add("gcc");

        File dependecyDirectoryFile = new File(dependencyDirectory);
        for(File f: dependecyDirectoryFile.listFiles()){
            if(f.getName().endsWith(".c")){
                command.add(f.getAbsolutePath());
            }
        }
        command.add(pathToSource);
        command.add("-o");
        command.add(executableFile.getAbsolutePath());
        command.add("-lpthread");
        command.add("-lm");
        ProcessBuilder pb = new ProcessBuilder(command);
        System.out.println(pb.command());
        try {

            pb.redirectError(new File("/tmp/runner.error"));
            pb.redirectOutput(new File("/tmp/runner.output"));

            Process p = pb.start();

            p.waitFor();

            System.out.println(p.exitValue());
        } catch (IOException e) {

            e.printStackTrace();
            throw new Exception("could not compile " + pathToSource);
        }

    }
    @Override
    public void finalize(){
    }
    @Override
    public void run(String firstArgument, String secondArgument) {
        dataFlowGraph = new MultiGraph("Data Flow Graph");
        staticCallGraph = new MultiGraph("Static Call Graph");
        dynamicCallGraph = new MultiGraph("Dynamic Call Graph");

        ProcessBuilder pb = new ProcessBuilder(executableFile.getPath(), firstArgument, secondArgument);
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            String line;
            int staticEdgeCounter = 1;
            int dynamicEdgeCounter = 1;
            int dataFlowEdgeCounter = 1;
            while( (line = reader.readLine()) != null){
                if(line.startsWith("BC")){
                    String[] args = line.replace("BC", "").trim().split(" ");

                    if(dynamicCallGraph.getNode(args[0]) == null)
                        dynamicCallGraph.addNode(args[0]);
                    if(dynamicCallGraph.getNode(args[2]) == null)
                        dynamicCallGraph.addNode(args[2]);

                    Edge dynamicEdge = dynamicCallGraph.addEdge("" + dynamicEdgeCounter++, args[0], args[2], true);
                    dynamicEdge.addAttribute("fromTo", args[1], args[3]);

                    if(staticCallGraph.getNode(args[1]) == null)
                        staticCallGraph.addNode(args[1]);
                    if(staticCallGraph.getNode(args[3]) == null)
                        staticCallGraph.addNode(args[3]);

                    Edge staticEdge = staticCallGraph.addEdge("" + staticEdgeCounter++, args[1], args[3], true);
                    staticEdge.addAttribute("fromTo", args[0], args[2]);

                }
                //TODO add data flow attribute of containing basic block
                if(line.startsWith("DF")){
                    String[] args = line.replace("DF", "").trim().split(" ");

                    if(dataFlowGraph.getNode(args[0]) == null)
                        dataFlowGraph.addNode(args[0]);
                    if(dataFlowGraph.getNode(args[1]) == null)
                        dataFlowGraph.addNode(args[1]);

                    dataFlowGraph.addEdge("" + dataFlowEdgeCounter++, args[0], args[1], true);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
