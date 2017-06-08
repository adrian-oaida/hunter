#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include "../tools/trace_helper.h"

int n;
int *array;
int *shadow_array;
int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");
    fscanf(f, "%d", &n);

    array = (int*) malloc(n * sizeof(int));
    shadow_array = (int*) malloc(n * sizeof(int));
    bzero(shadow_array, n * sizeof(int));

    for(int i = 0; i < n; i++){
        fscanf(f, "%d", &array[i]);
    }
    fclose(f);

    int basic_block_id;
    trace_init();
    int swap;
    int shadow_swap;
    enter_block(1, 1);
    for(int i = 0; i < n; i++){
        enter_block(2, 1);
        for(int j = 0; j < n - 1; j++){
            basic_block_id = enter_block(3, 1);
            data_flow_trace(shadow_array[j], basic_block_id);
            data_flow_trace(shadow_array[j + 1], basic_block_id);

            if(array[j] > array[j + 1]){
                basic_block_id = enter_block(4, 1);

                swap = array[j + 1];
                data_flow_trace(shadow_array[j + 1], basic_block_id);
                shadow_swap = basic_block_id;
                exit_block(1);

                basic_block_id = enter_block(5, 1);
                data_flow_trace(shadow_array[j], basic_block_id);

                array[j + 1] = array[j];

                shadow_array[j + 1] = basic_block_id;
                exit_block(1);

                basic_block_id = enter_block(6, 1);

                data_flow_trace(shadow_swap, basic_block_id);

                array[j] = swap;

                shadow_array[j] = basic_block_id;

                exit_block(1);
            }
            exit_block(1);
        }
        exit_block(1);
    }
    exit_block(1);
    trace_end();
    f = fopen(argv[2], "w");

    fprintf(f, "%d \n", n);
    for(int i = 0; i < n; i++){
        fprintf(f, "%d ", array[i]);
    }

    fclose(f);


    free(array);
    return 0;
}