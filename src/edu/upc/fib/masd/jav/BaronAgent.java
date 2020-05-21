package edu.upc.fib.masd.jav;

import sml.Identifier;
import sml.Kernel;

public class BaronAgent extends AoEAgent {

	public BaronAgent(Kernel k, String agentName, String productionsFile) {
		super(k, agentName, productionsFile);
	}
	
	public void treatSpecificCommand(Identifier command) {
		String name = command.GetCommandName();
		if (name.equals("demand-food"))
		{
			
		}
		else if (name.equals("demand-wood")) {
			
		}
		else if (name.equals("bestow-food")) {
			
		}
		else if (name.equals("bestow-wood")) {
			
		}
		else if (name.equals("demand-change-profession")) {
			
		}
		else if (name.equals("demand-build-house")) {
			
		}
		else
		{
			// Mark status error
			command.AddStatusError();
		}
	}

}
