package edu.upc.fib.masd.jav;

import sml.Kernel;

import java.util.HashSet;
import java.util.Set;

public abstract class VillagerAgent extends GeneralAgent {
    protected BaronAgent baron;
    protected Set<String> flags;
    protected Set<String> flagsThisTurn;

    public VillagerAgent(Kernel k, String agentName, String productionsFile, BaronAgent baron) {
        super(k, agentName, productionsFile);
        this.baron = baron;
        this.flags = new HashSet<>();
        this.flagsThisTurn = new HashSet<>();
    }

    public void receive(String material) {
        System.out.println("Agent " + agent.GetAgentName() + " receives " + material);
        if (material.equals("food")) {
            this.food += Environment.giveValue;
            agent.Update(foodWME, this.food);
            System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
        } else if (material.equals("wood")) {
            this.wood += Environment.giveValue;
            agent.Update(woodWME, this.wood);
            System.out.println("Agent " + agent.GetAgentName() + " wood: " + inputLink.GetParameterValue("wood"));
        }
    }

    protected void checkFlags() {
        Set<String> lowered = new HashSet<>(flags);
        lowered.removeAll(flagsThisTurn);

        Set<String> added = new HashSet<>(flagsThisTurn);
        added.removeAll(flags);

        for (String flag : added) {
            baron.addFlag(agent.GetAgentName(), flag);
        }

        for (String flag : lowered) {
            baron.lowerFlag(agent.GetAgentName(), flag);
        }
    }

    public abstract void petition(String petition);


    protected void kill() {
        Environment.getInstance().deleteAgent(agent.GetAgentName());
        gui.deleteAgent(agent.GetAgentName());

        this.baron.deleteAssignedVillager(this);
        System.out.println("Agent " + agent.GetAgentName() + " has died");
        super.kill();
    }
}
