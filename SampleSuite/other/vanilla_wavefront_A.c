/*
 * The test examples are from here http://www.cs.tufts.edu/comp/150IR/labs/wavefront.html
 * */

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include "../tools/tools.h"

int **map;
int n, m;
typedef struct{
    int x,y;
} map_pos;
int main(int argc, char *argv[]){

    FILE *f = fopen(argv[1],"r");

    fscanf(f, "%d %d", &n, &m);

    map = (int**)malloc(n * sizeof(int*));
    char tmp[100];
    int startX, startY;
    int stopX, stopY;
    for(int i = 0; i < n; i++){
        map[i] = (int*)malloc(m * sizeof(int));
        for(int j = 0; j < m; j++){
            fscanf(f, "%s ", tmp);
            if(tmp[0] != 'S' && tmp[0] != 'G'){
                map[i][j] = atoi(tmp);
            }
            if(tmp[0] == 'G'){
                startX = i; startY = j;
            }
            if(tmp[0] == 'S'){
                stopX = i; stopY = j;
            }
        }
        fscanf(f,"\n");
    }

    fclose(f);
    //propagate phase


    int qC = 0,qM = 0;
    map_pos *queue = (map_pos*)malloc(n * m * sizeof(map_pos));
    queue[qM].x = startX; queue[qM++].y = startY;
    map[startX][startY] = 2;

    while(qC != qM){
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
                map[x - 1][y - 1] = map[x][y] + 1;
            }
        }
        if(x > 0){
            if(y < m - 1){
                if(map[x - 1][y + 1] == 0){ //2
                    queue[qM].x = x - 1; queue[qM++].y = y + 1;
                    map[x - 1][y + 1] = map[x][y] + 1;
                }
            }

            if(map[x - 1][y] == 0){ // 1
                queue[qM].x = x - 1;queue[qM++].y = y;
                map[x - 1][y] = map[x][y] + 1;
            }
        }
        if(y > 0){
            if(x < n - 1){
                if(map[x + 1][y - 1] == 0){ //6
                    queue[qM].x = x + 1; queue[qM++].y = y - 1;
                    map[x + 1][y - 1] = map[x][y] + 1;
                }
            }
            if(map[x][y - 1] == 0){// 7
                queue[qM].x = x; queue[qM++].y = y - 1;
                map[x][y - 1] = map[x][y] + 1;
            }
        }
        if(x < n - 1 && y < m - 1) {
            if (map[x + 1][y + 1] == 0) {//4
                queue[qM].x = x + 1;
                queue[qM++].y = y + 1;
                map[x + 1][y + 1] = map[x][y] + 1;
            }
        }
        if(x < n - 1){
            if(map[x + 1][y] == 0){//5
                queue[qM].x = x + 1; queue[qM++].y = y;
                map[x + 1][y] = map[x][y] + 1;
            }
        }
        if(y < m - 1){
            if(map[x][y + 1] == 0){//3
                queue[qM].x = x; queue[qM].y = y + 1;
                map[x][y + 1] = map[x][y] + 1;
            }
        }


    }
    printf("\n");
    for(int i = 0; i < n ; i++){
        for(int j = 0; j < m ; j++){
            printf("%3d ", map[i][j]);
        }
        printf("\n");
    }

    free(map);//this does not completly free it
    free(queue);//this does not completly free it

    return 0;
}