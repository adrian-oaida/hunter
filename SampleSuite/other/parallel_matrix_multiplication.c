#include<stdlib.h>
#include<stdio.h>
#include<pthread.h>
#include "../tools/trace_helper.h"
#include "../tools/matrix_op.h"

//matrix dimensions ; n x m * m x p = n x p
int n,m,p;

int **m_a, **m_b, **m_r;

int **shadow_m_a, **shadow_m_b, **shadow_m_r;

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

    shadow_m_a = alloc_and_init_int_matrix(n, m);
    shadow_m_b = alloc_and_init_int_matrix(m, p);
    shadow_m_r = alloc_and_init_int_matrix(n, p);

    worker_ids = (pthread_t *) malloc(n * sizeof(pthread_t));

    trace_init();

    int basic_block_id;

    for(int i = 0; i < n; i++){
        basic_block_id = enter_block(1, n, "for(int i = 0; i < n; i++)");

        for(int j = 0; j < m; j++){
            basic_block_id = enter_block(2, n, "for(int j = 0; j < m; j++)");
                basic_block_id = enter_block(3, n, "fscanf(f, \\\"%d\\\", &m_a[i][j])");

                    fscanf(f, "%d", &m_a[i][j]);

                shadow_m_a[i][j] = basic_block_id;
                exit_block(n);
            exit_block(n);
        }
        exit_block(n);
    }

    for(int j = 0; j < m; j++){
        basic_block_id = enter_block(4, n, "for(int j = 0; j < m; j++)");
        for(int k = 0; k < p; k++){
            basic_block_id = enter_block(5, n, "for(int k = 0; k < p; k++)");
                basic_block_id = enter_block(6, n, "fscanf(f, \\\"%d\\\", &m_b[j][k])");

                    fscanf(f, "%d", &m_b[j][k]);

                shadow_m_b[j][k] = basic_block_id;
                exit_block(n);
            exit_block(n);
        }
        exit_block(n);
    }
    fclose(f);


    for(int i = 0; i < n; i++){
        enter_block(1, n, "for(int i = 0; i < n; i++)");
        pthread_create(&worker_ids[i], &attr, worker, (void *) i);
    }
    for(int i = 0; i < n; i++){
        pthread_join(worker_ids[i], NULL);
        exit_block(n);
    }



    free(m_a);
    free(m_b);

    f = fopen(argv[2], "w");

    fprintf(f, "%d %d\r\n", n, p);

    for(int i = 0; i < n; i++){
        basic_block_id = enter_block(7, n, "for(int i = 0; i < n; i++)");
        for(int k = 0; k < p; k++){
            basic_block_id = enter_block(8, n, "for(int k = 0; k < p; k++)");
                basic_block_id = enter_block(9, n, "fprintf(f, \\\"%d \\\", m_r[i][k])");

                    fprintf(f, "%d ", m_r[i][k]);

                data_flow_trace(shadow_m_r[i][k], basic_block_id, n);
                exit_block(n);
            exit_block(n);

        }
        fprintf(f, "\r\n");
        exit_block(n);
    }
    trace_end();
    fclose(f);
    free(m_r);
}

void *worker(void *arg){
    int worker_id = (int)arg;
    int basic_block_id;

    for(int k = 0; k < p; k++){
        enter_block(10, worker_id, "for(int k = 0; k < p; k++)");
        for(int j = 0; j < m; j++){
            enter_block(11, worker_id, "for(int j = 0; j < m; j++)");

                basic_block_id = enter_block(12, worker_id, "m_r[worker_id][k] = m_r[worker_id][k] + ( m_a[worker_id][j] * m_b[j][k] )");

                    m_r[worker_id][k] = m_r[worker_id][k] + ( m_a[worker_id][j] * m_b[j][k] );

                data_flow_trace(shadow_m_r[worker_id][k], basic_block_id, worker_id);
                data_flow_trace(shadow_m_a[worker_id][j], basic_block_id, worker_id);
                data_flow_trace(shadow_m_b[j][k], basic_block_id, worker_id);
                shadow_m_r[worker_id][k] = basic_block_id;
                exit_block(worker_id);
            exit_block(worker_id);
        }
        exit_block(worker_id);
    }

}