/* Jacobi iteration using pthreads

Adapted from
Jacobi iteration using pthreads by Greg Andrews
http://www.cs.arizona.edu/people/greg/mpdbook/programs/jacobi.c


*/

#define _REENTRANT
#include <pthread.h>
#include <stdlib.h>
#include <semaphore.h>
#include <stdio.h>
#include <sys/times.h>
#include <limits.h>
#include "../tools/tools.h"

void *worker(void *);
void InitializeGrids();


int gridSize, numWorkers, numIters, stripSize;
double *maxDiff;
double **grid1, **grid2;

int *shadow_maxDiff;
int **shadow_grid1, **shadow_grid2;

/* main() -- read command line, initialize grids, and create threads
             when the threads are done, print the results */

int main(int argc, char *argv[]) {
    /* thread ids and attributes */
    pthread_t *workerid;
    pthread_attr_t attr;
    int i, j;
    double maxdiff = 0.0;
    FILE *results;

    /* set global thread attributes */
    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);


    /* read command line and initialize grids */
    gridSize = atoi(argv[1]);
    numWorkers = atoi(argv[2]);
    numIters = atoi(argv[3]);

    workerid = (pthread_t*) malloc( numWorkers * sizeof(pthread_t));

    maxDiff = (double*) malloc(numWorkers * sizeof(double));
    grid1 = alloc_double_matrix(gridSize + 2, gridSize + 2);
    grid2 = alloc_double_matrix(gridSize + 2, gridSize + 2);

    shadow_maxDiff = get_trace_array(numWorkers);

    shadow_grid1 = get_trace_matrix(gridSize + 2, gridSize + 2);
    shadow_grid2 = get_trace_matrix(gridSize + 2, gridSize + 2);

    stripSize = gridSize/numWorkers;
    InitializeGrids();

    trace_init();


    /* create the workers, then wait for them to finish */
    for (i = 0; i < numWorkers; i++)
        pthread_create(&workerid[i], &attr, worker, (void *) i);
    for (i = 0; i < numWorkers; i++)
        pthread_join(workerid[i], NULL);


    int basic_block_id = 0;
    int shadow_maxdiff = 0;
    basic_block_id = enter_block(14, numWorkers + 1, "for (i = 0; i < numWorkers; i++)");
    /* print the results */
    for (i = 0; i < numWorkers; i++){
        if (maxdiff < maxDiff[i]){
            basic_block_id = enter_block(15, numWorkers + 1, "maxdiff = maxDiff[i]");
            maxdiff = maxDiff[i];

            data_flow_trace(shadow_maxDiff[i], basic_block_id, numWorkers + 1);
            shadow_maxdiff = basic_block_id;
            exit_block(numWorkers + 1);
        }
    }

    exit_block(numWorkers + 1);
    trace_end();


    results = fopen("results", "w");
    for (i = 1; i <= gridSize; i++) {
        for (j = 1; j <= gridSize; j++) {
            fprintf(results, "%f ", grid2[i][j]);
        }
        fprintf(results, "\n");
    }
    free(grid1);
    free(grid2);
    free(maxDiff);
}


/* Each Worker computes values in one strip of the grids.
   The main worker loop does two computations to avoid copying from
   one grid to the other.  */

