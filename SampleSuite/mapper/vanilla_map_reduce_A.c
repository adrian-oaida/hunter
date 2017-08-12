#include<pthread.h>
#include<stdio.h>
#include<string.h>
#include "../tools/tools.h"

int *data;
int main(int argc, char *argv[]){

    int n;
    n = atoi(argv[1]);
    char reducer[10000];

    data = (int*) malloc(n * sizeof(int));

    for(int i = 0; i < n; i++){
        data[i] = 0;
    }

    for(int i = 0; i < n; i++){
        data[i] = data[i] + 1;
    }
    for(int i = 0; i < n; i++){
        sprintf(reducer, "%s %d", reducer, data[i]);
    }

    return 0;
}
