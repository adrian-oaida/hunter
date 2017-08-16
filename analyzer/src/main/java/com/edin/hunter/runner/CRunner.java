package com.edin.hunter.runner;


import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.graph.Edge;
import com.edin.hunter.graph.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.edin.hunter.graph.DirectedGraph.*;

/**
 * Created by dude on 6/16/17.
 */
public class CRunner extends BaseRunner {
    private static final String compileLibraries = " -lpthread -lm";
    private File executableFile;

    private void log(Object logString){
        if(execOutputStream != null){
            execOutputStream.println(logString.toString());
        }
    }
    public CRunner(String pathToSource, String dependencyDirectory, PrintStream out) throws Exception{
        super();
        this.execOutputStream = out;
        compile(pathToSource, dependencyDirectory);
    }
    public CRunner(String pathToSource, String dependencyDirectory) throws Exception {
        super();
        compile(pathToSource, dependencyDirectory);
    }
    private void compile(String pathToSource, String dependencyDirectory) throws Exception {
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
        log(pb.command());
        try {

            Process p = pb.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            log(builder.toString());
            p.waitFor();

            log(p.exitValue());
        } catch (IOException e) {

            e.printStackTrace();
            throw new Exception("could not compile " + pathToSource);
        }
    }

    @Override
    public void run(String ...programArgs) {
        dataFlowGraph = null;
        staticCallGraph = null;
        dynamicCallGraph = null;

        dataFlowGraph = new DirectedGraph("Data Flow DirectedGraph");
        staticCallGraph = new DirectedGraph("Static Call DirectedGraph");
        dynamicCallGraph = new DirectedGraph("Dynamic Call DirectedGraph");
        List<String> arguments = new ArrayList<>();
        arguments.add(executableFile.getPath());
        arguments.addAll(Arrays.asList(programArgs));

        ProcessBuilder pb = new ProcessBuilder(arguments);
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long byteCount = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            String line;
            String currentStaticBlockId = null;
            while( (line = reader.readLine()) != null){
                byteCount += line.getBytes().length;
                if(line.startsWith("BC")){

                    String[] args = line.replace("BC", "").trim().split("\\|");

                    Node dynamicFromNode = dynamicCallGraph.getOrAddNode(args[0]);
                    dynamicFromNode.setAttribute(ATTR_STATIC_ID, args[1]);

                    Node dynamicToNode = dynamicCallGraph.getOrAddNode(args[2]);


                    dynamicToNode.setAttribute(ATTR_STATIC_ID, args[3]);

                    Edge dynamicEdge = dynamicFromNode.addEdgeTo(dynamicToNode);
//                    dynamicEdge.setAttribute("staticFromTo", args[1] + " -> " + args[3]);

                    Node staticFromNode = staticCallGraph.getOrAddNode(args[1]);
                    Node staticToNode  = staticCallGraph.getOrAddNode(args[3]);

                    staticToNode.associateWithNode(dynamicToNode);
                    dynamicToNode.associateWithNode(staticToNode);

                    Edge staticEdge = staticFromNode.addEdgeTo(staticToNode);

                    if(instructionsAsLabels){
                        dynamicToNode.setAttribute(ATTR_LABEL, String.format("\"%s\"", args[4]));
                        staticToNode.setAttribute(ATTR_LABEL, String.format("\"%s\"", args[4]));
                    }else{
                        dynamicToNode.setAttribute(ATTR_INSTRUCTION, String.format("\"%s\"", args[4]));
                        staticToNode.setAttribute(ATTR_INSTRUCTION, String.format("\"%s\"", args[4]));
                    }
//                    staticEdge.setAttribute("dynamicFromTo", args[0] + " -> " + args[2]);


                }

                if(line.startsWith("DF")){
                    String[] args = line.replace("DF", "").trim().split("\\|");

                    Node fromNode = dataFlowGraph.getOrAddNode(Integer.parseInt(args[0]));

                    Node toNode   = dataFlowGraph.getOrAddNode(Integer.parseInt(args[1]));
                    toNode.setAttribute(DirectedGraph.ATTR_STATIC_ID, args[2]);

                    fromNode.addEdgeTo(toNode);

                    if(instructionsAsLabels){
                        fromNode.setAttribute(ATTR_SHAPE, "box");
                        toNode.setAttribute(ATTR_SHAPE, "box");
                    }
                    if(augmentDataFlowNodes){
                        fromNode.copyAttributesFrom(dynamicCallGraph.getOrAddNode(fromNode.getId()));
                        toNode.copyAttributesFrom(dynamicCallGraph.getOrAddNode(toNode.getId()));

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
