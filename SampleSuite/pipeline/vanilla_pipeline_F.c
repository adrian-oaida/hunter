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

void *worker(void *arg1);


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

    worker_ids = (pthread_t *) malloc(num_workers * sizeof(pthread_t));

    init_barrier(num_workers);

    data = create_and_init_int_array(data_size);

    //creating the workers and putting them to work
    for(i = 0; i < num_workers; i++){

        pthread_create(&worker_ids[i], &attr, worker, (void *) i);
    }

    //joining the threads to get the computation result
    for(i = 0; i < num_workers; i++){
        pthread_join(worker_ids[i], NULL);

    }
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


    for(int i = 0; i < worker_id;i++){
        //wait for other workers to catch to star the stage
        wait_for_barrier();
    }


    for(int i = 0; i < data_size; i++){

        worker_state = worker_state + data[i];


        int other_pipe;

        other_pipe = data[i];


        //iterate over something while we are using data[i]
        //and make a decision with previous state
        int tmp;

        tmp = worker_state;


        for(int j = 0; j < 6; j++){
            if(j % 2 == 0){

                tmp = tmp * 5;


            }else{

                tmp = tmp * other_pipe;


            }

        }
        other_pipe *= 3;



        data[i] = tmp + other_pipe;


        //wait for other workers to catch up
        wait_for_barrier();
    }

    for(int i = 0; i < (num_workers - worker_id -1); i++){
        //wait for other workers to catch up to end the stage
        wait_for_barrier();
    }

}


