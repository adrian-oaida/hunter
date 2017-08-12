#include<stdio.h>
#include<math.h>
#include<stdlib.h>
#include<string.h>

#include "../tools/trace_helper.h"
#include "../tools/matrix_op.h"
/*
 * In this program I am adding two matrices n x m, and I am tracking data flow from file read of the matrices to file output of the resulting matrix
 * */
//matrix a, matrix b, matrix result ; n x m matrix
int **m_a, **m_b, **m_r;

int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");
    int n, m;
    fscanf(f, "%d %d\n", &n, &m);

    m_a = alloc_matrix(n, m);
    m_b = alloc_matrix(n, m);
    m_r = alloc_matrix(n, m);





    //matrix element


    for(int i=0; i < n; i++){
        for(int j=0; j < m ;j++){



            fscanf(f, "%d", &m_a[i][j]);


        }
    }



    for(int i=0; i < n; i++){
        for(int j=0; j < m ;j++){



            fscanf(f, "%d", &m_b[i][j]);


        }
    }



    fclose(f);


    //because it is a serial implementation we have a single worker



    for(int i=0; i < n; i++){
        for(int j=0; j< m; j++){


            m_r[i][j] = m_a[i][j] + m_b[i][j];


        }
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