void *worker(void *arg) {
    int worker_id = (int) arg;
    double maxdiff, temp;
    int i, j, iters;
    int first, last;

    int basic_block_id = 0;

    /* determine first and last rows of my strip of the grids */
    first = worker_id*stripSize + 1;
    last = first + stripSize - 1;
    basic_block_id = enter_block(1, worker_id," for (iters = 1; iters <= numIters; iters++)");

    for (iters = 1; iters <= numIters; iters++) {
        /* update my points */
        basic_block_id = enter_block(2, worker_id, " for (i = first; i <= last; i++)");
        for (i = first; i <= last; i++) {
            basic_block_id = enter_block(3, worker_id, "for (j = 1; j <= gridSize; j++)");
            for (j = 1; j <= gridSize; j++) {
                basic_block_id = enter_block(4, worker_id, "grid2[i][j] = (grid1[i-1][j] + grid1[i+1][j] + grid1[i][j-1] + grid1[i][j+1]) * 0.25");

                grid2[i][j] = (grid1[i-1][j] + grid1[i+1][j] +
                               grid1[i][j-1] + grid1[i][j+1]) * 0.25;

                data_flow_trace(shadow_grid1[i - 1][j], basic_block_id, worker_id);
                data_flow_trace(shadow_grid1[i + 1][j], basic_block_id, worker_id);
                data_flow_trace(shadow_grid1[i][j - 1], basic_block_id, worker_id);
                data_flow_trace(shadow_grid1[i][j + 1], basic_block_id, worker_id);

                shadow_grid2[i][j] = basic_block_id;

                exit_block(worker_id);
            }
        }
        exit_block(worker_id);
        wait_for_barrier();
        basic_block_id = enter_block(5, worker_id, "for (i = first; i <= last; i++)");
        /* update my points again */
        for (i = first; i <= last; i++) {
            basic_block_id = enter_block(6, worker_id, "for (j = 1; j <= gridSize; j++)");
            for (j = 1; j <= gridSize; j++) {
                basic_block_id = enter_block(7, worker_id, "grid1[i][j] = (grid2[i-1][j] + grid2[i+1][j] + grid2[i][j-1] + grid2[i][j+1]) * 0.25;");

                grid1[i][j] = (grid2[i-1][j] + grid2[i+1][j] +
                               grid2[i][j-1] + grid2[i][j+1]) * 0.25;

                data_flow_trace(shadow_grid2[i - 1][j], basic_block_id, worker_id);
                data_flow_trace(shadow_grid2[i + 1][j], basic_block_id, worker_id);
                data_flow_trace(shadow_grid2[i][j - 1], basic_block_id, worker_id);
                data_flow_trace(shadow_grid2[i][j + 1], basic_block_id, worker_id);


                shadow_grid1[i][j] = basic_block_id;

                exit_block(worker_id);
            }
        }

        exit_block(worker_id);
        wait_for_barrier();
    }
    /* compute the maximum difference in my strip and set global variable */

    int shadow_temp = 0;
    int shadow_maxdiff = 0;
    basic_block_id = enter_block(8, worker_id, "for (i = first; i <= last; i++)");
    maxdiff = 0.0;
    for (i = first; i <= last; i++) {
        basic_block_id = enter_block(9, worker_id, " for (j = 1; j <= gridSize; j++) ");
        for (j = 1; j <= gridSize; j++) {
            basic_block_id = enter_block(10, worker_id, "temp = grid1[i][j]-grid2[i][j]");

            temp = grid1[i][j]-grid2[i][j];

            data_flow_trace(shadow_grid1[i][j], basic_block_id, worker_id);
            data_flow_trace(shadow_grid2[i][j], basic_block_id, worker_id);
            shadow_temp = basic_block_id;

            exit_block(worker_id);

            if (temp < 0){
                basic_block_id = enter_block(11, worker_id, "temp = -temp");
                temp = -temp;

                data_flow_trace(shadow_temp, basic_block_id, worker_id);
                shadow_temp = basic_block_id;

                exit_block(worker_id);

            }
            if (maxdiff < temp){
                basic_block_id = enter_block(12, worker_id, "maxdiff = temp");

                maxdiff = temp;

                data_flow_trace(shadow_temp, basic_block_id, worker_id);
                shadow_maxdiff = basic_block_id;


                exit_block(worker_id);
            }


            exit_block(worker_id);
        }
        exit_block(worker_id);
    }
    exit_block(worker_id);

    basic_block_id = enter_block(13, worker_id, "maxDiff[worker_id] = maxdiff");

    maxDiff[worker_id] = maxdiff;


    data_flow_trace(shadow_maxdiff, basic_block_id, worker_id);
    shadow_maxDiff[worker_id] = basic_block_id;

    exit_block(worker_id);
    exit_block(worker_id);
}

void InitializeGrids() {
    /* initialize the grids (grid1 and grid2)
       set boundaries to 1.0 and interior points to 0.0  */
    int i, j;
    for (i = 0; i <= gridSize+1; i++)
        for (j = 0; j <= gridSize+1; j++) {
            grid1[i][j] = 0.0;
            grid2[i][j] = 0.0;
        }
    for (i = 0; i <= gridSize+1; i++) {
        grid1[i][0] = 1.0;
        grid1[i][gridSize+1] = 1.0;
        grid2[i][0] = 1.0;
        grid2[i][gridSize+1] = 1.0;
    }
    for (j = 0; j <= gridSize+1; j++) {
        grid1[0][j] = 1.0;
        grid2[0][j] = 1.0;
        grid1[gridSize+1][j] = 1.0;
        grid2[gridSize+1][j] = 1.0;
    }
}

