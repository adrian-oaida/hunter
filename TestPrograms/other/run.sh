#/bin/sh
timestamp(){
  date +"%s"
}
t=`timestamp`

rm -f other_run
rm -f temp_generated_*

run_file=${1%".c"}

gcc ../tools/*.c $1 -o other_run -lpthread -lm
./other_run ${@:2} > temp_generated_$t
../tools/makedotgraph.sh temp_generated_$t
dot -Tpng temp_generated_$t.gv -o ${run_file}_data.png > ${run_file}_calls.png

