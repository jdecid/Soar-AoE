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
		if (name.equals("cut-wood")) {
			cutWood();
		}
		else if (name.equals("sow-field")) {
			
		}
		else if (name.equals("harvest-field")) {
			
		}
		else {
			System.out.println("Command " + name + " not implemented");
		}
	}
	
	public void cutWood() {
		this.wood -= 1;
		agent.Update(woodWME, this.wood);
	}
	
}
