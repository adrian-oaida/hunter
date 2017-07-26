/*
 * developer: Adrian Oaida
 * this code is based on code developed by Murray Cole
 */

#include<stdio.h>
#include<math.h>
#include<stdlib.h>
#include<string.h>
#include<pthread.h>
#include "../tools/tools.h"

#define MAX_DATA 10000
#define MAX_WORKERS 50


void *worker(void *arg1);


int data_size, num_workers;

int *data;
int *shadow_data;

int main(int argc, char *argv[]){

    pthread_t *worker_ids;
    pthread_attr_t attr;

    int i;

    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);


    data_size = atoi(argv[1]);
    num_workers = atoi(argv[2]);

    worker_ids = (pthread_t *) malloc(num_workers * sizeof(pthread_t));

    init_barrier(num_workers);

    data = create_and_init_int_array(data_size);
    shadow_data = get_trace_array(data_size);

    trace_init();
    enter_block(1, num_workers + 1, "for(i = 0; i < num_workers; i++)");
    //creating the workers and putting them to work
    for(i = 0; i < num_workers; i++){

        pthread_create(&worker_ids[i], &attr, worker, (void *) i);
    }

    //joining the threads to get the computation result
    for(i = 0; i < num_workers; i++){
        pthread_join(worker_ids[i], NULL);

    }
    exit_block(num_workers + 1);
    trace_end();
    //writing the computation result to a file

    FILE *results = fopen("results", "w");
    fprintf(results, "number of workers: %d\n", num_workers);
    for(i = 0;i < data_size; i++){
        fprintf(results, "%d\n", data[i]);
    }
    fprintf(results, "\n");

    return 0;
}


void *worker(void *arg){
    int worker_id = (int)arg;
    int worker_state = worker_id;

    int shadow_worker_state = 0;

    for(int i = 0; i < worker_id;i++){
        //wait for other workers to catch to star the stage
        wait_for_barrier();
    }
    int basic_block_id = 0;

    basic_block_id = enter_block(2, worker_id,"for(int i = 0; i < data_size; i++)");

    for(int i = 0; i < data_size; i++){
        basic_block_id = enter_block(3, worker_id, "worker_state = worker_state + data[i]");

        worker_state = worker_state + data[i];

        data_flow_trace(shadow_data[i], basic_block_id, worker_id);
        data_flow_trace(shadow_worker_state, basic_block_id, worker_id);
        shadow_worker_state = basic_block_id;
        exit_block(worker_id);

        basic_block_id = enter_block(4, worker_id, "other_pipe = data[i]");
        int other_pipe;
        int shadow_other_pipe = 0;

        other_pipe = data[i];

        data_flow_trace(shadow_data[i], basic_block_id, worker_id);
        shadow_other_pipe = basic_block_id;
        exit_block(worker_id);

        //iterate over something while we are using data[i]
        //and make a decision with previous state
        basic_block_id = enter_block(5, worker_id, "tmp = data[i]");
        int shadow_tmp = 0;
        int tmp;

        tmp = worker_state;

        data_flow_trace(shadow_worker_state, basic_block_id, worker_id);
        shadow_tmp = basic_block_id;
        exit_block(worker_id);

        for(int j = 0; j < 6; j++){
            if(j % 2 == 0){
                basic_block_id = enter_block(5, worker_id, "tmp = tmp * 5");

                tmp = tmp * 5;

                data_flow_trace(shadow_tmp, basic_block_id, worker_id);
                shadow_tmp = basic_block_id;

                exit_block(worker_id);
            }else{
                basic_block_id = enter_block(6, worker_id, "tmp = tmp * 5");

                tmp = tmp * other_pipe;

                data_flow_trace(shadow_tmp, basic_block_id, worker_id);
                data_flow_trace(shadow_other_pipe, basic_block_id, worker_id);
                shadow_tmp = basic_block_id;

                exit_block(worker_id);
            }

        }
        basic_block_id = enter_block(7, worker_id, "other_pipe *= 3");
        other_pipe *= 3;

        data_flow_trace(shadow_other_pipe, basic_block_id, worker_id);
        shadow_other_pipe = basic_block_id;
        exit_block(worker_id);

        basic_block_id = enter_block(8, worker_id, "data[i] = tmp");

        data[i] = tmp + other_pipe;

        data_flow_trace(shadow_other_pipe, basic_block_id, worker_id);
        data_flow_trace(shadow_tmp, basic_block_id, worker_id);
        shadow_data[i] = basic_block_id;
        exit_block(worker_id);

        //wait for other workers to catch up
        wait_for_barrier();
    }
    exit_block(worker_id);

    for(int i = 0; i < (num_workers - worker_id -1); i++){
        //wait for other workers to catch up to end the stage
        wait_for_barrier();
    }

}


