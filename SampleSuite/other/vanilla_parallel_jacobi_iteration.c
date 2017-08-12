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



    stripSize = gridSize/numWorkers;
    InitializeGrids();



    /* create the workers, then wait for them to finish */
    for (i = 0; i < numWorkers; i++)
        pthread_create(&workerid[i], &attr, worker, (void *) i);
    for (i = 0; i < numWorkers; i++)
        pthread_join(workerid[i], NULL);


    /* print the results */
    for (i = 0; i < numWorkers; i++){
        if (maxdiff < maxDiff[i]){
            maxdiff = maxDiff[i];

        }
    }



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
    /* determine first and last rows of my strip of the grids */
    first = worker_id*stripSize + 1;
    last = first + stripSize - 1;

    for (iters = 1; iters <= numIters; iters++) {
        /* update my points */
        for (i = first; i <= last; i++) {
            for (j = 1; j <= gridSize; j++) {
                grid2[i][j] = (grid1[i-1][j] + grid1[i+1][j] +
                               grid1[i][j-1] + grid1[i][j+1]) * 0.25;
            }
        }
        wait_for_barrier();
        /* update my points again */
        for (i = first; i <= last; i++) {
            for (j = 1; j <= gridSize; j++) {
                grid1[i][j] = (grid2[i-1][j] + grid2[i+1][j] +
                               grid2[i][j-1] + grid2[i][j+1]) * 0.25;
            }
        }
        wait_for_barrier();
    }
    /* compute the maximum difference in my strip and set global variable */

    maxdiff = 0.0;
    for (i = first; i <= last; i++) {
        for (j = 1; j <= gridSize; j++) {
            temp = grid1[i][j]-grid2[i][j];
            if (temp < 0){
                temp = -temp;
            }
            if (maxdiff < temp){
                maxdiff = temp;
            }
        }
    }
    maxDiff[worker_id] = maxdiff;
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

