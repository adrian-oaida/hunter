package com.edin.hunter.runner;


/**
 * Created by dude on 6/16/17.
 */
public abstract class BaseRunner {
    protected Graph dataFlowGraph;
    protected Graph dynamicCallGraph;
    protected Graph staticCallGraph;


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
