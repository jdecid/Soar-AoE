package edu.upc.fib.masd.jav;

import edu.upc.fib.masd.jav.utils.Field;
import edu.upc.fib.masd.jav.utils.FieldState;
import edu.upc.fib.masd.jav.utils.Material;
import sml.IntElement;
import sml.Kernel;
import sml.StringElement;
import sml.WMElement;

import java.util.HashMap;
import java.util.Map;

public class CollectorAgent extends VillagerAgent {
    private final Map<String, Field> fields;
    private StringElement foodPetitionWME;
    private StringElement woodPetitionWME;

    public CollectorAgent(Kernel k, String agentName, String productionsFile, BaronAgent baron, int food, int foodSatiety, int wood) {
        super(k, agentName, productionsFile, baron, food, foodSatiety, wood);
        this.fields = new HashMap<String, Field>();
    }

    public void addField(Field field) {
        fields.put(field.getId(), field);
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public void treatSpecificCommand(WMElement command) {
        String name = command.GetAttribute();
        switch (name) {
            case "cut-wood":
                cutWood();
                break;
            case "sow-field":
                String sowFieldId = command.GetValueAsString();
                sowField(sowFieldId);
                break;
            case "harvest-field":
                String harvestFieldId = command.GetValueAsString();
                harvestField(harvestFieldId);
                break;
            case "give-baron":
                String strMaterial = command.GetValueAsString();
                Material material = Enum.valueOf(Material.class, strMaterial);
                giveBaron(material);
                break;
            default:
                System.out.println("Command " + name + " not implemented");
                break;
        }
    }

    public void cutWood() {
        this.wood += 1;
        agent.Update(woodWME, this.wood);
        System.out.println("Agent " + agent.GetAgentName() + " cuts wood.");
        System.out.println("Agent " + agent.GetAgentName() + " wood: " + inputLink.GetParameterValue("wood"));
    }

    private void sowField(String fieldId) {
        fields.get(fieldId).changeState(FieldState.SOWN);
        System.out.println("Agent " + agent.GetAgentName() + " sows field " + fieldId);
    }

    private void harvestField(String fieldId) {
        this.food += fields.get(fieldId).getYield();
        agent.Update(foodWME, this.food);
        fields.get(fieldId).decreaseYield();
        fields.get(fieldId).changeState(FieldState.DRY);
        System.out.println("Agent " + agent.GetAgentName() + " harvests field " + fieldId);
        System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
    }

    private void giveBaron(Material material) {
        if (material == Material.FOOD) {
            if (this.food >= 2) {
                this.food -= 2;
                agent.Update(foodWME, this.food);
                baron.receiveFood(2);
                foodPetitionWME.DestroyWME();
            }
        }
        else if (material == Material.WOOD) {
            if (this.food >= 2) {
                this.food -= 2;
                agent.Update(woodWME, this.wood);
                baron.receiveWood(2);
                woodPetitionWME.DestroyWME();
            };
        }
    }

    public void petition(Material material) {
        if (material == Material.FOOD) {
            foodPetitionWME = inputLink.CreateStringWME("petition",material.string);
        }
        else if (material == Material.WOOD) {
            woodPetitionWME = inputLink.CreateStringWME("petition",material.string);
        }
    }

    protected void kill() {
        this.baron.deleteAssignedVillager(this);
        super.kill();
    }
}
