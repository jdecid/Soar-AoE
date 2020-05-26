package edu.upc.fib.masd.jav.agents;

import edu.upc.fib.masd.jav.Environment;
import sml.Kernel;
import sml.StringElement;
import sml.WMElement;

public class BuilderAgent extends VillagerAgent {
    private StringElement build_petition;
    public BuilderAgent(Kernel k, String agentName, BaronAgent baron) {
        super(k, agentName, "resources/soar/PRESET_builder_agent.soar", baron);
        build_petition = null;
        job = "Builder";
    }

    public void treatSpecificCommand(WMElement command) {
        String name = command.GetAttribute();
        switch (name) {
            case "build":
                build_house();
                break;
            case "loll":
                System.out.println("Agent " + agent.GetAgentName() + " loll");
                break;
            case "flag":
                String flagName = command.GetValueAsString();
                flagsThisTurn.add(flagName);
                break;
            default:
                System.out.println("Agent " + agent.GetAgentName() + " command " + name + " not implemented");
                break;
        }
    }

    public void petition(String petition) {
        if (petition.equals("build")) {
            build_petition = inputLink.CreateStringWME("petition", "build");
            System.out.println("Agent " + agent.GetAgentName() + " received petition for a house");
        } else {
            System.out.println("Agent " + agent.GetAgentName() + " received non-understood petition for " + petition);
        }

    }
    private void build_house(){
        if(wood >= Environment.woodRequiredToBuild) {
            wood -= Environment.woodRequiredToBuild;
            agent.Update(woodWME, wood);
            if(build_petition != null) {
                build_petition.DestroyWME();
            }
            System.out.println("Agent " + agent.GetAgentName() + " built a house");
            Environment.getInstance().addCollector(kernel, baron);
            baron.removeBuildDemand(agent.GetAgentName());
        } else {
            System.out.println("Agent " + agent.GetAgentName() + " did not have materials to build a house");
        }
        System.out.println("Agent " + agent.GetAgentName() + " wood: " + inputLink.GetParameterValue("wood"));
    }
}
