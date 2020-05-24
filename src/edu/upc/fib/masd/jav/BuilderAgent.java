package edu.upc.fib.masd.jav;

import sml.Identifier;
import sml.Kernel;
import sml.StringElement;
import sml.WMElement;

public class BuilderAgent extends VillagerAgent {
    private StringElement build_petition;
    public BuilderAgent(Kernel k, String agentName, String productionsFile, BaronAgent baron, int food, int foodSatiety, int wood) {
        super(k, agentName, productionsFile, baron, food, foodSatiety, wood);
        build_petition = null;
    }

    public void treatSpecificCommand(WMElement command) {
        String name = command.GetAttribute();
        if (name.equals("build-house")) {
            build_house();
        } else {
            System.out.println("Command " + name + " not implemented");
        }
    }


    public void petition(String petition) {
        if (petition.equals("build")) {
            build_petition = inputLink.CreateStringWME("petition", "build");
            System.out.println("Builder received petition for a house");
        } else {
            System.out.println("Builder received non-understood petition for " + petition);
        }

    }
    private void build_house(){
        if(wood >= 5) {
            wood -= 5;
            agent.Update(woodWME, wood);
            if(build_petition != null) {
                build_petition.DestroyWME();
            }
            //TODO: ask environment to instantiate a new collector
            System.out.println("Builder built a house.");
        } else {
            System.out.println("Builder did not have materials to build a house.");
        }
    }
}
