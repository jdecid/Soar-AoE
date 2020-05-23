package edu.upc.fib.masd.jav;

import edu.upc.fib.masd.jav.utils.Material;
import sml.Identifier;
import sml.Kernel;
import sml.WMElement;

import java.util.HashMap;
import java.util.Map;


public class BaronAgent extends GeneralAgent {
    private final Map<String, VillagerAgent> villagers;
    private final Identifier subordinatesWME;

    public BaronAgent(Kernel k, String agentName, String productionsFile, int food, int foodSatiety, int wood) {
        super(k, agentName, productionsFile, food, foodSatiety, wood);
        villagers = new HashMap<>();
        subordinatesWME = this.inputLink.CreateIdWME("subordinates");
    }

    public void addVillager(VillagerAgent villager) {
        String villagerName = villager.getAgent().GetAgentName();
        villagers.put(villagerName, villager);

        Identifier subordinate = subordinatesWME.CreateIdWME("subordinate");
        subordinate.CreateStringWME("id", villagerName);
        if (villager instanceof CollectorAgent) {
            subordinate.CreateStringWME("type", "collector");
        }
        else if (villager instanceof BuilderAgent) {
            subordinate.CreateStringWME("type", "builder");
        }
    }

    public void deleteAssignedVillager(VillagerAgent villager) {
        String collectorName = villager.getAgent().GetAgentName();
        villagers.remove(collectorName);

        for (int i = 0; i < subordinatesWME.GetNumberChildren(); ++i) {
            if (collectorName.equals(subordinatesWME.GetChild(i).GetAttribute())) {
                subordinatesWME.GetChild(i).DestroyWME();
                break;
            }
        }
    }

    public void treatSpecificCommand(WMElement command) {
        String name = command.GetAttribute();
        switch (name) {
            case "demand-food":
                String foodCollectorId = command.GetValueAsString();
                demandToVillager(foodCollectorId, Material.FOOD);
                break;
            case "demand-wood":
                String woodCollectorId = command.GetValueAsString();
                demandToVillager(woodCollectorId, Material.WOOD);
                break;
            case "bestow-food":
                System.out.println("Bestow food");
                break;
            case "bestow-wood":
                System.out.println("Bestow wood");
                break;
            case "demand-change-profession":
                System.out.println("Demand Change Profession");
                break;
            case "demand-build-house":
                System.out.println("Demand Build House");
                break;
            default:
                System.out.printf("Command %s not implemented%n", name);
                break;
        }
    }

    private void demandToVillager(String collectorId, Material material) {
        villagers.get(collectorId).petition(material);
        System.out.println("Agent " + agent.GetAgentName() + " asks for " + material.string + " to " + collectorId);
    }

    public void receiveFood(int num) {
        this.food += num;
        agent.Update(foodWME, this.food);
        System.out.println("Agent " + agent.GetAgentName() + " receives food: " + num);
        System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
    }

    public void receiveWood(int num) {
        this.wood += num;
        agent.Update(woodWME, this.wood);
        System.out.println("Agent " + agent.GetAgentName() + " receives wood: " + num);
        System.out.println("Agent " + agent.GetAgentName() + " wood: " + inputLink.GetParameterValue("wood"));
    }

    protected void kill() {
        super.kill();
        kernel.Shutdown();

        System.out.println("\n Baron died. Game over.");
        System.exit(0);
    }

}
