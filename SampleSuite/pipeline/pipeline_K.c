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
int *shadow_data;


int main(int argc, char *argv[]){


    trace_init();
    int i;

    data_size = atoi(argv[1]);


    data = (int*)malloc(data_size * sizeof(int));

    shadow_data = get_trace_array(data_size);

    int basic_block_id;

    basic_block_id = enter_block(1, 1, "stage1()");

        stage1();

    exit_block(1);

    basic_block_id = enter_block(2, 1, "stage2()");

        stage2();

    exit_block(1);

    basic_block_id = enter_block(3, 1, "stage3()");

        stage3();

    exit_block(1);

    FILE *results = fopen("results", "w");
    for(i = 0;i < data_size; i++){
        fprintf(results, "%d\n", data[i]);
    }
    fprintf(results, "\n");

    trace_end();
    return 0;
}

void stage1(){
    int stage1_state = 0;
    int shadow_state = 0;
    int basic_block_id;

    basic_block_id = enter_block(4, 1, " for(int i = 0 ; i < data_size; i++)");
    for(int i = 0 ; i < data_size; i++){
        basic_block_id = enter_block(5, 1, " for(int i = 0 ; i < data_size; i++)");
            basic_block_id = enter_block(6, 1, "stage1_state += data[i]");

                stage1_state += data[i];

            data_flow_trace(shadow_data[i], basic_block_id, 1);
            data_flow_trace(shadow_state, basic_block_id, 1);
            shadow_state = basic_block_id;
            exit_block(1);
            basic_block_id = enter_block(7, 1, "data[i] = 23");

                data[i] = data[i] + 23;

            data_flow_trace(shadow_data[i], basic_block_id, 1);
            shadow_data[i] = basic_block_id;
            exit_block(1);
        exit_block(1);
    }
    exit_block(1);

}
void stage2(){
    int tmp;
    int shadow_tmp = 0;
    int basic_block_id;

    basic_block_id = enter_block(8, 1, "for(int i = 0; i < data_size; i++)");
    for(int i = 0; i < data_size; i++){
        basic_block_id = enter_block(9, 1, "for(int i = 0; i < data_size; i++)");
            basic_block_id = enter_block(10, 1, "tmp = data[i]");

            tmp = data[i];

            data_flow_trace(shadow_data[i], basic_block_id, 1);
            shadow_tmp = basic_block_id;
            exit_block(1);
            basic_block_id = enter_block(11, 1, "while(tmp != 0)");
            while(tmp != 0){
                basic_block_id = enter_block(12, 1, "while(tmp != 0)");
                    basic_block_id = enter_block(13, 1, "tmp = tmp / 3");

                        tmp = tmp / 3;

                    data_flow_trace(shadow_tmp, basic_block_id, 1);
                    shadow_tmp = basic_block_id;
                    exit_block(1);
                exit_block(1);
            }
            exit_block(1);
            basic_block_id = enter_block(14, 1, "data[i] = tmp");

                data[i] = tmp;

            data_flow_trace(shadow_tmp, basic_block_id, 1);
            shadow_data[i] = basic_block_id;
            exit_block(1);
        exit_block(1);
    }
    exit_block(1);
}
void stage3(){
    int stage3_state = 0;
    int basic_block_id;
    int shadow_stage3_state = 0;

    basic_block_id = enter_block(15, 1, "for(int i = 0; i < data_size; i++)");
    for(int i = 0; i < data_size; i++){
        basic_block_id = enter_block(16, 1, "for(int i = 0; i < data_size; i++)");
            basic_block_id = enter_block(17, 1, "stage3_state+= data[i]");

                stage3_state+= data[i];

            data_flow_trace(shadow_data[i], basic_block_id, 1);
            data_flow_trace(shadow_stage3_state, basic_block_id, 1);
            shadow_stage3_state = basic_block_id;
            exit_block(1);

            if(stage3_state % 3 == 0){
                basic_block_id = enter_block(18, 1, "if(state % 3 == 0)");
                    basic_block_id = enter_block(19, 1, "data[i] = state + data[i]");

                        data[i] = stage3_state + data[i];

                    data_flow_trace(shadow_stage3_state, basic_block_id, 1);
                    data_flow_trace(shadow_data[i], basic_block_id, 1);
                    shadow_data[i] = basic_block_id;
                    exit_block(1);
                exit_block(1);
            }else{
                basic_block_id = enter_block(20, 1, "if(state % 3 == 0) else");
                    basic_block_id = enter_block(21, 1, "data[i]++");

                        data[i]++;

                    data_flow_trace(shadow_data[i], basic_block_id, 1);
                    shadow_data[i] = basic_block_id;
                    exit_block(1);
                exit_block(1);
            }
        exit_block(1);
    }
    exit_block(1);

}

