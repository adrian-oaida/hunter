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