#/bin/sh
timestamp(){
  date +"%s"
}
t=`timestamp`

rm -f pipeline_run
rm -f temp_generated_*

run_file=${1%".c"}

gcc ../tools/*.c $1 -o pipeline_run -lpthread -lm

for i in `seq 1 $2`;
do
    for j in `seq 1 $3`;
    do
        echo "doing $i $j run"
        echo "generated temp_generated_${i}_${j}_$t"
        ./pipeline_run $i $j  > temp_generated_${i}_${j}_$t
        ../tools/makedotgraph.sh temp_generated_${i}_${j}_$t
        dot -Tpng temp_generated_${i}_${j}_$t.gv -o ${run_file}_${i}_${j}_data.png > ${run_file}_${i}_${j}_calls.png
    done
done
