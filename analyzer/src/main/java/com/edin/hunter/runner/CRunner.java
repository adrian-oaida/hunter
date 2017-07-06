package com.edin.hunter.runner;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dude on 6/16/17.
 */
public class CRunner extends BaseRunner {
    private static final String compileLibraries = " -lpthread -lm";
    private File executableFile;

    public CRunner(String pathToSource, String dependencyDirectory) throws Exception {
        super();


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

            Process p = pb.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            System.out.println(builder.toString());
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
    public void run(String ...programArgs) {
        dataFlowGraph = new Graph("Data Flow Graph");
        staticCallGraph = new Graph("Static Call Graph");
        dynamicCallGraph = new Graph("Dynamic Call Graph");
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            String line;
            String currentStaticBlockId = null;
            while( (line = reader.readLine()) != null){
                if(line.startsWith("BC")){

                    List<Integer> args = Arrays.stream(line.replace("BC", "").trim().split(" ")).map(Integer::parseInt).collect(Collectors.toList());

                    Node dynamicFromNode = dynamicCallGraph.getOrAddNode(args.get(0));
                    Node dynamicToNode = dynamicCallGraph.getOrAddNode(args.get(2));

                    Edge dynamicEdge = dynamicFromNode.addEdgeTo(dynamicToNode);
                    dynamicEdge.setAttribute("staticFromTo", args.get(1) + " -> " + args.get(3));

                    Node staticFromNode = staticCallGraph.getOrAddNode(args.get(1));
                    Node staticToNode  = staticCallGraph.getOrAddNode(args.get(3));

                    Edge staticEdge = staticFromNode.addEdgeTo(staticToNode);
                    staticEdge.setAttribute("dynamicFromTo", args.get(0) + " -> " + args.get(2));


                }
                //TODO add data flow attribute of containing basic block
                if(line.startsWith("DF")){
                    List<Integer> args = Arrays.stream(line.replace("DF", "").trim().split(" ")).map(elem -> Integer.parseInt(elem)).collect(Collectors.toList());

                    Node fromNode = dataFlowGraph.getOrAddNode(args.get(0));
                    Node toNode   = dataFlowGraph.getOrAddNode(args.get(1));
                    fromNode.addEdgeTo(toNode);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
