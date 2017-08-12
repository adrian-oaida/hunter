This directory contains programs that use the pipeline model
* Simple pipeline - this program is used for sanity checks on variable tracing

``` ./run.sh simple_pipeline.c 2 8```

The run.sh script accepts the desired program to be run an its parameters and it output two png images.
The script recompiles the inputted program source file.
* pipeline_run_calls.png is a call graph
* pipeline_run_data.png is a data flow graph

