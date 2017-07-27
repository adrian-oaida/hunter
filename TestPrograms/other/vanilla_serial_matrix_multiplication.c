#include<stdlib.h>
#include<stdio.h>
#include "../tools/trace_helper.h"
#include "../tools/matrix_op.h"

//matrix dimensions ; n x m * m x p = n x p
int n,m,p;

int **m_a, **m_b, **m_r;


int main(int argc, char *argv[]){
    FILE *f;

    f = fopen(argv[1], "r");

    fscanf(f, "%d %d %d", &n, &m, &p);

    m_a = alloc_matrix(n, m);
    m_b = alloc_matrix(m, p);

    m_r = alloc_matrix(n, p);
    init_int_matrix(m_r, n, p);






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
        for(int k = 0; k < p; k++){
            for(int j = 0; j < m; j++){

                m_r[i][k] = m_r[i][k] + ( m_a[i][j] * m_b[j][k] );




            }
        }
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
