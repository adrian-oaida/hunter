/*
 * developer: Adrian Oaida
 * this code is based on code developed by Murray Cole
 */

#include <pthread.h>
#include <stdio.h>
#include "trace_helper.h"
#include "stack.h"

#define MAX_TRACE_THREADS 100


static stack *trace_stacks[MAX_TRACE_THREADS];

static int max_block_ticket = 0;
static int data_edges = 0;

pthread_mutex_t print_lock;
pthread_mutex_t ticket_lock;

int max_static_blocks_nr = 1;

void trace_init(){
    pthread_mutex_init(&print_lock, NULL);
    pthread_mutex_init(&ticket_lock, NULL);

    printf("format BlockCall: BC  ParentBlockDynamicID ParentBlockStaticID ChildBlockDynamicID ChildBlockStaticID\n");
    printf("format DataFlow:  DF  WriterBlockDynamicID DFReaderBlockDynamicID\n");
    printf("format TraceSize: ATS StaticBlocks DynamicBlocks DataFlowEdges\n");


    for(int i=0; i < MAX_TRACE_THREADS; i++){
        trace_stacks[i] = create_stack();
        trace_block b = {0, 0};
        stack_push(trace_stacks[i], b);
    }
}

void trace_end(){
    printf("ATS %d %d %d\n", max_static_blocks_nr + 1, max_block_ticket+1, data_edges);
}

int get_max_dynamic_block_id(){
    pthread_mutex_lock(&ticket_lock);

    max_block_ticket+=1;
    int ticket = max_block_ticket;

    pthread_mutex_unlock(&ticket_lock);
    return ticket;
}

int enter_block(int static_block_nr, int worker_id){

    if(static_block_nr > max_static_blocks_nr){
        max_static_blocks_nr = static_block_nr;
    }
    trace_block old_trace_block = stack_top(trace_stacks[worker_id]);

    trace_block new_trace_block = {static_block_nr, get_max_dynamic_block_id()};
    stack_push(trace_stacks[worker_id], new_trace_block);


    pthread_mutex_lock(&print_lock);
    printf("BC %d %d %d %d\n", old_trace_block.dynamic_id, old_trace_block.static_id, new_trace_block.dynamic_id, new_trace_block.static_id);
    pthread_mutex_unlock(&print_lock);

    return new_trace_block.dynamic_id;
}

void trace_variable(int value, int basic_block_id){
    pthread_mutex_lock(&print_lock);
    printf("DF %d %d\n", value, basic_block_id);
    data_edges++;
    pthread_mutex_unlock(&print_lock);
}

void exit_block(int worker_id){
    stack_pop(trace_stacks[worker_id]);
}