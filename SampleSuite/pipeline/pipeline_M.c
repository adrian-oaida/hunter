/*
 * developer: Adrian Oaida
 *
 */

#include<stdio.h>
#include<math.h>
#include<stdlib.h>
#include<string.h>
#include<pthread.h>
#include "../tools/tools.h"


void *worker(void *arg);

int data_size, num_workers;

int *data;
int *shadow_data;


int main(int argc, char *argv[]){
    pthread_t *worker_ids;
    pthread_attr_t attr;
    trace_init();
    int i;

    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);


    data_size = atoi(argv[1]);
    num_workers = atoi(argv[2]);

    num_workers = atoi(argv[2]);

    init_barrier(num_workers);

    data = (int*)malloc(data_size * sizeof(int));

    worker_ids = (pthread_t *)malloc(num_workers * sizeof(pthread_t));


    shadow_data = get_trace_array(data_size);

    int basic_block_id;

    basic_block_id = enter_block(1, num_workers, "for(i = 0; i < num_workers; i++)");
    for(i = 0; i < num_workers; i++){

        pthread_create(&worker_ids[i], &attr, worker, (void *) i);
    }

    //joining the threads to get the computation result
    for(i = 0; i < num_workers; i++){
        pthread_join(worker_ids[i], NULL);
    }

    exit_block(num_workers);

    FILE *results = fopen("results", "w");
    for(i = 0;i < data_size; i++){
        fprintf(results, "%d\n", data[i]);
    }
    fprintf(results, "\n");

    trace_end();
    return 0;
}

void *worker(void *arg){
    int worker_id = (int)arg;
    int stage1_state = worker_id;
    int stage3_state = worker_id;
    int shadow_stage1_state = 0;
    int shadow_stage3_state = 0;
    int tmp;
    int shadow_tmp = 0;
    int basic_block_id;

    for(int i = 0; i < worker_id;i++){
        //wait for other workers to catch to star the stage
        wait_for_barrier();
    }
    basic_block_id = enter_block(2, worker_id, "for(int i = 0 ; i < data_size; i++)");
    for(int i = 0 ; i < data_size; i++){
        basic_block_id = enter_block(3, worker_id, "for(int i = 0 ; i < data_size; i++)");

        basic_block_id = enter_block(4, worker_id, "switch(worker_id % 3)");
        switch(worker_id % 3){
            case 0:
                basic_block_id = enter_block(5, worker_id, "stage1_state += data[i]");

                    stage1_state += data[i];

                data_flow_trace(shadow_stage1_state, basic_block_id, worker_id);
                data_flow_trace(shadow_data[i], basic_block_id, worker_id);
                shadow_stage1_state = basic_block_id;

                exit_block(worker_id);

                basic_block_id = enter_block(6, worker_id, "data[i] = data[i] + 23");

                data[i] = data[i] + 23;

                data_flow_trace(shadow_data[i], basic_block_id, worker_id);
                shadow_data[i] = basic_block_id;
                exit_block(worker_id);

                break;
            case 1:
                basic_block_id = enter_block(7, worker_id, "tmp = data[i]");

                    tmp = data[i - 2];

                data_flow_trace(shadow_data[i - 2], basic_block_id, worker_id);
                shadow_tmp = basic_block_id;
                exit_block(worker_id);

                basic_block_id = enter_block(8, worker_id, "while(tmp != 0)");
                while(tmp != 0){
                    basic_block_id = enter_block(9, worker_id, "while(tmp != 0)");
                        basic_block_id = enter_block(10, worker_id, "tmp = tmp / 3");

                            tmp = tmp / 3;

                        data_flow_trace(shadow_tmp, basic_block_id, worker_id);
                        shadow_tmp = basic_block_id;
                        exit_block(worker_id);
                    exit_block(worker_id);
                }
                exit_block(worker_id);

                basic_block_id = enter_block(11, worker_id, "data[i] = tmp");

                    data[i] = tmp;

                data_flow_trace(shadow_tmp, basic_block_id, worker_id);
                shadow_data[i] = basic_block_id;
                exit_block(worker_id);
                break;
            case 2:
                basic_block_id = enter_block(12, worker_id, "stage2_state+= data[i]");

                    stage3_state+= data[i];

                data_flow_trace(shadow_stage3_state, basic_block_id, worker_id);
                data_flow_trace(shadow_data[i], basic_block_id, worker_id);
                shadow_stage3_state = basic_block_id;

                exit_block(worker_id);
                if(stage3_state % 3 == 0){
                    basic_block_id = enter_block(13, worker_id, "if(stage3_state % 3 == 0)");
                        basic_block_id = enter_block(14, worker_id, "data[i] = stage3_state + data[i]");

                            data[i] = stage3_state + data[i];

                        data_flow_trace(shadow_stage3_state, basic_block_id, worker_id);
                        data_flow_trace(shadow_data[i], basic_block_id, worker_id);
                        shadow_data[i] = basic_block_id;
                        exit_block(worker_id);
                    exit_block(worker_id);
                }else{
                    basic_block_id = enter_block(15, worker_id, "if(stage3_state % 3 == 0) else");
                        basic_block_id = enter_block(16, worker_id, "data[i]++");

                            data[i]++;

                        data_flow_trace(shadow_data[i], basic_block_id, worker_id);
                        shadow_data[i] = basic_block_id;
                        exit_block(worker_id);
                    exit_block(worker_id);

                }

                break;
        }
        exit_block(worker_id);
        wait_for_barrier();
        exit_block(worker_id);

    }
    exit_block(worker_id);
    for(int i = 0; i < (num_workers - worker_id -1); i++){
        //wait for other workers to catch up to end the stage
        wait_for_barrier();
    }

}

