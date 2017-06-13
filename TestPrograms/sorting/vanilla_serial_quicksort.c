#include <stdio.h>
#include <stdlib.h>

int n, *a;

int partition(int lo, int hi){

    int pivot = a[hi];

    int i = lo - 1;
    int tmp;
    for(int j = lo; j < hi; j++){

        if(a[j] <= pivot){
            i++;
            if(i != j){

                tmp = a[i]; a[i] = a[j]; a[j] = tmp;
            }
        }
    }
    tmp = a[i + 1]; a[i + 1] = a[hi]; a[hi] = tmp;
    return i + 1;
}
void quick_sort(int lo, int hi){
    if(lo < hi){
        int p = partition(lo, hi);
        quick_sort(lo, p - 1);
        quick_sort(p + 1, hi);
    }
}


int main(int argc, char *argv[]){
    FILE *f = fopen(argv[1], "r");
    fscanf(f, "%d", &n);

    a = (int*) malloc(n * sizeof(int));

    for(int i = 0; i < n; i++){
        fscanf(f, "%d", &a[i]);
    }

    fclose(f);

    quick_sort(0, n - 1);

    f = fopen(argv[2], "w");
    fprintf(f, "%d\n", n);
    for(int i = 0; i < n; i++){
        fprintf(f, "%d ", a[i]);
    }
    fclose(f);
    return 0;
}

