package edu.upc.fib.masd.jav;

import sml.Identifier;
import sml.Kernel;
import sml.WMElement;

import java.util.HashMap;
import java.util.Map;


public class BaronAgent extends GeneralAgent {
    private final Map<String, VillagerAgent> villagers;
    private final Identifier rootSubordinatesWME;
    private final Map<String, Identifier> subordinatesWME;

    public BaronAgent(Kernel k, String agentName, String productionsFile, int food, int foodSatiety, int wood) {
        super(k, agentName, productionsFile, food, foodSatiety, wood);
        villagers = new HashMap<>();
        rootSubordinatesWME = this.inputLink.CreateIdWME("subordinates");
        subordinatesWME = new HashMap<>();
    }

    public void addVillager(VillagerAgent villager) {
        String villagerName = villager.getAgent().GetAgentName();
        villagers.put(villagerName, villager);

        Identifier subordinate = rootSubordinatesWME.CreateIdWME("subordinate");
        subordinate.CreateStringWME("id", villagerName);
        if (villager instanceof CollectorAgent) {
            subordinate.CreateStringWME("type", "collector");
        }
        else if (villager instanceof BuilderAgent) {
            subordinate.CreateStringWME("type", "builder");
        }
        subordinatesWME.put(villagerName, subordinate);
    }

    public void deleteAssignedVillager(VillagerAgent villager) {
        String villagerName = villager.getAgent().GetAgentName();
        villagers.remove(villagerName);
        subordinatesWME.get(villagerName).DestroyWME();
        subordinatesWME.remove(villagerName);
    }

    public void treatSpecificCommand(WMElement command) {
        String name = command.GetAttribute();
        switch (name) {
            case "demand-food":
                String foodCollectorId = command.GetValueAsString();
                demandToVillager(foodCollectorId, "food");
                break;
            case "demand-wood":
                String woodCollectorId = command.GetValueAsString();
                demandToVillager(woodCollectorId, "wood");
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

    private void demandToVillager(String villagerId, String material) {
        villagers.get(villagerId).petition(material);
        subordinatesWME.get(villagerId).CreateStringWME("sent-demands", material);
        System.out.println("Agent " + agent.GetAgentName() + " asks for " + material + " to " + villagerId);
    }

    public void receiveFood(String villagerId, int num) {
        this.food += num;
        agent.Update(foodWME, this.food);

        // Remove sent-demands food
        Identifier subordinate = subordinatesWME.get(villagerId);
        for (int i=0; i<subordinate.GetNumberChildren(); ++i) {
            String material = subordinate.GetChild(i).GetAttribute();
            if (material.equals("food")) {
                subordinate.GetChild(i).DestroyWME();
            }
        }

        System.out.println("Agent " + agent.GetAgentName() + " receives food: " + num);
        System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
    }

    public void receiveWood(String villagerId, int num) {
        this.wood += num;
        agent.Update(woodWME, this.wood);

        // Remove sent-demands food
        Identifier subordinate = subordinatesWME.get(villagerId);
        for (int i=0; i<subordinate.GetNumberChildren(); ++i) {
            String material = subordinate.GetChild(i).GetAttribute();
            if (material.equals("wood")) {
                subordinate.GetChild(i).DestroyWME();
            }
        }

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
