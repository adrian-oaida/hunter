#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../tools/tools.h"

int n, *a;
int *shadow_a;

int partition(int lo, int hi){
    int shadow_pivot = 0;
    int basic_block_id;


    basic_block_id = enter_block(6, 1, "int pivot = a[hi]");
    int pivot = a[hi];

    shadow_pivot = basic_block_id;
    data_flow_trace(shadow_a[hi], basic_block_id, 1);
    exit_block(1);

    int i = lo - 1;
    int tmp;
    int shadow_tmp;
    enter_block(7, 1, "for(int j = lo; j < hi; j++)");
    for(int j = lo; j < hi; j++){
        enter_block(8, 1, "if(a[j] <= pivot)");
        if(a[j] <= pivot){
            i++;
            enter_block(9, 1, "if(i != j)");
            if(i != j){

                basic_block_id = enter_block(10, 1, "tmp = a[i]");
                tmp = a[i];
                data_flow_trace(shadow_a[i], basic_block_id, 1);
                shadow_tmp = basic_block_id;
                exit_block(1);
                basic_block_id = enter_block(11, 1, "a[i] = a[j]");
                a[i] = a[j];

                data_flow_trace(shadow_a[j], basic_block_id, 1);
                shadow_a[i] = basic_block_id;
                exit_block(1);

                basic_block_id = enter_block(12, 1, "a[j] = tmp");
                a[j] = tmp;

                shadow_a[i] = basic_block_id;
                data_flow_trace(shadow_tmp, basic_block_id, 1);
                exit_block(1);

            }
            exit_block(1);
        }
        exit_block(1);
    }
    exit_block(1);

    basic_block_id = enter_block(13, 1, "tmp = a[i + 1]");

    tmp = a[i + 1];

    data_flow_trace(shadow_a[i + 1], basic_block_id, 1);
    shadow_tmp = basic_block_id;
    exit_block(1);

    basic_block_id = enter_block(14, 1, "a[i + 1] = a[hi]");
    a[i + 1] = a[hi];

    data_flow_trace(shadow_a[hi], basic_block_id, 1);
    shadow_a[i + 1] = basic_block_id;
    exit_block(1);

    basic_block_id = enter_block(15, 1, "a[hi] = tmp");
    a[hi] = tmp;

    data_flow_trace(shadow_tmp, basic_block_id, 1);
    shadow_a[hi] = basic_block_id;
    exit_block(1);

    return i + 1;
}
void quick_sort(int lo, int hi){
    enter_block(2, 1, "if(lo < hi)");
    if(lo < hi){
        enter_block(3, 1, "int p = partition(lo, hi)");
        int p = partition(lo, hi);
        exit_block(1);

        enter_block(4, 1, "quick_sort(lo, p - 1)");
        quick_sort(lo, p - 1);
        exit_block(1);

        enter_block(5, 1, "quick_sort(p + 1, hi)");
        quick_sort(p + 1, hi);
        exit_block(1);
    }
    exit_block(1);
}


int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");
    fscanf(f, "%d", &n);

    a = (int*) malloc(n * sizeof(int));
    shadow_a = get_trace_array(n);

    for(int i = 0; i < n; i++){
        fscanf(f, "%d", &a[i]);
    }

    fclose(f);
    trace_init();
    enter_block(1,1, "quick_sort(0, n - 1)");

    quick_sort(0, n - 1);

    exit_block(1);
    trace_end();

    f = fopen(argv[2], "w");
    fprintf(f, "%d\n", n);
    for(int i = 0; i < n; i++){
        fprintf(f, "%d ", a[i]);
    }
    fclose(f);
    return 0;
}

