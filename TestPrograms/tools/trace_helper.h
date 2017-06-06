#ifndef TRACE_HELPER_H
#define TRACE_HELPER_H

void trace_init();
void trace_end();

int enter_block(int block_number, int worker_id);

void trace_variable(int value, int basic_block_id);

void exit_block(int worker_id);

#endif