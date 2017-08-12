//
// Created by Adrian on 6/2/17.
//


#include<stdio.h>
#include<stdlib.h>
#include<string.h>

int *parentTree;
int n;
void view_tree(int p){
    int *found = (int*)malloc((n+1)*sizeof(int));
    int f_n=1, i_n = 0;
    found[0] = p;

    int c_n;
    printf("%d\t\r\n", p);

    while(f_n < n){
        c_n = f_n;
        for(int i=1; i<=n ;i++)
            for(int j=i_n; j< c_n; j++){
                if(parentTree[i] == found[j]){
                    printf("%d\t", i);
                    found[f_n++] = i;
                }
            }
        printf("\r\n");

        i_n = c_n;
    }

    free(found);
}

int findLink(int start, int final, int current, char *seen){
    if(current == final)
        return 1;

    if(seen[current] == 1)
        return 0;

    seen[current] = 1;
    int result =0 ;
    if(parentTree[current] != 0){
        result = findLink(start, final, parentTree[current], seen);
        if(result >= 1) return result + 1;
    }

    for(int i=1; i<= n; i++){
        if(parentTree[i] == current){
            result = findLink(start, final, i, seen);
            if(result >= 1) return result + 1;
        }
    }
    return 0;
}
void *findLink(int start, int final, int q){

}
int main(int argc, char *argv[]){
    FILE *f = fopen("/Users/dude/edin/msc/hunter/TestPrograms/training.txt", "r");
    int a, b, q;
    fscanf(f, "%d\n", &n);

    parentTree = (int*)malloc((n+1)*sizeof(int));
    char *seen = (char*)malloc((n+1)*sizeof(char));

    bzero(parentTree, n+1);

    for(int i=1; i<n; i++){
        fscanf(f, "%d,%d\n", &a, &b);
        parentTree[b] = a;
    }
    fclose(f);

    view_tree(1);
    bzero(seen, n+1);
    int result = findLink(2,4,2, seen);

    f = fopen("requests.txt", "r");

    while(fscanf(f, "%d,%d,%d", &a,&b,&q) == 3){
        bzero(seen, n+1);
        result = findLink(a,b,a, seen);

        if(result > q){
            printf("%d %d %d < %d -  NO\n", a,b,q, result);
        }else{
            printf("%d %d %d > %d -  YES\n", a,b,q, result);
        }
    }

    free(parentTree);
    free(seen);
    return 0;
}