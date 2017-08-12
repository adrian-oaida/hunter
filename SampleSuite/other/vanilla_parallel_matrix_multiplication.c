#include<stdlib.h>
#include<stdio.h>
#include<pthread.h>
#include "../tools/trace_helper.h"
#include "../tools/matrix_op.h"

//matrix dimensions ; n x m * m x p = n x p
int n,m,p;

int **m_a, **m_b, **m_r;


void *worker(void *arg);

int main(int argc, char *argv[]){

    pthread_t *worker_ids;

    pthread_attr_t attr;

    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);


    FILE *f;
    f = fopen(argv[1], "r");

    fscanf(f, "%d %d %d", &n, &m, &p);

    m_a = alloc_int_matrix(n, m);
    m_b = alloc_int_matrix(m, p);

    m_r = alloc_and_init_int_matrix(n, p);




    worker_ids = (pthread_t *) malloc(n * sizeof(pthread_t));


    for(int i = 0; i < n; i++){
        for(int j = 0; j < m; j++){
            fscanf(f, "%d", &m_a[i][j]);
        }
    }

    for(int j = 0; j < m; j++){
        for(int k = 0; k < p; k++){
            fscanf(f, "%d", &m_b[j][k]);
        }
    }
    fclose(f);


    for(int i = 0; i < n; i++){
        pthread_create(&worker_ids[i], &attr, worker, (void *) i);
    }
    for(int i = 0; i < n; i++){
        pthread_join(worker_ids[i], NULL);
    }



    free(m_a);
    free(m_b);

    f = fopen(argv[2], "w");

    fprintf(f, "%d %d\r\n", n, p);

    for(int i = 0; i < n; i++){
        for(int k = 0; k < p; k++){
            fprintf(f, "%d ", m_r[i][k]);
        }
        fprintf(f, "\r\n");
    }

    fclose(f);
    free(m_r);
}

void *worker(void *arg){
    int worker_id = (int)arg;

    for(int k = 0; k < p; k++){

        for(int j = 0; j < m; j++){

            m_r[worker_id][k] = m_r[worker_id][k] + ( m_a[worker_id][j] * m_b[j][k] );




        }

    }

}
