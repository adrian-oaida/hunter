#include "matrix_op.h"
#include<stdlib.h>
#include<string.h>

double **alloc_double_matrix(int n, int m){
    double **matrix = (double**) malloc(n * sizeof(double*));
    for(int i=0; i < n; i++){
        matrix[i] = (double*) malloc(m * sizeof(double));
    }
    return matrix;
}

int **alloc_int_matrix(int n, int m){
    int **matrix = (int**) malloc(n * sizeof(int*));
    for(int i=0; i < n; i++){
        matrix[i] = (int*) malloc(m * sizeof(int));
    }
    return matrix;
}
void init_int_matrix(int **matrix, int n, int m){
    for(int i=0; i < n; i++){
        bzero(matrix[i], m * sizeof(int));
    }
}
