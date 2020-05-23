package edu.upc.fib.masd.jav;

import sml.Kernel;
import edu.upc.fib.masd.jav.utils.Material;

public abstract class VillagerAgent extends GeneralAgent {
    protected BaronAgent baron;

    public VillagerAgent(Kernel k, String agentName, String productionsFile, BaronAgent baron, int food, int foodSatiety, int wood) {
        super(k, agentName, productionsFile, food, foodSatiety, wood);
        this.baron = baron;
    }

    public abstract void petition(Material material);
}
