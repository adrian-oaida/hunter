package com.edin.hunter.main;

import com.edin.hunter.matcher.PatternMatcher;
import com.edin.hunter.matcher.constrictor.DINConstrictor;
import com.edin.hunter.matcher.detector.OLMDetector;
import com.edin.hunter.matcher.detector.SLMDetector;
import com.edin.hunter.runner.BaseRunner;
import com.edin.hunter.runner.CRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class BenchmarkRunner {
    private static String projectBase = "/Users/dude/edin/msc/hunter/";

    private static String sampleSuiteBase = projectBase + "SampleSuite/";
    private static String sampleSuiteDependenciesDirectory = sampleSuiteBase + "tools/";
    public static void main(String[] args) throws Exception {

        String[] samplePrograms = { "pipeline/pipeline_A.c", "pipeline/pipeline_E.c", "pipeline/pipeline_G.c"};

        int[] dataSize = {2, 10, 100, 1000};
        int[] workerSize = {2, 5, 10, 15, 20};
        String benchmarkedConfiguration = "olm_din";
        for(String sample : samplePrograms){
            System.gc();
            BufferedWriter execWriter = null;
            BufferedWriter analysisWriter = null;
            execWriter = new BufferedWriter(new FileWriter(new File(projectBase + "benchmark/"+benchmarkedConfiguration+"/exec_log_" + sample.split("/")[1].split("\\.")[0])));
            analysisWriter = new BufferedWriter(new FileWriter(new File(projectBase + "benchmark/"+ benchmarkedConfiguration +"/analysis_log_" + sample.split("/")[1].split("\\.")[0])));
            BaseRunner programRun = null;

            programRun = new CRunner(sampleSuiteBase + sample, sampleSuiteDependenciesDirectory);

            for(int i = 0; i < dataSize.length; i++){
                for(int j = 0; j < workerSize.length; j++){
                    System.gc();
                    long startTime = System.nanoTime();
                    long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                    programRun.run(dataSize[i] + "", workerSize[j] + "");

                    long totalNodeCount = programRun.getDataFlowGraph().getNodeCount() - 1;
                    float execTime, execMemory, analysisTime, analysisMemory;
                    execTime = (float)(System.nanoTime() - startTime) / (1000000000);
                    execMemory = (float)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) - startMemory)/(1024 * 1024);

                    System.gc();
                    execWriter.write(totalNodeCount + "\t " + execTime + "\t " + execMemory + "\n");

                    startTime = System.nanoTime();

                    PatternMatcher matcher = new PatternMatcher(programRun);
                    boolean found = matcher.match(new OLMDetector(programRun), new DINConstrictor(programRun));
                    System.out.println("found " + found);

                    analysisTime = (float)(System.nanoTime() - startTime) / (1000000000);
                    analysisMemory =  ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) - startMemory)/(1024 * 1024);

                    analysisWriter.write(totalNodeCount + "\t " + analysisTime + "\t " + analysisMemory + "\n");
                    System.gc();
                    execWriter.flush();
                    analysisWriter.flush();
                }
            }
            execWriter.close();
            analysisWriter.close();
        }


    }
}
