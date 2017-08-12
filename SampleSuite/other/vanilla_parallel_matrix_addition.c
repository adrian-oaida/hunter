#include<stdio.h>
#include<stdlib.h>
#include<pthread.h>
#include "../tools/tools.h"

//in this example we are going to read from a file and the do a parallel matrix addtion, each row is given to one worker
//then the workers are joined and the result is writen to a file
//matrix a, matrix b, matrix result ; n x m matrix
int **m_a, **m_b, **m_r;
int n, m;
void *worker(void *arg);

int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");

    fscanf(f, "%d %d\n", &n, &m);

    m_a = alloc_int_matrix(n, m);
    m_b = alloc_int_matrix(n, m);
    m_r = alloc_int_matrix(n, m);




    pthread_t *worker_ids = (pthread_t *) malloc(n * sizeof(pthread_t));
    pthread_attr_t attr;

    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);





    for(int i = 0; i < n; i++){
        for(int j = 0; j < m; j++){


            fscanf(f, "%d", &m_a[i][j]);


        }
    }

    for(int i = 0; i < n; i++){
        for(int j = 0; j < m; j++){

            fscanf(f, "%d", &m_b[i][j]);


        }
    }

    fclose(f);

    for(int i = 0; i < n; i++){
        pthread_create(&worker_ids[i], &attr, worker, (void *) i);
    }

    for(int i=0; i < n; i++){
        pthread_join(worker_ids[i], NULL);
    }

    free(m_a);
    free(m_b);
    f = fopen(argv[2], "w");
    fprintf(f, "%d %d \r\n", n, m);

    for(int i=0; i < n; i++){
        for(int j=0; j< m; j++){


            fprintf(f, "%d ", m_r[i][j]);


        }
        fprintf(f, "\r\n");
    }
    fclose(f);


    free(m_r);

    return 0;
}
void *worker(void *arg){
    int worker_id = (int)arg;



    for(int j = 0; j < m; j++){


        m_r[worker_id][j] = m_a[worker_id][j] + m_b[worker_id][j];


    }

}
