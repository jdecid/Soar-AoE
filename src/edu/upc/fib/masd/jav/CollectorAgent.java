package edu.upc.fib.masd.jav;

import java.util.HashMap;
import java.util.Map;
import edu.upc.fib.masd.jav.utils.Field;
import edu.upc.fib.masd.jav.utils.FieldState;
import sml.IntElement;
import sml.Kernel;
import sml.WMElement;

public class CollectorAgent extends GeneralAgent{
	private BaronAgent baron;
	private int wood;
	private IntElement woodWME;
	private Map<String, Field> fields;

	public CollectorAgent(Kernel k, String agentName, String productionsFile, BaronAgent baron, int food, int foodSatiety, int wood) {
		super(k, agentName, productionsFile, food, foodSatiety);
		this.baron = baron;
		this.wood = wood;
		this.woodWME = inputLink.CreateIntWME("wood", wood);
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
}
