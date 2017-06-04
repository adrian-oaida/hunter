This project contains the test programs used to test the analyzer.
This has the following types of programs:

    - mapper

    - other
    - pipeline
    - reducer
    - wavefront
    
for each of these program we need :

    - the source code;    
    - a sketch of how the pipeline looks like;
    - a description of what the stages are doing;
    - what is the interesting aspect of what this example is capturing; this example has a complex computions for example; 
    - argument why this example is important;
    
code - reason - description - test input data

including in test data:
 
sequential, parallel, examples where there is internal state where there is no external state, within the code we want varying levels of
  ones which are correct instances of pipelines; some which are a bit broken;

for sanity some examples which are strictly not pipelines

