/* a simple producer/consumer using semaphores and threads
   example from https://www2.cs.arizona.edu/people/greg/mpdbook/programs/pc.sems.c

   usage on Solaris:
     gcc thisfile.c -lpthread -lposix4
     a.out numIters

*/

#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <semaphore.h>
#include "../tools/trace_helper.h"

#define SHARED 1

void *Producer(void *);  /* the two threads */
void *Consumer(void *);

sem_t empty, full;    /* the global semaphores */
int data;             /* shared buffer         */
int shadow_data;
int numIters;

/* main() -- read command line and create threads, then
             print result when the threads have quit */

int main(int argc, char *argv[]) {
    /* thread ids and attributes */
    pthread_t pid, cid;
    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);

    numIters = atoi(argv[1]);
    sem_init(&empty, SHARED, 1);  /* sem empty = 1 */
    sem_init(&full, SHARED, 0);   /* sem full = 0  */

    printf("main started\n");
    trace_init();
    enter_block(1, 1);
    pthread_create(&pid, &attr, Producer, NULL);
    pthread_create(&cid, &attr, Consumer, NULL);
    pthread_join(pid, NULL);
    pthread_join(cid, NULL);
    exit_block(1);
    trace_end();
    printf("main done\n");
}

/* deposit 1, ..., numIters into the data buffer */
void *Producer(void *arg) {
    int produced;
    enter_block(2, 2);
    int basic_block_id;
    printf("Producer created\n");
    for (produced = 0; produced < numIters; produced++) {
        enter_block(3, 2);
        sem_wait(&empty);
        basic_block_id = enter_block(4, 2);
        data_flow_trace(0, basic_block_id);

        data = produced;
        shadow_data = basic_block_id;
        exit_block(2);

        sem_post(&full);

        exit_block(2);
    }
    exit_block(2);
}

/* fetch numIters items from the buffer and sum them */
void *Consumer(void *arg) {
    int total = 0, consumed;

    enter_block(5, 3);
    int basic_block_id;
    int shadow_total = 0;
    printf("Consumer created\n");
    for (consumed = 0; consumed < numIters; consumed++) {
        enter_block(6, 3);
        sem_wait(&full);

        basic_block_id = enter_block(7, 3);
        data_flow_trace(shadow_data, basic_block_id);
        data_flow_trace(shadow_total, basic_block_id);

        total = total+data;

        shadow_total = basic_block_id;

        exit_block(3);
        sem_post(&empty);
        exit_block(3);
    }
    exit_block(3);
    printf("for %d iterations, the total is %d\n", numIters, total);
}