/*
 * developer: Adrian Oaida
 * this code is based on code developed by Murray Cole
 */

#include<pthread.h>
#include "barrier.h"

int num_arrived;
int num_workers_for_barrier;
pthread_mutex_t barrier;
pthread_cond_t go;


void init_barrier(int num_workers){
    num_arrived = 0;
    num_workers_for_barrier = num_workers;
    pthread_mutex_init(&barrier, NULL);
    pthread_cond_init(&go, NULL);
}

void wait_for_barrier(){
    pthread_mutex_lock(&barrier);
    num_arrived++;

    if(num_arrived == num_workers_for_barrier){
        //all threads have arived at this stage
        num_arrived = 0;
        //release workers to go to the next stage
        pthread_cond_broadcast(&go);

    }else{
        pthread_cond_wait(&go, &barrier);
    }
    pthread_mutex_unlock(&barrier);
}