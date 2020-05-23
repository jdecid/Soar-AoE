package edu.upc.fib.masd.jav;

import sml.Kernel;

public abstract class VillagerAgent extends GeneralAgent {
    protected BaronAgent baron;

    public VillagerAgent(Kernel k, String agentName, String productionsFile, BaronAgent baron, int food, int foodSatiety) {
        super(k, agentName, productionsFile, food, foodSatiety);
        this.baron = baron;
    }
}
