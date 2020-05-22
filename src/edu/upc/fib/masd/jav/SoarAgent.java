package edu.upc.fib.masd.jav;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import sml.Agent;
import sml.Identifier;
import sml.Kernel;
import sml.smlUpdateEventId;
import sml.Kernel.UpdateEventInterface;
import sml.WMElement;
import sml.smlRunStepSize;

public abstract class SoarAgent
{
	protected final Kernel kernel;

	protected final Agent agent;

	// Input-link WME so that we can quickly add additional messages as they come in.
	protected Identifier inputLink;
	
	// Output-link WME
	protected Identifier outputLink;
	
	// To ask Soar to stop executing.
	private final AtomicBoolean stopSoar = new AtomicBoolean(true);


	public SoarAgent(Kernel k, String agentName, String productionsFile) {
		kernel = k;
		agent = kernel.CreateAgent(agentName);
		
		if (agent == null)
		{
			System.err.println("Error creating agent: " + kernel.GetLastErrorDescription());
			System.exit(1);
		}

		// Load the productions.
		if (!agent.LoadProductions(productionsFile)) {
			System.err.println("Can't load " + productionsFile + " file.");
			System.exit(1);
		}

		// Create and cache input-link and output-link
		inputLink = agent.GetInputLink();
		outputLink = agent.GetOutputLink();

		// Event that fires after our agent passes its output phase. 
		// Post new messages on the input-link and read commands of the output link.
		kernel.RegisterForUpdateEvent(
				smlUpdateEventId.smlEVENT_AFTER_ALL_OUTPUT_PHASES,
				new UpdateEventInterface()
				{
					public void updateEventHandler(int eventID,
							Object data, Kernel kernel, int runFlags)
					{
						System.out.println("Agent " + agent.GetAgentName() + " commands received: " + outputLink.GetNumberChildren());

						// Iterate through the commands on the output link.
						for (int index = 0; index < outputLink.GetNumberChildren(); ++index)
						{
							// Get command
							WMElement command = outputLink.GetChild(index);
							treatCommand(command);
						}

						// Mark commands as seen so they will not be encountered again 
						// if they are still on the output-link then.
						agent.ClearOutputLinkChanges();

						// Check if we have to stop
						if (stopSoar.get()) {
							kernel.StopAllAgents();
						}
					}
				}, null);
		/*
		 * That final null parameter above is for user data. Anything passed
		 * there will appear in the updateEventHandler's Object data
		 * parameter.
		 */
	}
	
	public void runStep() {
		System.out.println("Agent " + agent.GetAgentName() + " run step");
		this.agent.RunSelf(1, smlRunStepSize.sml_UNTIL_OUTPUT);
	}

	public void stop() {
		// Ask the agent to stop itself during its next update event.
		stopSoar.set(true);
	}

	public void shutdown() {
		stop();
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException ignored){}
		kernel.Shutdown();
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	public abstract void treatCommand(WMElement command); 
	
}