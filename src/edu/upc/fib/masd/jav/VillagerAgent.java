package edu.upc.fib.masd.jav;

import sml.Identifier;
import sml.Kernel;

public class VillagerAgent extends AoEAgent{

	public VillagerAgent(Kernel k, String agentName, String productionsFile) {
		super(k, agentName, productionsFile);
	}
	
	public void treatSpecificCommand(Identifier command) {
		String name = command.GetCommandName();
		if (name.equals("cut-wood"))
		{
			
		}
		else if (name.equals("sow-field")) {
			
		}
		else if (name.equals("harvest-field")) {
			
		}
		else
		{
			// Mark status error
			command.AddStatusError();
		}
	}
	
}
