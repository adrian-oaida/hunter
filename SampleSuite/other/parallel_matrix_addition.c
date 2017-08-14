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

    m_a = alloc_int_matrix(n, m);
    m_b = alloc_int_matrix(n, m);
    m_r = alloc_int_matrix(n, m);

    shadow_m_a = alloc_and_init_int_matrix(n, m);

    shadow_m_b = alloc_and_init_int_matrix(n, m);

    shadow_m_r = alloc_and_init_int_matrix(n, m);;

    pthread_t *worker_ids = (pthread_t *) malloc(n * sizeof(pthread_t));
    pthread_attr_t attr;

    pthread_attr_init(&attr);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);

    trace_init();

    int basic_block_id;



    for(int i = 0; i < n; i++){
        basic_block_id = enter_block(1, n, "for(int i = 0; i < n; i++)");

        for(int j = 0; j < m; j++){
            enter_block(2, n, "for(int j = 0; j < m; j++)");

                basic_block_id = enter_block(3, n, "fscanf(f, \"%d\", &m_a[i][j])");

                    fscanf(f, "%d", &m_a[i][j]);

                data_flow_trace(shadow_m_a[i][j], basic_block_id, n);
                shadow_m_a[i][j] = basic_block_id;
                exit_block(n);
            exit_block(n);
        }
        exit_block(n);
    }

    for(int i = 0; i < n; i++){
        enter_block(4, n, "for(int i = 0; i < n; i++)");

        for(int j = 0; j < m; j++){
            enter_block(5, n, "for(int j = 0; j < m; j++)");
                basic_block_id = enter_block(6, n, "fscanf(f, \"%d\", &m_b[i][j])");

                    fscanf(f, "%d", &m_b[i][j]);

                data_flow_trace(shadow_m_b[i][j], basic_block_id, n);
                shadow_m_b[i][j] = basic_block_id;
                exit_block(n);
            exit_block(n);
        }
        exit_block(n);
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
        basic_block_id = enter_block(9, n, "for(int i=0; i < n; i++)");
        for(int j=0; j< m; j++){
            basic_block_id = enter_block(10, n, "for(int j=0; j< m; j++)");

                basic_block_id = enter_block(8, 0, "fprintf(f, \"%d \", m_r[i][j])");
                    fprintf(f, "%d ", m_r[i][j]);

                data_flow_trace(shadow_m_r[i][j], basic_block_id, 0);
                shadow_m_r[i][j] = basic_block_id;
                exit_block(1);
            exit_block(n);
        }
        fprintf(f, "\r\n");
        exit_block(n);
    }
    fclose(f);


    free(m_r);

    trace_end();
    return 0;
}
void *worker(void *arg){
    int worker_id = (int)arg;

    int basic_block_id;


    for(int j = 0; j < m; j++){
        basic_block_id = enter_block(7, worker_id, "for(int j = 0; j < m; j++)");
            basic_block_id = enter_block(8, worker_id, "m_r[worker_id][j] = m_a[worker_id][j] + m_b[worker_id][j]");

                m_r[worker_id][j] = m_a[worker_id][j] + m_b[worker_id][j];

            data_flow_trace(shadow_m_a[worker_id][j], basic_block_id, worker_id);
            data_flow_trace(shadow_m_b[worker_id][j], basic_block_id, worker_id);
            shadow_m_r[worker_id][j] = basic_block_id;
            exit_block(worker_id);
        exit_block(worker_id);
    }

}