#ifndef HUNTER_TOOLS_H
#define HUNTER_TOOLS_H

#include "barrier.h"
#include "stack.h"
#include "trace_helper.h"
#include "matrix_op.h"
#include "file_operations.h"

void init_int_array(int *array, int size);

int *get_trace_array(int size);
int **get_trace_matrix(int n, int m);

int *create_and_init_int_array(int size);
#endif