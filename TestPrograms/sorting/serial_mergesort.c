#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include "../tools/tools.h"

#define min(a,b) a<b?a:b

int n, *a, *b;

int *shadow_a, *shadow_b;

void bottom_up_merge_sort();
void bottom_up_merge(int left_index, int right_index, int end_index);
void copy_array();


int main(int argc, char *argv[]){

    FILE *f = fopen(argv[1], "r");
    fscanf(f, "%d", &n);

    a = (int*) malloc(n * sizeof(int));
    b = (int*) malloc(n * sizeof(int));
    shadow_a = get_trace_array(n);
    shadow_b = get_trace_array(n);

    for(int i = 0; i < n; i++){
        fscanf(f, "%d", &a[i]);
    }
    fclose(f);

    trace_init();
    enter_block(1, 1, "bottom_up_merge_sort()");

    bottom_up_merge_sort();

    exit_block(1);
    trace_end();

    f = fopen(argv[2], "w");
    fprintf(f, "%d\n", n);
    for(int i = 0; i < n; i++){
        fprintf(f,"%d ", a[i]);
    }
    fclose(f);

    free(shadow_a);
    free(a);
    return 0;
}

void bottom_up_merge_sort(){
    enter_block(2, 1, "for(int width = 1; width < n; width = 2 * width)");
    for(int width = 1; width < n; width = 2 * width){
        enter_block(3, 1, "for(int i = 0; i < n; i = i + 2 * width)");
        for(int i = 0; i < n; i = i + 2 * width){
            enter_block(4, 1, "bottom_up_merge(i, min(i + width, n), min(i + 2 * width, n))");
            bottom_up_merge(i, min(i + width, n), min(i + 2 * width, n));
            exit_block(1);
        }
        exit_block(1);

        enter_block(5, 1, "copy_array()");
        copy_array();
        exit_block(1);
    }
    exit_block(1);
}

void bottom_up_merge(int left_index, int right_index, int end_index){

    int basic_block_id;

    int i = left_index, j = right_index;
    enter_block(8, 1, "for(int k = left_index; k < end_index; k++)");
    for(int k = left_index; k < end_index; k++){
        enter_block(9, 1, "if(i < right_index && (j >= end_index || a[i] <= a[j]))");
        if(i < right_index && (j >= end_index || a[i] <= a[j])){
            basic_block_id = enter_block(10, 1, "b[k] = a[i]");

            b[k] = a[i];
            data_flow_trace(shadow_a[i], basic_block_id, 1);
            shadow_b[k] = basic_block_id;
            exit_block(1);

            i = i + 1;
        }else{
            basic_block_id = enter_block(11, 1, "b[k] = a[j]");
            b[k] = a[j];
            data_flow_trace(shadow_a[j], basic_block_id, 1);
            shadow_b[k] = basic_block_id;
            exit_block(1);

            j = j + 1;
        }
        exit_block(1);
    }
    exit_block(1);
}
void copy_array(){
    int basic_block_id;
    enter_block(6, 1, "for(int i = 0; i < n; i++)");
    for(int i = 0; i < n; i++){
        basic_block_id = enter_block(7, 1, "a[i] = b[i]");

        a[i] = b[i];

        data_flow_trace(shadow_b[i], basic_block_id, 1);
        shadow_a[i] = basic_block_id;
        exit_block(1);
    }
    exit_block(1);

}