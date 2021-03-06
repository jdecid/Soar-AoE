package edu.upc.fib.masd.jav.agents;

import edu.upc.fib.masd.jav.Environment;
import edu.upc.fib.masd.jav.utils.Field;
import edu.upc.fib.masd.jav.utils.FieldState;
import sml.Identifier;
import sml.Kernel;
import sml.StringElement;
import sml.WMElement;

import java.util.HashMap;
import java.util.Map;

public class CollectorAgent extends VillagerAgent {
    private final Map<String, Field> fields;
    private StringElement foodPetitionWME;
    private StringElement woodPetitionWME;

    public CollectorAgent(Kernel k, String agentName, BaronAgent baron) {
        super(k, agentName, "src/main/resources/soar/PRESET_collector_agent.soar", baron);
        job = "Collector";
        this.fields = new HashMap<>();
        Identifier fieldsRoot = inputLink.CreateIdWME("fields");
        for (int i = 0; i < Environment.configuration.get("villager").get("numFields"); ++i) {
            Field field = new Field(this, fieldsRoot, String.format("Field_%d", i), FieldState.DRY,
                    Environment.configuration.get("villager").get("startFieldYield"));
            addField(field);
        }
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
                String material = command.GetValueAsString();
                giveBaron(material);
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

    public void cutWood() {
        this.wood += 1;
        this.wood = Math.min(this.wood, Environment.configuration.get("villager").get("maxWood"));
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
        this.food = Math.min(this.food, Environment.configuration.get("villager").get("maxFood"));
        agent.Update(foodWME, this.food);
        fields.get(fieldId).decreaseYield();
        fields.get(fieldId).changeState(FieldState.DRY);
        System.out.println("Agent " + agent.GetAgentName() + " harvests field " + fieldId);
        System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
    }

    private void giveBaron(String material) {
        System.out.println("Agent " + agent.GetAgentName() + " gives baron " + material);
        if ("food".equals(material)) {
            if (this.food >= Environment.configuration.get("villager").get("giveValue")) {
                this.food -= Environment.configuration.get("villager").get("giveValue");
                agent.Update(foodWME, this.food);
                baron.receive(agent.GetAgentName(), "food");
                foodPetitionWME.DestroyWME();
            } else System.out.println("Agent " + agent.GetAgentName() + " fails to fulfil, has " + this.food);
        } else if ("wood".equals(material)) {
            if (this.wood >= Environment.configuration.get("villager").get("giveValue")) {
                this.wood -= Environment.configuration.get("villager").get("giveValue");
                agent.Update(woodWME, this.wood);
                baron.receive(agent.GetAgentName(), "wood");
                woodPetitionWME.DestroyWME();
            } else System.out.println("Agent " + agent.GetAgentName() + " fails to fulfil, has " + this.wood);
        }
    }

    public void petition(String petition) {
        if (petition.equals("food")) {
            foodPetitionWME = inputLink.CreateStringWME("petition", petition);
        } else if (petition.equals("wood")) {
            woodPetitionWME = inputLink.CreateStringWME("petition", petition);
        } else {
            System.out.println("Collector received non-understood petition for " + petition);
        }
    }

    public void decreaseFieldYield(String fieldId) {
        fields.get(fieldId).decreaseYield();
    }
}
