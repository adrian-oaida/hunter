#include<stdio.h>
#include<math.h>
#include<stdlib.h>
#include<string.h>
#include<pthread.h>

#define MAX_DATA 10000
#define MAX_WORKERS 50

//#define trace(string, variable) pthread_mutex_lock(&print_lock); fprintf(traceFile,string, variable); pthread_mutex_unlock(&print_lock);

#define init_int_data(array,size) bzero(array, size * sizeof(int))

void *worker(void *);
void wait_for_barrier();

pthread_mutex_t barrier;
pthread_mutex_t print_lock;

pthread_cond_t go;


int num_arrived = 0;
int data_size, num_workers;

int data[MAX_DATA];

int main(int argc, char *argv[]){

    pthread_t worker_ids[MAX_WORKERS];
    pthread_attr_t attr;

    int i;

    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);

    pthread_mutex_init(&barrier, NULL);
    pthread_cond_init(&go, NULL);

    pthread_mutex_init(&print_lock, NULL);

    data_size = atoi(argv[1]);
    num_workers = atoi(argv[2]);

    init_int_data(data, MAX_DATA);

    for(i = 0; i < num_workers; i++){
        pthread_create(&worker_ids[i], &attr, worker, (void *) i);
    }
    for(i = 0; i < num_workers; i++){
        pthread_join(worker_ids[i], NULL);
    }

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

    for(int i = 0; i < worker_id;i++){
        wait_for_barrier();
    }
    for(int i = 0; i < data_size; i++){
        if(i != 0)
            data[i] = data[i-1] + 3;
        else
            data[i]++;
        //wait for other workers to catch up
        wait_for_barrier();
    }

    for(int i = 0; i < (num_workers - worker_id -1); i++){
        //wait for other workers to catch up to end the stage
        wait_for_barrier();
    }
}

void wait_for_barrier(){
    pthread_mutex_lock(&barrier);
    num_arrived++;

    if(num_arrived == num_workers){

        num_arrived = 0;
        pthread_cond_broadcast(&go);

    }else{
        pthread_cond_wait(&go, &barrier);
    }
    pthread_mutex_unlock(&barrier);
}
