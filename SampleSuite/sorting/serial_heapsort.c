#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include "../tools/tools.h"

int n, *array;
int *shadow_array;

void sift_down(int *array, int start, int end);

int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");
    fscanf(f, "%d", &n);

    array = (int*) malloc(n * sizeof(int));
    shadow_array = get_trace_array(n);


    for(int i = 0; i < n; i++){
        fscanf(f, "%d", &array[i]);
    }
    fclose(f);

    trace_init();
    enter_block(1, 1, "begin_of_race");

    enter_block(2, 1, "for(int start = (n/2); start >= 0; start--)");
    for(int start = (n/2); start >= 0; start--){
        sift_down(array, start, n - 1);
    }
    exit_block(1);

    int end = n - 1;
    int tmp;


    int basic_block_id;
    int shadow_tmp = 0;
    enter_block(3, 1,"while(end > 0)");
    while(end > 0){
        enter_block(8, 1, "tmp = array[end]");

        tmp = array[end];

        data_flow_trace(shadow_array[end], basic_block_id, 1);
        shadow_tmp = basic_block_id;
        exit_block(1);

        basic_block_id = enter_block(9, 1, "array[end] = array[0]");

        array[end] = array[0];

        data_flow_trace(shadow_array[0], basic_block_id, 1);
        shadow_array[end] = basic_block_id;
        exit_block(1);

        basic_block_id = enter_block(10, 1, "array[0] = tmp");

        array[0] = tmp;

        data_flow_trace(shadow_tmp, basic_block_id, 1);
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


    int basic_block_id;
    int shadow_tmp = 0;
    enter_block(4, 1, "while((left_child = root * 2) <= end)");
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
            basic_block_id = enter_block(5, 1, "tmp = array[root]");

            tmp = array[root];

            data_flow_trace(shadow_array[root], basic_block_id, 1);
            shadow_tmp = basic_block_id;
            exit_block(1);

            basic_block_id = enter_block(6, 1, "array[root] = array[swap]");

            array[root] = array[swap];

            data_flow_trace(shadow_array[swap], basic_block_id, 1);
            shadow_array[root] = basic_block_id;
            exit_block(1);

            basic_block_id = enter_block(7, 1, "array[swap] = tmp");

            array[swap] = tmp;

            data_flow_trace(shadow_tmp, basic_block_id, 1);
            shadow_array[swap] = basic_block_id;
            exit_block(1);

            root = swap;
        }
    }
    exit_block(1);
}
