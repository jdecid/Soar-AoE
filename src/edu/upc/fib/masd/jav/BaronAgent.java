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

    public BaronAgent(Kernel k, String agentName) {
        super(k, agentName, "SOAR_Codes/PRESET_baron_agent.soar");
        villagers = new HashMap<>();
        rootSubordinatesWME = this.inputLink.CreateIdWME("subordinates");
        subordinatesWME = new HashMap<>();
        job = "Baron";
    }

    public void addVillager(VillagerAgent villager) {
        String villagerName = villager.getAgent().GetAgentName();
        villagers.put(villagerName, villager);

        Identifier subordinate = rootSubordinatesWME.CreateIdWME("subordinate");
        subordinate.CreateStringWME("id", villagerName);
        if (villager instanceof CollectorAgent) {
            subordinate.CreateStringWME("type", "collector");
        } else if (villager instanceof BuilderAgent) {
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
        if (villagers.size() == 0) {
            System.out.println("No more villagers alive");
        } else {
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
                    String foodReceiverId = command.GetValueAsString();
                    bestowToVillager(foodReceiverId, "food");
                    break;
                case "bestow-wood":
                    String woodReceiverId = command.GetValueAsString();
                    bestowToVillager(woodReceiverId, "wood");
                    break;
                case "demand-change-profession":
                    String villagerId = command.GetValueAsString();
                    demandChangeProfession(villagerId);
                    break;
                case "demand-build-house":
                    String builderID = command.GetValueAsString();
                    demandToVillager(builderID, "build");
                    break;
                default:
                    System.out.println("Agent " + agent.GetAgentName() + " command not implemented");
                    break;
            }
        }
    }

    private void demandChangeProfession(String villagerId) {
        System.out.println("Agent " + agent.GetAgentName() + " demands change profession to " + villagerId);
        villagers.get(villagerId).changeProfession();
    }

    private void demandToVillager(String villagerId, String petition) {
        System.out.println("Agent " + agent.GetAgentName() + " asks for " + petition + " to " + villagerId);
        villagers.get(villagerId).petition(petition);
        subordinatesWME.get(villagerId).CreateStringWME("sent-demands", petition);
    }

    private void bestowToVillager(String villagerId, String material) {
        System.out.println("Agent " + agent.GetAgentName() + " bestows " + material + " upon " + villagerId);
        if (material.equals("food")) {
            this.food -= Environment.giveValue;
            agent.Update(foodWME, this.food);
            System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
        } else if (material.equals("wood")) {
            this.wood -= Environment.giveValue;
            agent.Update(woodWME, this.wood);
            System.out.println("Agent " + agent.GetAgentName() + " wood: " + inputLink.GetParameterValue("wood"));
        }
        villagers.get(villagerId).receive(material);
    }

    public void receive(String villagerId, String material) {
        System.out.println("Agent " + agent.GetAgentName() + " receives " + material);
        if (material.equals("food")) {
            this.food += Environment.giveValue;
            this.food = Math.min(this.food, Environment.maxBaronFood);
            agent.Update(foodWME, this.food);
            System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
        } else if (material.equals("wood")) {
            this.wood += Environment.giveValue;
            this.wood = Math.min(this.wood, Environment.maxBaronWood);
            agent.Update(woodWME, this.wood);
            System.out.println("Agent " + agent.GetAgentName() + " wood: " + inputLink.GetParameterValue("wood"));
        }

        // Remove sent-demands
        Identifier subordinate = subordinatesWME.get(villagerId);
        for (int i = 0; i < subordinate.GetNumberChildren(); ++i) {
            if (subordinate.GetChild(i).GetAttribute().equals("sent-demands")) {
                String demandValue = subordinate.GetChild(i).GetValueAsString();
                if (demandValue.equals(material)) {
                    subordinate.GetChild(i).DestroyWME();
                }
            }
        }
    }

    public void removeBuildDemand(String builderId) {
        Identifier subordinate = subordinatesWME.get(builderId);
        for (int i = 0; i < subordinate.GetNumberChildren(); ++i) {
            if (subordinate.GetChild(i).GetAttribute().equals("sent-demands")) {
                String demandValue = subordinate.GetChild(i).GetValueAsString();
                if (demandValue.equals("build")) {
                    subordinate.GetChild(i).DestroyWME();
                }
            }
        }
    }

    protected void kill() {
        super.kill();
        kernel.Shutdown();

        System.out.println("\n Baron died. Game over.");
        System.exit(0);
    }

    protected void checkFlags() {
    }

    public void addFlag(String villagerId, String flag) {
        subordinatesWME.get(villagerId).CreateStringWME("petition", flag);
    }

    public void lowerFlag(String villagerId, String flag) {
        // Remove flag
        Identifier subordinate = subordinatesWME.get(villagerId);
        for (int i = 0; i < subordinate.GetNumberChildren(); ++i) {
            if (subordinate.GetChild(i).GetAttribute().equals("petition")) {
                String petitionValue = subordinate.GetChild(i).GetValueAsString();
                if (petitionValue.equals(flag)) {
                    subordinate.GetChild(i).DestroyWME();
                }
            }
        }
    }
}
