#include<pthread.h>
#include<stdio.h>
#include<string.h>
#include "../tools/tools.h"

int *data;
int *shadow_data;
int main(int argc, char *argv[]){

    int n;
    int basic_block_id;
    n = atoi(argv[1]);

    trace_init();

    data = (int*) malloc(n * sizeof(int));
    shadow_data = get_trace_array(n);

    enter_block(1, 1, "for(int i = 0; i < n; i++)");
    for(int i = 0; i < n; i++){

        basic_block_id = enter_block(2, 1, "data[i] = 0");
        data[i] = 0;
        shadow_data[i] = basic_block_id;
        exit_block(1);

    }
    exit_block(1);

    enter_block(3, 1, "for(int i = 0; i < n; i++)");
    for(int i = 0; i < n; i++){

        basic_block_id = enter_block(4, 1, "data[i] = data[i] + 1");

        data[i] = data[i] + 1;

        data_flow_trace(shadow_data[i], basic_block_id, 1);
        shadow_data[i] = basic_block_id;

        exit_block(1);
    }

    exit_block(1);


    int shadow_reducer = 0;
    char reducer[10000];

    enter_block(5, 1, "for(int i = 0; i < n; i++)");
    for(int i = 0; i < n; i++){
        basic_block_id = enter_block(6, 1, "sprintf(reducer, \\\"%s %d\\\", reducer, data[i])");

        sprintf(reducer, "%s %d", reducer, data[i]);

        data_flow_trace(shadow_reducer, basic_block_id, 1);
        data_flow_trace(shadow_data[i], basic_block_id, 1);
        shadow_reducer = basic_block_id;

        exit_block(1);
    }
    exit_block(1);
    trace_end();
    return 0;
}