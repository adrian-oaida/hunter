/*
 * developer: Adrian Oaida
 *
 */

#include<stdio.h>
#include<math.h>
#include<stdlib.h>
#include<string.h>
#include<pthread.h>
#include "../tools/tools.h"

void stage1();
void stage2();
void stage3();

int data_size;

int *data;


int main(int argc, char *argv[]){


    int i;

    data_size = atoi(argv[1]);


    data = (int*)malloc(data_size * sizeof(int));



    stage1();

    stage2();

    stage3();

    FILE *results = fopen("results", "w");
    for(i = 0;i < data_size; i++){
        fprintf(results, "%d\n", data[i]);
    }
    fprintf(results, "\n");

    return 0;
}

void stage1(){
    int stage1_state = 0;

    for(int i = 0 ; i < data_size; i++){

        stage1_state += data[i];

        data[i] = data[i] + 23;
    }
}
void stage2(){
    int tmp;

    for(int i = 0; i < data_size; i++){
        tmp = data[i];

        while(tmp != 0){
            tmp = tmp / 3;

        }
        data[i] = tmp;

    }
}
void stage3(){
    int stage3_state = 0;

    for(int i = 0; i < data_size; i++){
        stage3_state+= data[i];

        if(stage3_state % 3 == 0){
            data[i] = stage3_state + data[i];
        }else{
            data[i]++;
        }
    }
}

