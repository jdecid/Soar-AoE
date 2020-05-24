package edu.upc.fib.masd.jav;

import sml.Kernel;
import sml.WMElement;

public class BuilderAgent extends VillagerAgent {

    public BuilderAgent(Kernel k, String agentName, String productionsFile, BaronAgent baron, int food, int foodSatiety, int wood) {
        super(k, agentName, productionsFile, baron, food, foodSatiety, wood);
    }

    public void treatSpecificCommand(WMElement command) {
        String name = command.GetAttribute();
        if (name.equals("build-house")) {

        } else {
            System.out.println("Command " + name + " not implemented");
        }
    }


    public void petition(String material) {

    }
}
