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
    public static void main(String[] args){
        StringBuilder compileSourceFilesSB = new StringBuilder();

        String pathToSource = "/Users/dude/edin/msc/hunter/TestPrograms/sorting/vanilla_serial_mergesort.c";

        String dependencyDirectory = "";

        List<String> command = new ArrayList<>();
        command.add("clang");
        command.add("-cc1");
        command.add("-ast-dump");
        command.add(pathToSource);


//        File dependecyDirectoryFile = new File(dependencyDirectory);
//        for(File f: dependecyDirectoryFile.listFiles()){
//            if(f.getName().endsWith(".c")){
//                command.add(f.getAbsolutePath());
//            }
//        }

        ProcessBuilder pb = new ProcessBuilder(command);
        System.out.println(pb.command());
        try {


//            pb.redirectError(new File("/tmp/runner.error"));
//            pb.redirectOutput(new File("/tmp/runner.output"));

            Process p = pb.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            p.waitFor();
            System.out.println(result);
            System.out.println(p.exitValue());
        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
