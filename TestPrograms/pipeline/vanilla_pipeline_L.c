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


int main(int argc, char *argv[]){
    pthread_t *worker_ids;
    pthread_attr_t attr;
    int i;

    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);


    data_size = atoi(argv[1]);
    num_workers = atoi(argv[2]);

    num_workers = atoi(argv[2]);

    init_barrier(num_workers);

    data = (int*)malloc(data_size * sizeof(int));

    worker_ids = (pthread_t *)malloc(num_workers * sizeof(pthread_t));




    for(i = 0; i < num_workers; i++){

        pthread_create(&worker_ids[i], &attr, worker, (void *) i);
    }

    //joining the threads to get the computation result
    for(i = 0; i < num_workers; i++){
        pthread_join(worker_ids[i], NULL);
    }


    FILE *results = fopen("results", "w");
    for(i = 0;i < data_size; i++){
        fprintf(results, "%d\n", data[i]);
    }
    fprintf(results, "\n");

    return 0;
}

void *worker(void *arg){
    int worker_id = (int)arg;
    int stage1_state = worker_id;
    int stage3_state = worker_id;
    int tmp;

    for(int i = 0; i < worker_id;i++){
        //wait for other workers to catch to star the stage
        wait_for_barrier();
    }
    for(int i = 0 ; i < data_size; i++){
        switch(worker_id % 3){
            case 0:
                stage1_state += data[i];
                data[i] = data[i] + 23;
                break;
            case 1:
                tmp = data[i];
                while(tmp != 0){
                    tmp = tmp / 3;
                }
                data[i] = tmp;
                break;
            case 2:
                stage3_state+= data[i];
                if(stage3_state % 3 == 0){
                    data[i] = stage3_state + data[i];

                }else{
                    data[i]++;
                }
                break;
        }
        wait_for_barrier();
    }
    for(int i = 0; i < (num_workers - worker_id -1); i++){
        //wait for other workers to catch up to end the stage
        wait_for_barrier();
    }

}

