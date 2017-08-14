/*
 * The test examples are from here http://www.cs.tufts.edu/comp/150IR/labs/wavefront.html
 * */

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include "../tools/tools.h"

int **map;
int **shadow_map;
int n, m;
typedef struct{
    int x,y;
} map_pos;
int main(int argc, char *argv[]){

    FILE *f = fopen(argv[1],"r");

    fscanf(f, "%d %d", &n, &m);

    map = (int**)malloc(n * sizeof(int*));
    shadow_map = get_trace_matrix( n, m);

    trace_init();
    enter_block(1, 1, "initial");
    int basic_block_id;
    char tmp[100];
    int shadow_tmp = 0;
    int startX, startY;
    int stopX, stopY;


    for(int i = 0; i < n; i++){
        basic_block_id = enter_block(2, 1, "for(int i = 0; i < n; i++)");
            map[i] = (int*)malloc(m * sizeof(int));
            for(int j = 0; j < m; j++){
                basic_block_id = enter_block(3, 1, "for(int j = 0; j < m; j++)");
                    basic_block_id = enter_block(4, 1, "fscanf(f, \\\"%s \\\", tmp)");

                        fscanf(f, "%s ", tmp);

                    shadow_tmp = basic_block_id;
                    exit_block(1);


                    if(tmp[0] != 'S' && tmp[0] != 'G'){
                        basic_block_id = enter_block(5, 1, "if(tmp[0] != 'S' && tmp[0] != 'G')");
                            basic_block_id = enter_block(6, 1, "map[i][j] = atoi(tmp)");

                                map[i][j] = atoi(tmp);

                            data_flow_trace(shadow_tmp, basic_block_id, 1);
                            shadow_map[i][j] = basic_block_id;
                            exit_block(1);
                        exit_block(1);
                    }

                    if(tmp[0] == 'G'){
                        startX = i; startY = j;
                    }
                    if(tmp[0] == 'S'){
                        stopX = i; stopY = j;
                    }
                exit_block(1);
            }
            fscanf(f,"\n");
        exit_block(1);
    }
    fclose(f);
    //propagate phase


    int qC = 0,qM = 0;
    map_pos *queue = (map_pos*)malloc(n * m * sizeof(map_pos));
    queue[qM].x = startX; queue[qM++].y = startY;

    basic_block_id = enter_block(7, 1, "map[startX][startY] = 2");

        map[startX][startY] = 2;

    shadow_map[startX][startY] = basic_block_id;
    exit_block(1);

    while(qC != qM){
        basic_block_id = enter_block(8, 1, "while(qC != qM)");
        //neigh are
        //   8 1 2
        //   7 C 3
        //   6 5 4
        int x = queue[qC].x, y = queue[qC++].y;
        if(x == stopX && y == stopY)
            break;

        if(x > 0 && y > 0){
            if(map[x - 1][y - 1] == 0){ // 8
                queue[qM].x = x - 1;queue[qM++].y = y - 1;
                basic_block_id = enter_block(9, 1, "map[x - 1][y - 1] = map[x][y] + 1");

                    map[x - 1][y - 1] = map[x][y] + 1;

                data_flow_trace(shadow_map[x][y], basic_block_id, 1);
                shadow_map[x - 1][y - 1] = basic_block_id;
                exit_block(1);
            }
        }
        if(x > 0){
            if(y < m - 1){
                if(map[x - 1][y + 1] == 0){ //2
                    queue[qM].x = x - 1; queue[qM++].y = y + 1;
                    basic_block_id = enter_block(10, 1, "map[x - 1][y + 1] = map[x][y] + 1");

                        map[x - 1][y + 1] = map[x][y] + 1;

                    data_flow_trace(shadow_map[x][y], basic_block_id, 1);
                    shadow_map[x - 1][y + 1] = basic_block_id;
                    exit_block(1);
                }
            }

            if(map[x - 1][y] == 0){ // 1
                queue[qM].x = x - 1;queue[qM++].y = y;
                basic_block_id = enter_block(11, 1, "map[x - 1][y] = map[x][y] + 1");

                    map[x - 1][y] = map[x][y] + 1;

                data_flow_trace(shadow_map[x][y], basic_block_id, 1);
                shadow_map[x - 1][y] = basic_block_id;
                exit_block(1);

            }
        }
        if(y > 0){
            if(x < n - 1){
                if(map[x + 1][y - 1] == 0){ //6
                    queue[qM].x = x + 1; queue[qM++].y = y - 1;
                    basic_block_id = enter_block(12, 1, "map[x + 1][y - 1] = map[x][y] + 1");

                        map[x + 1][y - 1] = map[x][y] + 1;

                    data_flow_trace(shadow_map[x][y], basic_block_id, 1);
                    shadow_map[x + 1][y - 1] = basic_block_id;
                    exit_block(1);

                }
            }
            if(map[x][y - 1] == 0){// 7
                queue[qM].x = x; queue[qM++].y = y - 1;
                basic_block_id = enter_block(13, 1, "map[x][y - 1] = map[x][y] + 1");

                    map[x][y - 1] = map[x][y] + 1;

                data_flow_trace(shadow_map[x][y], basic_block_id, 1);
                shadow_map[x][y - 1] = basic_block_id;
                exit_block(1);

            }
        }
        if(x < n - 1 && y < m - 1) {
            if (map[x + 1][y + 1] == 0) {//4
                queue[qM].x = x + 1;queue[qM++].y = y + 1;
                basic_block_id = enter_block(14, 1, "map[x + 1][y + 1] = map[x][y] + 1");

                    map[x + 1][y + 1] = map[x][y] + 1;

                data_flow_trace(shadow_map[x][y], basic_block_id, 1);
                shadow_map[x + 1][y + 1] = basic_block_id;
                exit_block(1);

            }
        }
        if(x < n - 1){
            if(map[x + 1][y] == 0){//5
                queue[qM].x = x + 1; queue[qM++].y = y;
                basic_block_id = enter_block(15, 1, "map[x + 1][y] = map[x][y] + 1");

                    map[x + 1][y] = map[x][y] + 1;

                data_flow_trace(shadow_map[x][y], basic_block_id, 1);
                shadow_map[x + 1][y] = basic_block_id;
                exit_block(1);

            }
        }
        if(y < m - 1){
            if(map[x][y + 1] == 0){//3
                queue[qM].x = x; queue[qM].y = y + 1;
                basic_block_id = enter_block(16, 1, "map[x][y + 1] = map[x][y] + 1");

                    map[x][y + 1] = map[x][y] + 1;

                data_flow_trace(shadow_map[x][y], basic_block_id, 1);
                shadow_map[x][y + 1] = basic_block_id;
                exit_block(1);

            }
        }

        exit_block(1);
    }

    printf("\n");

    f = fopen(argv[2], "w");

    for(int i = 0; i < n ; i++){
        enter_block(17, 1, "for(int i = 0; i < n ; i++)");
        for(int j = 0; j < m ; j++){
            enter_block(18, 1, "for(int j = 0; j < m ; j++)");
                basic_block_id = enter_block(19, 1, "printf(\\\"%3d \\\", map[i][j])");

                    fprintf(f, "%3d ", map[i][j]);

                data_flow_trace(shadow_map[i][j], basic_block_id, 1);
                exit_block(1);
            exit_block(1);

        }
        fprintf(f, "\n");
        exit_block(1);

    }
    fclose(f);
    exit_block(1);
    trace_end();
    free(map);//this does not completly free it
    free(queue);//this does not completly free it

    return 0;
}