#ifndef MATRIX_OP_H
#define MATRIX_OP_H

double **alloc_double_matrix(int n, int m);
int **alloc_int_matrix(int n, int m);
void init_int_matrix(int **matrix, int n, int m);
void init_double_matrix(double *matrix, int n, int m);

int **alloc_and_init_int_matrix(int n, int m);


#endif