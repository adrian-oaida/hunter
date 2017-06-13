#include<stdio.h>
#include<stdlib.h>

int n;
int *array;

int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");
    fscanf(f, "%d", &n);

    array = (int*) malloc(n * sizeof(int));

    for(int i = 0; i < n; i++){
        fscanf(f, "%d", &array[i]);
    }
    fclose(f);

    int swap;

    for(int i = 0; i < n; i++){
        for(int j = 0; j < n - 1; j++){

            if(array[j] > array[j + 1]){

                swap = array[j + 1];
                array[j + 1] = array[j];
                array[j] = swap;
            }
        }
    }
    f = fopen(argv[2], "w");

    fprintf(f, "%d \n", n);
    for(int i = 0; i < n; i++){
        fprintf(f, "%d ", array[i]);
    }

    fclose(f);


    free(array);
    return 0;
}