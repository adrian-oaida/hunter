#include "tools.h"
#include<string.h>
#include<stdlib.h>

void init_int_array(int *array, int size){
    bzero(array, size * sizeof(int));
}

int *create_and_init_int_array(int size){
    int *array = (int*) malloc(size * sizeof(int));
    bzero(array, size * sizeof(int));
    return array;
}

int *get_trace_array(int size){
    int *array = (int*) malloc(size * sizeof(int));
    bzero(array, size * sizeof(int));
    return array;
}
int **get_trace_matrix(int n, int m){
    int **matrix = (int**) malloc(n * sizeof(int*));
    for(int i=0; i < n; i++){
        matrix[i] = (int*) malloc(m * sizeof(int));
        bzero(matrix[i], m * sizeof(int));
    }
    return matrix;
}
