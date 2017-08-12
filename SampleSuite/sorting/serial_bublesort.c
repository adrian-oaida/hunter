#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include "../tools/tools.h"

int n;
int *array;
int *shadow_array;

int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");
    fscanf(f, "%d", &n);

    array = (int*) malloc(n * sizeof(int));
    shadow_array = get_trace_array(n);

    for(int i = 0; i < n; i++){
        fscanf(f, "%d", &array[i]);
    }
    fclose(f);

    int basic_block_id;
    trace_init();

    int swap;
    int shadow_swap = 0;
    enter_block(1, 1, "for(int i = 0; i < n; i++)");
    for(int i = 0; i < n; i++){
        enter_block(2, 1, "for(int j = 0; j < n - 1; j++)");
        for(int j = 0; j < n - 1; j++){
            enter_block(3, 1, "if(array[j] > array[j + 1])");

            if(array[j] > array[j + 1]){
                basic_block_id = enter_block(4, 1, "swap = array[j + 1]");

                swap = array[j + 1];
                data_flow_trace(shadow_array[j + 1], basic_block_id, 1);
                shadow_swap = basic_block_id;

                exit_block(1);
                basic_block_id = enter_block(5, 1, "array[j + 1] = array[j]");

                array[j + 1] = array[j];

                data_flow_trace(shadow_array[j], basic_block_id, 1);
                shadow_array[j + 1] = basic_block_id;

                exit_block(1);
                basic_block_id = enter_block(6, 1, "array[j] = swap");

                array[j] = swap;

                data_flow_trace(shadow_swap, basic_block_id, 1);
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