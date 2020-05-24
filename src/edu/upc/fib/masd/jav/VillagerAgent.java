package edu.upc.fib.masd.jav;

import sml.Kernel;

public abstract class VillagerAgent extends GeneralAgent {
    protected BaronAgent baron;

    public VillagerAgent(Kernel k, String agentName, String productionsFile, BaronAgent baron, int food, int foodSatiety, int wood) {
        super(k, agentName, productionsFile, food, foodSatiety, wood);
        this.baron = baron;
    }
    public void receive(String material) {
        if (material.equals("food")) {
            this.food += 2;
            agent.Update(foodWME, this.food);
        } else if (material.equals("wood")) {
            this.wood += 2;
            agent.Update(woodWME, this.wood);
        }
        updateInfoGUI();
    }

    public abstract void petition(String material);
}
