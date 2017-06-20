#include<stdio.h>
#include<stdlib.h>
#include<pthread.h>
#include "../tools/tools.h"

//in this example we are going to read from a file and the do a parallel matrix addtion, each row is given to one worker
//then the workers are joined and the result is writen to a file
//matrix a, matrix b, matrix result ; n x m matrix
int **m_a, **m_b, **m_r;
int **shadow_m_a, **shadow_m_b, **shadow_m_r;
int n, m;
void *worker(void *arg);

int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");

    fscanf(f, "%d %d\n", &n, &m);

    m_a = alloc_matrix(n, m);
    m_b = alloc_matrix(n, m);
    m_r = alloc_matrix(n, m);

    shadow_m_a = alloc_matrix(n, m);
    init_int_matrix(shadow_m_a, n, m);

    shadow_m_b = alloc_matrix(n, m);
    init_int_matrix(shadow_m_b, n, m);

    shadow_m_r = alloc_matrix(n, m);
    init_int_matrix(shadow_m_r, n, m);

    pthread_t *worker_ids = (pthread_t *) malloc(n * sizeof(pthread_t));
    pthread_attr_t attr;

    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);

    trace_init();

    int basic_block_id;

    enter_block(1, 0);

    enter_block(2, 0);
    for(int i = 0; i < n; i++){
        enter_block(3, 0);
        for(int j = 0; j < m; j++){
            basic_block_id = enter_block(4, 0);

            data_flow_trace(shadow_m_a[i][j], basic_block_id);

            fscanf(f, "%d", &m_a[i][j]);

            shadow_m_a[i][j] = basic_block_id;
            exit_block(0);

        }
        exit_block(0);
    }
    exit_block(0);

    enter_block(2, 0);
    for(int i = 0; i < n; i++){
        enter_block(3, 0);
        for(int j = 0; j < m; j++){
            basic_block_id = enter_block(4, 0);

            data_flow_trace(shadow_m_b[i][j], basic_block_id);

            fscanf(f, "%d", &m_b[i][j]);

            shadow_m_b[i][j] = basic_block_id;
            exit_block(0);
        }
        exit_block(0);
    }
    exit_block(0);

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
            enter_block(8, 1);

            basic_block_id = enter_block(9, 1);

            data_flow_trace(shadow_m_r[i][j], basic_block_id);
            fprintf(f, "%d ", m_r[i][j]);

            shadow_m_r[i][j] = basic_block_id;

            exit_block(1);
            exit_block(1);
        }
        fprintf(f, "\r\n");
    }
    fclose(f);


    exit_block(0);

    free(m_r);

    trace_end();
    return 0;
}
void *worker(void *arg){
    int worker_id = (int)arg;

    int basic_block_id;


    for(int j = 0; j < m; j++){
        enter_block(6, worker_id + 1);
        basic_block_id = enter_block(7, worker_id + 1);

        data_flow_trace(shadow_m_a[worker_id][j], basic_block_id);
        data_flow_trace(shadow_m_b[worker_id][j], basic_block_id);

        m_r[worker_id][j] = m_a[worker_id][j] + m_b[worker_id][j];

        shadow_m_r[worker_id][j] = basic_block_id;


        exit_block(worker_id + 1);
        exit_block(worker_id + 1);
    }

}