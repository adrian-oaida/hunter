package com.edin.hunter.runner;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

/**
 * Created by dude on 6/16/17.
 */
public abstract class BaseRunner {
    protected MultiGraph dataFlowGraph;
    protected MultiGraph dynamicCallGraph;
    protected MultiGraph staticCallGraph;


    public abstract void run(String ...programArgs);

    public Graph getDataFlowGraph(){
        return dataFlowGraph;
    }
    public Graph getDynamicCallGraph(){
        return dynamicCallGraph;
    }
    public Graph getStaticCallGraph(){
        return staticCallGraph;
    }

}
