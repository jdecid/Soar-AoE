package edu.upc.fib.masd.jav;

import java.util.HashMap;
import java.util.Map;
import edu.upc.fib.masd.jav.utils.Field;
import sml.IntElement;
import sml.Kernel;
import sml.WMElement;

public class CollectorAgent extends GeneralAgent{
	private BaronAgent baron;
	private int wood;
	private IntElement woodWME;
	
	private Map<String, Field> fields;

	public Map<String, Field> getFields() {
		return fields;
	}

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
	
	public void treatSpecificCommand(WMElement command) {
		String name = command.GetAttribute();
		switch (name) {
			case "cut-wood":
				cutWood();
				break;
			case "sow-field":
				sowField();
				break;
			case "harvest-field":
				// Set field again to dry
				// Increase food agent (yield)
				break;
			default:
				System.out.println("Command " + name + " not implemented");
				break;
		}
	}

	private void sowField() {
		System.out.println(1234);
	}

	public void cutWood() {
		this.wood += 1;
		agent.Update(woodWME, this.wood);
	}
	
}
