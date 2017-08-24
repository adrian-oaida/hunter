package com.edin.hunter.main;

import com.edin.hunter.matcher.PatternMatcher;
import com.edin.hunter.matcher.constrictor.DIINConstrictor;
import com.edin.hunter.matcher.constrictor.DINConstrictor;
import com.edin.hunter.matcher.constrictor.MCINConstrictor;
import com.edin.hunter.matcher.detector.OLMDetector;
import com.edin.hunter.matcher.detector.SLMDetector;
import com.edin.hunter.runner.BaseRunner;
import com.edin.hunter.runner.CRunner;

import java.util.HashMap;
import java.util.Map;

public class AccuracyRunner {

    private static String sampleSuiteBase = "/Users/dude/edin/msc/hunter/SampleSuite/";
    private static String sampleSuiteDependenciesDirectory = sampleSuiteBase + "tools/";


    private static void addPipelineSuite(Map<String, String[]> sampleSuite){
        sampleSuite.put("pipeline/pipeline_A.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_B.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_C.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_D.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_E.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_F.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_G.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_H.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_I.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_J.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_K.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_L.c", new String[]{"8", "4"});
        sampleSuite.put("pipeline/pipeline_M.c", new String[]{"8", "4"});
    }
    private static void addOtherSuite(Map<String, String[]> sampleSuite){
        sampleSuite.put("mapper/map_reduce_A.c", new String[]{"30"});
        sampleSuite.put("other/parallel_jacobi_iteration.c", new String[]{"4", "4", "5"});
        sampleSuite.put("other/serial_matrix_addition.c", new String[]{sampleSuiteBase+"other/matrix_addition_input", sampleSuiteBase+"other/matrix_addition_output"});
        sampleSuite.put("other/parallel_matrix_addition.c", new String[]{sampleSuiteBase+"other/matrix_addition_input", sampleSuiteBase+"other/matrix_addition_output"});
        sampleSuite.put("other/serial_matrix_multiplication.c", new String[]{sampleSuiteBase+"other/matrix_multiplication_input", sampleSuiteBase+"other/matrix_multiplication_output"});
        sampleSuite.put("other/parallel_matrix_multiplication.c", new String[]{sampleSuiteBase+"other/matrix_multiplication_input", sampleSuiteBase+"other/matrix_multiplication_output"});
        sampleSuite.put("other/wavefront_A.c", new String[]{sampleSuiteBase+"other/map_input", sampleSuiteBase+"other/map_output"});
    }
    private static void addSortingSuite(Map<String, String[]> sampleSuite){
        sampleSuite.put("sorting/serial_bublesort.c", new String[]{sampleSuiteBase+"sorting/desc_ordered_sort_input", sampleSuiteBase+"sorting/desc_ordered_sort_output"});
        sampleSuite.put("sorting/serial_bublesort.c", new String[]{sampleSuiteBase+"sorting/ordered_sort_input", sampleSuiteBase+"sorting/ordered_sort_output"});
        sampleSuite.put("sorting/serial_bublesort.c", new String[]{sampleSuiteBase+"sorting/random_sort_input", sampleSuiteBase+"sorting/random_sort_output"});

        sampleSuite.put("sorting/serial_heapsort.c", new String[]{sampleSuiteBase+"sorting/desc_ordered_sort_input", sampleSuiteBase+"sorting/desc_ordered_sort_output"});
        sampleSuite.put("sorting/serial_heapsort.c", new String[]{sampleSuiteBase+"sorting/ordered_sort_input", sampleSuiteBase+"sorting/ordered_sort_output"});
        sampleSuite.put("sorting/serial_heapsort.c", new String[]{sampleSuiteBase+"sorting/random_sort_input", sampleSuiteBase+"sorting/random_sort_output"});

        sampleSuite.put("sorting/serial_mergesort.c", new String[]{sampleSuiteBase+"sorting/desc_ordered_sort_input", sampleSuiteBase+"sorting/desc_ordered_sort_output"});
        sampleSuite.put("sorting/serial_mergesort.c", new String[]{sampleSuiteBase+"sorting/ordered_sort_input", sampleSuiteBase+"sorting/ordered_sort_output"});
        sampleSuite.put("sorting/serial_mergesort.c", new String[]{sampleSuiteBase+"sorting/random_sort_input", sampleSuiteBase+"sorting/random_sort_output"});

        sampleSuite.put("sorting/serial_heapsort.c", new String[]{sampleSuiteBase+"sorting/desc_ordered_sort_input", sampleSuiteBase+"sorting/desc_ordered_sort_output"});
        sampleSuite.put("sorting/serial_heapsort.c", new String[]{sampleSuiteBase+"sorting/ordered_sort_input", sampleSuiteBase+"sorting/ordered_sort_output"});
        sampleSuite.put("sorting/serial_heapsort.c", new String[]{sampleSuiteBase+"sorting/random_sort_input", sampleSuiteBase+"sorting/random_sort_output"});
    }
    public static void main(String[] args){

        Map<String, String[]> sampleSuite = new HashMap<>();

        addPipelineSuite(sampleSuite);

//        addOtherSuite(sampleSuite);
//
//        addSortingSuite(sampleSuite);

        for(Map.Entry<String, String[]> sample : sampleSuite.entrySet()){
            BaseRunner programRun = null;
            try {
                long startTime = System.nanoTime();
                long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                programRun = new CRunner(sampleSuiteBase + sample.getKey(), sampleSuiteDependenciesDirectory);

                programRun.run(sample.getValue());
                long totalNodeCount = programRun.getDataFlowGraph().getNodeCount() - 1;
                float execTime, execMemory, analysisTime, analysisMemory;
                execTime = (float)(System.nanoTime() - startTime) / (1000000000);
                execMemory = (float)((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) - startMemory)/(1024 * 1024);

                System.gc();

                startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                startTime = System.nanoTime();


                PatternMatcher matcher = new PatternMatcher(programRun);
                boolean found = matcher.match(new OLMDetector(programRun), new DIINConstrictor(programRun));

                analysisTime = (float)(System.nanoTime() - startTime) / (1000000000);
                analysisMemory = execMemory + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) - startMemory)/(1024 * 1024);

                System.out.printf("%s, %s, %d, %f, %f, %f, %f\n", sample.getKey().split("/")[1].split("\\.")[0],
                        found, totalNodeCount, execTime, analysisTime, execMemory, analysisMemory);
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
            programRun.augmentDataFlowNodes(true);
            programRun.setInstructionsAsLabes(true);

        }



    }
}
