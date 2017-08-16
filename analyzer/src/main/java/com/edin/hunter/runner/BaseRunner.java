package com.edin.hunter.runner;


import com.edin.hunter.graph.DirectedGraph;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by dude on 6/16/17.
 */
public abstract class BaseRunner {
    protected DirectedGraph dataFlowGraph;
    protected DirectedGraph dynamicCallGraph;
    protected DirectedGraph staticCallGraph;

    protected boolean instructionsAsLabels = false;
    protected boolean augmentDataFlowNodes = false;
    protected PrintStream execOutputStream;

    public abstract void run(String ...programArgs);

    public void redirectRunOutput(PrintStream out){
        execOutputStream = out;
    }
    public DirectedGraph getDataFlowGraph(){
        return dataFlowGraph;
    }
    public DirectedGraph getDynamicCallGraph(){
        return dynamicCallGraph;
    }
    public DirectedGraph getStaticCallGraph(){
        return staticCallGraph;
    }

    public void setInstructionsAsLabes(boolean instr){
        this.instructionsAsLabels = instr;
    }
    public void augmentDataFlowNodes(boolean instr){
        this.augmentDataFlowNodes = instr;
    }

}
