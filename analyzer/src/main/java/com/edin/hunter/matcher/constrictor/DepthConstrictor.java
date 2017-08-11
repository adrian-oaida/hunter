package com.edin.hunter.matcher.constrictor;

import com.edin.hunter.graph.DirectedGraph;
import com.edin.hunter.runner.BaseRunner;

public class DepthConstrictor extends AgglomerationConstrictor {

    public DepthConstrictor(BaseRunner runner) {
        super(runner);
    }

    @Override
    public boolean eliminateAgglomeration() {
        return false;
    }
}
