#include<stdlib.h>
#include<stdio.h>
#include "../tools/trace_helper.h"
#include "../tools/matrix_op.h"

//matrix dimensions ; n x m * m x p = n x p
int n,m,p;

int **m_a, **m_b, **m_r;

int **shadow_m_a, **shadow_m_b, **shadow_m_r;

int main(int argc, char *argv[]){
    FILE *f;

    f = fopen("matrix_multiplication_input", "r");

    fscanf(f, "%d %d %d", &n, &m, &p);

    m_a = alloc_matrix(n, m);
    m_b = alloc_matrix(m, p);

    m_r = alloc_matrix(n, p);
    init_int_matrix(m_r, n, p);

    shadow_m_a = alloc_matrix(n, m);
    init_int_matrix(shadow_m_a, n, m);

    shadow_m_b = alloc_matrix(m, p);
    init_int_matrix(shadow_m_b, m, p);

    shadow_m_r = alloc_matrix(n, p);
    init_int_matrix(shadow_m_r, n, p);

    trace_init();
    int basic_block_id;


    //enter_block(2, 1);
    for(int i = 0; i < n; i++){
        //enter_block(3, 1);
        for(int j = 0; j < m; j++){
            //basic_block_id = enter_block(4, 1);

            fscanf(f, "%d", &m_a[i][j]);

            //data_flow_trace(shadow_m_a[i][j], basic_block_id);
            //shadow_m_a[i][j] = basic_block_id;
            //exit_block(1);
        }
        //exit_block(1);
    }
    //exit_block(1);

    //enter_block(5, 1);
    for(int j = 0; j < m; j++){
        //enter_block(6, 1);
        for(int k = 0; k < p; k++){
            //basic_block_id = enter_block(7, 1);

            fscanf(f, "%d", &m_b[j][k]);

            //data_flow_trace(shadow_m_b[j][k], basic_block_id);
            //shadow_m_b[j][k] = basic_block_id;
            //exit_block(1);
        }
        //exit_block(1);
    }
    //exit_block(1);
    fclose(f);

    enter_block(1, 1);
    for(int i = 0; i < n; i++){
        enter_block(2, 1);
        for(int k = 0; k < p; k++){
            enter_block(3, 1);
            for(int j = 0; j < m; j++){
                basic_block_id = enter_block(4, 1);

                m_r[i][k] = m_r[i][k] + ( m_a[i][j] * m_b[j][k] );

                data_flow_trace(shadow_m_r[i][k], basic_block_id);

                data_flow_trace(shadow_m_a[i][j], basic_block_id);

                data_flow_trace(shadow_m_b[j][k], basic_block_id);

                shadow_m_r[i][k] = basic_block_id;
                exit_block(1);
            }
            exit_block(1);
        }
        exit_block(1);
    }
    exit_block(1);

    trace_end();

    free(m_a);
    free(m_b);

    f = fopen("matrix_multiplication_result", "w");

    fprintf(f, "%d %d\r\n", n, p);

    //enter_block(12, 1);
    for(int i = 0; i < n; i++){
        //enter_block(13, 1);
        for(int k = 0; k < p; k++){
            //basic_block_id = enter_block(14, 1);
            fprintf(f, "%d ", m_r[i][k]);
            //data_flow_trace(shadow_m_r[i][k], basic_block_id);

           // exit_block(1);
        }
        fprintf(f, "\r\n");
        //exit_block(1);
    }
    //exit_block(1);


    fclose(f);
    free(m_r);
}