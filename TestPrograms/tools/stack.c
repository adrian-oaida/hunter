/*
 * developer: Adrian Oaida
 * this code is based on code developed by Murray Cole
 */

#include "stack.h"
#include<stdlib.h>
#include<stdio.h>

stack *create_stack(){
    stack *s = (stack*)malloc(sizeof(stack));
    if(s == NULL){
        perror("Error occurred while allocating memory for a new stack");
        return 0;
    }
    s->top = NULL;

    return s;
}

void free_stack(stack *s){
    stack_node *tmp = s->top, *tmp2;
    while(tmp){
        tmp2 = tmp;
        tmp = tmp->next;
        free(tmp2);
    }
    free(s);
}

void stack_push(stack *s, trace_block b){
    stack_node *n;
    n = (stack_node*) malloc(sizeof(stack_node));
    n->data = b;
    n->next = NULL;

    if(s->top == NULL){
        s->top = n;
    }else{
        n->next = s->top;
        s->top = n;
    }
}

trace_block stack_pop(stack *s){
    stack_node *sn;

    //this situation should not happen
    if(s == NULL || s->top == NULL){
        exit(901);
    }
    sn = s->top;
    s->top = s->top->next;

    trace_block data = sn->data;
    free(sn);

    return data;
}

trace_block stack_top(stack *s){

    //this situation should not happen
    if(s == NULL || s->top == NULL){
        exit(901);
    }

    return s->top->data;
}

int stack_is_empty(stack *s){
    return (s == NULL || s->top == NULL);
}