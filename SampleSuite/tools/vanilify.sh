#/bin/sh
#This script was created by Adrian Oaida

egrep -v "shadow|enter_block|trace_init|trace_end|exit_block|data_flow_trace|basic_block_id" $1 > vanilla_$1



