package edu.upc.fib.masd.jav.agents;

import edu.upc.fib.masd.jav.Environment;
import sml.Kernel;

import java.util.HashSet;
import java.util.Set;

public abstract class VillagerAgent extends GeneralAgent {
    protected BaronAgent baron;
    protected Set<String> flags;
    protected Set<String> flagsThisTurn;

    public VillagerAgent(Kernel k, String agentName, String productionsFile, BaronAgent baron) {
        super(k, agentName, productionsFile,
                Environment.configuration.get("villager").get("startFood"),
                Environment.configuration.get("villager").get("startFoodSatiety"),
                Environment.configuration.get("villager").get("startWood"));
        this.baron = baron;
        this.flags = new HashSet<>();
        this.flagsThisTurn = new HashSet<>();
    }

    public void init(int food, int foodSatiety, int wood) {
        this.food = food;
        this.foodSatiety = foodSatiety;
        this.wood = wood;
        agent.Update(foodWME, food);
        agent.Update(foodSatietyWME, foodSatiety);
        agent.Update(woodWME, wood);
    }

    public void receive(String material) {
        System.out.println("Agent " + agent.GetAgentName() + " receives " + material);
        if (material.equals("food")) {
            this.food += Environment.configuration.get("villager").get("giveValue");
            this.food = Math.min(this.food, Environment.configuration.get("villager").get("maxFood"));
            agent.Update(foodWME, this.food);
            System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
        } else if (material.equals("wood")) {
            this.wood += Environment.configuration.get("villager").get("giveValue");
            this.wood = Math.min(this.wood, Environment.configuration.get("villager").get("maxWood"));
            agent.Update(woodWME, this.wood);
            System.out.println("Agent " + agent.GetAgentName() + " wood: " + inputLink.GetParameterValue("wood"));
        }
    }

    protected void checkFlags() {
        /*System.out.println("New flags:");
        for (String flag : flagsThisTurn) System.out.print(flag);
        System.out.println("");
        System.out.println("Old flags:");
        for (String flag : flags) System.out.print(flag);
        */
        Set<String> lowered = new HashSet<>(flags);
        lowered.removeAll(flagsThisTurn);

        Set<String> added = new HashSet<>(flagsThisTurn);
        added.removeAll(flags);

        //System.out.println("added:");
        for (String flag : added) {
            baron.addFlag(agent.GetAgentName(), flag);
            //System.out.print(flag);
        }

        //System.out.println("lowered");
        for (String flag : lowered) {
            baron.lowerFlag(agent.GetAgentName(), flag);
            //System.out.print(flag);
        }
        flags = flagsThisTurn;
        flagsThisTurn = new HashSet<>();
    }

    public abstract void petition(String petition);

    public void changeProfession() {
        exciseAgent();
        this.baron.deleteAssignedVillager(this);
        String agentId = this.agent.GetAgentName();
        super.kill();
        Environment.getInstance().changeAgentProfession(kernel, baron, agentId, food, foodSatiety, wood);
    }

    protected void kill() {
        Environment.getInstance().deleteAgent(agent.GetAgentName());
        gui.deleteAgent(agent.GetAgentName());
        this.baron.deleteAssignedVillager(this);
        System.out.println("Agent " + agent.GetAgentName() + " has died");
        super.kill();
    }
}
