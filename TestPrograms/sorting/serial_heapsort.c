#include<stdio.h>
#include<stdlib.h>
#include "../tools/trace_helper.h"
#include<string.h>

int n, *array;
int *shadow_array;

void sift_down(int *array, int start, int end);

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

    trace_init();
    enter_block(1, 1);

    enter_block(2, 1);
    for(int start = (n/2); start >= 0; start--){
        sift_down(array, start, n - 1);
    }
    exit_block(1);

    int end = n - 1;
    int tmp;

    enter_block(3, 1);
    int basic_block_id;
    int shadow_tmp;
    while(end > 0){
        enter_block(8, 1);
        data_flow_trace(shadow_array[end], basic_block_id);

        tmp = array[end];

        shadow_tmp = basic_block_id;
        exit_block(1);

        basic_block_id = enter_block(9, 1);
        data_flow_trace(shadow_array[0], basic_block_id);

        array[end] = array[0];

        shadow_array[end] = basic_block_id;
        exit_block(1);

        basic_block_id = enter_block(10, 1);
        data_flow_trace(shadow_tmp, basic_block_id);

        array[0] = tmp;

        shadow_array[0] = basic_block_id;
        exit_block(1);

        end--;
        sift_down(array, 0, end);
    }
    exit_block(1);
    exit_block(1);
    trace_end();

    f = fopen(argv[2], "w");
    fprintf(f, "%d\r\n", n);
    for(int i = 0; i < n; i++){
        fprintf(f, "%d ", array[i]);
    }
    fclose(f);
}
void sift_down(int *array, int start, int end){
    int tmp;
    int root = start;

    int left_child;
    int swap;

    enter_block(4, 1);
    int basic_block_id;
    int shadow_tmp;
    while((left_child = root * 2) <= end){

        swap = root;
        if(array[swap] < array[left_child]){
            swap = left_child;
        }
        if(left_child+1 <= end && array[swap] < array[left_child + 1]){
            swap = left_child + 1;
        }
        if(swap == root){
            return;
        }else{
            basic_block_id = enter_block(5, 1);
            data_flow_trace(shadow_array[root], basic_block_id);

            tmp = array[root];

            shadow_tmp = basic_block_id;
            exit_block(1);

            basic_block_id = enter_block(6, 1);
            data_flow_trace(shadow_array[swap], basic_block_id);

            array[root] = array[swap];

            shadow_array[root] = basic_block_id;
            exit_block(1);

            basic_block_id = enter_block(7, 1);
            data_flow_trace(shadow_tmp, basic_block_id);

            array[swap] = tmp;

            shadow_array[swap] = basic_block_id;
            exit_block(1);

            root = swap;
        }
    }
    exit_block(1);
}
