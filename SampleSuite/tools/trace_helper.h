#ifndef TRACE_HELPER_H
#define TRACE_HELPER_H

void trace_init();
void trace_end();

int enter_block(int block_number, int worker_id, char *instruction);

void data_flow_trace(int from_block_id, int to_block_id, int worker_id);

void exit_block(int worker_id);

#endif