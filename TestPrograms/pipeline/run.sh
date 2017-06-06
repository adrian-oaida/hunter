#/bin/sh
timestamp(){
  date +"%s"
}
t=`timestamp`

rm -f pipeline_run
rm -f temp_generated_*

gcc ../tools/*.c $1 -o pipeline_run -lpthread -lm
./pipeline_run $2 $3 > temp_generated_$t
../tools/makedotgraph.sh temp_generated_$t
dot -Tpng temp_generated_$t.gv -o pipeline_run_data.png > pipeline_run_calls.png
