#ifndef HUNTER_STACK_H
#define HUNTER_STACK_H

typedef struct stack_node_tag stack_node;
typedef struct stack_tag stack;


typedef struct trace_block_tag{
    int static_id;
    int dynamic_id;
}trace_block;

struct stack_node_tag {
    trace_block data;
    stack_node *next;
};

struct stack_tag {
    stack_node  *top;
};


stack *create_stack();
void free_stack(stack *s);


void stack_push(stack *s, trace_block b);
trace_block stack_pop(stack *s);
trace_block stack_top(stack *s);
int stack_is_empty(stack *);

#endif