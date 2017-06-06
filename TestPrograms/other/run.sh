#/bin/sh
timestamp(){
  date +"%s"
}
t=`timestamp`

rm -f other_run
rm -f temp_generated_*

gcc ../tools/*.c $1 -o other_run -lpthread -lm
./other_run $2 $3 > temp_generated_$t
../tools/makedotgraph.sh temp_generated_$t
dot -Tpng temp_generated_$t.gv -o run_data.png > run_calls.png
