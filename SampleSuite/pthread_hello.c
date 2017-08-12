#include<pthread.h>
#include<stdio.h>

#define NUM_THREADS 10

struct thread_data{
    long thread_id;
    double sum;
    char *message;
};
struct thread_data thread_data_array[NUM_THREADS];

void *print_hello(void *thread_arg){
    struct thread_data *t = (struct thread_data*)thread_arg;

    int i;
    long tid;
    double result=0.0;

    printf("Thread %ld starting...\n",t->thread_id);
    for (i=0; i<1000000; i++)
    {
        result = result + sin(i) * tan(i);
    }
    t->sum = result;

    printf("Thread %ld done. Result = %e\n",t->thread_id, t->sum);
    pthread_exit((void*) t->thread_id);
}

int main(int argc, char *argv[]){
    pthread_t threads[NUM_THREADS];
    pthread_attr_t attr;
    int rc;
    long t;
    void *status;
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);

    for(t=0; t< NUM_THREADS; t++){
        printf("In main creating thread %ld\r\n", t);
        thread_data_array[t].thread_id = t;
        thread_data_array[t].sum = 4*t;
        if(t%3)
            thread_data_array[t].message = "Wanker";
        else
            thread_data_array[t].message = "Twat";

        rc = pthread_create(&threads[t], &attr, print_hello, (void*)&thread_data_array[t]);

        if(rc){
            printf("Error: return code from pthread_create is %d\r\n", rc);
            exit(-1);
        }
    }
    pthread_attr_destroy(&attr);
    for(t = 0; t < NUM_THREADS; t++){
        rc = pthread_join(threads[t], &status);
        if(rc){
            printf("Error: return code from pthread_join is %d\r\n", rc);
            exit(-1);
        }
        printf("Main: completed join with thread %ld having status %ld\r\n", t, (long) status);

    }
    return 0;
}