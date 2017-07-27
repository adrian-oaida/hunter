#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include "../tools/tools.h"

#define min(a,b) a<b?a:b

int n, *a, *b;


void bottom_up_merge_sort();
void bottom_up_merge(int left_index, int right_index, int end_index);
void copy_array();


int main(int argc, char *argv[]){

    FILE *f = fopen(argv[1], "r");
    fscanf(f, "%d", &n);

    a = (int*) malloc(n * sizeof(int));
    b = (int*) malloc(n * sizeof(int));

    for(int i = 0; i < n; i++){
        fscanf(f, "%d", &a[i]);
    }
    fclose(f);


    bottom_up_merge_sort();


    f = fopen(argv[2], "w");
    fprintf(f, "%d\n", n);
    for(int i = 0; i < n; i++){
        fprintf(f,"%d ", a[i]);
    }
    fclose(f);

    free(a);
    return 0;
}

void bottom_up_merge_sort(){
    for(int width = 1; width < n; width = 2 * width){
        for(int i = 0; i < n; i = i + 2 * width){
            bottom_up_merge(i, min(i + width, n), min(i + 2 * width, n));
        }

        copy_array();
    }
}

void bottom_up_merge(int left_index, int right_index, int end_index){


    int i = left_index, j = right_index;
    for(int k = left_index; k < end_index; k++){
        if(i < right_index && (j >= end_index || a[i] <= a[j])){

            b[k] = a[i];

            i = i + 1;
        }else{
            b[k] = a[j];

            j = j + 1;
        }
    }
}
void copy_array(){
    for(int i = 0; i < n; i++){

        a[i] = b[i];

    }

}
