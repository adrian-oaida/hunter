#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include "../tools/tools.h"

int n, *array;

void sift_down(int *array, int start, int end);

int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");
    fscanf(f, "%d", &n);

    array = (int*) malloc(n * sizeof(int));


    for(int i = 0; i < n; i++){
        fscanf(f, "%d", &array[i]);
    }
    fclose(f);


    for(int start = (n/2); start >= 0; start--){
        sift_down(array, start, n - 1);
    }

    int end = n - 1;
    int tmp;


    while(end > 0){

        tmp = array[end];



        array[end] = array[0];



        array[0] = tmp;


        end--;
        sift_down(array, 0, end);
    }

    f = fopen(argv[2], "w");
    fprintf(f, "%d\r\n", n);
    for(int i = 0; i < n; i++){
        fprintf(f, "%d ", array[i]);
    }
    fclose(f);
}
void sift_down(int *array, int start, int end){
    int tmp;
    int root = start;

    int left_child;
    int swap;


    while((left_child = root * 2) <= end){

        swap = root;
        if(array[swap] < array[left_child]){
            swap = left_child;
        }
        if(left_child+1 <= end && array[swap] < array[left_child + 1]){
            swap = left_child + 1;
        }
        if(swap == root){
            return;
        }else{

            tmp = array[root];



            array[root] = array[swap];



            array[swap] = tmp;


            root = swap;
        }
    }
}
