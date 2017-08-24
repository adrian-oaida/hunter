package com.edin.hunter.annotator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dude on 6/23/17.
 */
public class Parser {
    private String pathToSource;
    private String dependencyDirectory;
    public Parser(String pathToSource, String dependencyDirectory){
        this.pathToSource = pathToSource;
        this.dependencyDirectory = dependencyDirectory;
    }
    public void compile(){
        List<String> command = new ArrayList<>();

        File dependecyDirectoryFile = new File(dependencyDirectory);
        for(File f: dependecyDirectoryFile.listFiles()){
            if(f.getName().endsWith(".c")){
                command.add(f.getAbsolutePath());
            }
        }
    }
    public void parse(){
        StringBuilder compileSourceFilesSB = new StringBuilder();


        List<String> command = new ArrayList<>();
        command.add("clang");
        command.add("-cc1");
        command.add("-ast-dump");
        command.add(pathToSource);


        ProcessBuilder pb = new ProcessBuilder(command);
        System.out.println(pb.command());
        String astString = null;
        try {

            Process p = pb.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            astString = builder.toString();
            p.waitFor();
            System.out.println(p.exitValue());
        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(astString);
        ArrayList<String> functions = new ArrayList<>();
    }
}
