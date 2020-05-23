package edu.upc.fib.masd.jav;

import sml.Identifier;
import sml.Kernel;
import sml.WMElement;

import java.util.HashMap;
import java.util.Map;


public class BaronAgent extends GeneralAgent {
    private final Map<String, VillagerAgent> collectors;
    private final Map<String, VillagerAgent> builders;
    private final Identifier subordinatesWME;

    public BaronAgent(Kernel k, String agentName, String productionsFile, int food, int foodSatiety) {
        super(k, agentName, productionsFile, food, foodSatiety);
        collectors = new HashMap<>();
        builders = new HashMap<>();

        subordinatesWME = this.inputLink.CreateIdWME("subordinates");
    }

    ////////////////////////////////////////////////////////
    // Add and remove builders and collectors as vassals. //
    ////////////////////////////////////////////////////////

    public void addCollector(CollectorAgent collector) {
        addVillager(collector, collectors, "collector");
    }

    public void addBuilder(BuilderAgent builder) {
        addVillager(builder, builders, "builder");
    }

    private void addVillager(VillagerAgent villager, Map<String, VillagerAgent> villagers, String type) {
        String villagerName = villager.getAgent().GetAgentName();
        villagers.put(villagerName, villager);

        Identifier subordinate = subordinatesWME.CreateIdWME("subordinate");
        subordinate.CreateStringWME("id", villagerName);
        subordinate.CreateStringWME("type", type);
    }

    public void deleteAssignedCollector(CollectorAgent collector) {
        deleteAssignedVillager(collector, collectors);
    }

    public void deleteAssignedBuilder(BuilderAgent builder) {
        deleteAssignedVillager(builder, builders);
    }

    private void deleteAssignedVillager(VillagerAgent villager, Map<String, VillagerAgent> villagers) {
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
                System.out.println("Demand food");
                break;
            case "demand-wood":
                System.out.println("Demand wood");
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

    protected void kill() {
        super.kill();
        kernel.Shutdown();

        System.out.println("\n Baron died. Game over.");
        System.exit(0);
    }

}
