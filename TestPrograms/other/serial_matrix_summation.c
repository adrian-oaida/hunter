#include<stdio.h>
#include<math.h>
#include<stdlib.h>
#include<string.h>

#include "../tools/trace_helper.h"

/*
 * In this program I am adding two matrices n x m, and I am tracking data flow from file read of the matrices to file output of the resulting matrix
 * */
//matrix a, matrix b, matrix result ; n x m matrix
int **m_a, **m_b, **m_r;
int **shadow_m_a, **shadow_m_b, **shadow_m_r;

int **alloc_matrix(int n, int m){
    int **matrix = (int**) malloc(n * sizeof(int*));
    for(int i=0; i < n; i++){
        matrix[i] = (int*) malloc(m * sizeof(int));
    }
    return matrix;
}
void init_matrix(int **matrix, int n, int m){
    for(int i=0; i < n; i++){
        bzero(matrix[i], m * sizeof(int));
    }
}
int main(int argc, char *argv[]){
    FILE *f = fopen("matrix_input", "r");
    int n, m;
    fscanf(f, "%d %d\n", &n, &m);

    m_a = alloc_matrix(n, m);
    m_b = alloc_matrix(n, m);
    m_r = alloc_matrix(n, m);

    shadow_m_a = alloc_matrix(n, m);
    init_matrix(shadow_m_a, n, m);

    shadow_m_b = alloc_matrix(n, m);
    init_matrix(shadow_m_b, n, m);

    shadow_m_r = alloc_matrix(n, m);
    init_matrix(shadow_m_r, n, m);


    //matrix element
    int basic_block_id;

    trace_init();
    enter_block(1, 1);

    enter_block(2, 1);
    for(int i=0; i < n; i++){
        for(int j=0; j < m ;j++){

            basic_block_id = enter_block(3, 1);

            data_flow_trace(shadow_m_a[i][j], basic_block_id);

            fscanf(f, "%d", &m_a[i][j]);

            shadow_m_a[i][j] = basic_block_id;

            exit_block(1);
        }
    }

    exit_block(1);

    enter_block(2, 1);

    for(int i=0; i < n; i++){
        for(int j=0; j < m ;j++){

            basic_block_id = enter_block(3, 1);

            data_flow_trace(shadow_m_b[i][j], basic_block_id);

            fscanf(f, "%d", &m_b[i][j]);

            shadow_m_b[i][j] = basic_block_id;

            exit_block(1);
        }
    }

    exit_block(1);

    fclose(f);


    //because it is a serial implementation we have a single worker

    enter_block(4, 1);

    for(int i=0; i < n; i++){
        for(int j=0; j< m; j++){
            basic_block_id = enter_block(5, 1);

            data_flow_trace(shadow_m_a[i][j], basic_block_id);
            data_flow_trace(shadow_m_b[i][j], basic_block_id);

            m_r[i][j] = m_a[i][j] + m_b[i][j];

            shadow_m_r[i][j] = basic_block_id;

            exit_block(1);
        }
    }


    enter_block(6, 1);
    f = fopen("matrix_addition_result", "w");
    fprintf(f, "%d %d \r\n", n, m);

    for(int i=0; i < n; i++){
        for(int j=0; j< m; j++){
            basic_block_id = enter_block(7, 1);

            data_flow_trace(shadow_m_r[i][j], basic_block_id);
            fprintf(f, "%d ", m_r[i][j]);

            shadow_m_r[i][j] = basic_block_id;

            exit_block(1);
        }
        fprintf(f, "\r\n");
    }
    fclose(f);

    exit_block(1);

    exit_block(1);

    trace_end();

    return 0;
}