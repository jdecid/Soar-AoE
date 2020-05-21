package edu.upc.fib.masd.jav;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
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
	// When Soar issues a print, it makes a call to this interface which must be registered beforehand.
	public interface PrintListener
	{
		public void printEvent(String message);
	}

	public static final PrintListener nullListener = new PrintListener()
	{
		public void printEvent(String message)
		{
		}
	};

	protected final Kernel kernel;

	protected final Agent agent;

	// Input-link WME so that we can quickly add additional messages as they come in.
	protected Identifier inputLink;
	
	// Each message's WME as we add it so that we can easily remove them
	protected final Map<String, WMElement> wmes = new HashMap<String, WMElement>();

	// To ask Soar to stop executing.
	private final AtomicBoolean stopSoar = new AtomicBoolean(true);

	// Output from the Soar interface gets sent to this print listener
	private PrintListener pl = nullListener;


	public SoarAgent(Kernel k, String agentName, String productionsFile)
	{
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

		// Create and cache input-link 
		inputLink = agent.GetInputLink();

		// Event that fires after our agent passes its output phase. 
		// Post new messages on the input-link and read commands of the output link.
		kernel.RegisterForUpdateEvent(
				smlUpdateEventId.smlEVENT_AFTER_ALL_OUTPUT_PHASES,
				new UpdateEventInterface()
				{
					public void updateEventHandler(int eventID,
							Object data, Kernel kernel, int runFlags)
					{
						System.out.println("Agent " + agent.GetAgentName() + " commands received: " + agent.GetNumberCommands());
						// Iterate through the commands on the output link.
						for (int index = 0; index < agent.GetNumberCommands(); ++index)
						{
							// Get command
							Identifier command = agent.GetCommand(index);
							treatCommand(command);
						}

						// Mark commands as seen so they will not be encountered again 
						// if they are still on the output-link then.
						agent.ClearOutputLinkChanges();

						// Check if we have to stop
						if (stopSoar.get())
						{
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

	public void setPrintListener(PrintListener pl)
	{
		if (pl == null)
		{
			this.pl = nullListener;
		}
		else
		{
			this.pl = pl;
		}
	}

	public void runStep()
	{
		System.out.println("Agent " + agent.GetAgentName() + " run step");
		this.agent.RunSelf(1, smlRunStepSize.sml_UNTIL_OUTPUT);
	}

	public void stop()
	{
		// Ask the agent to stop itself during its next update event.
		stopSoar.set(true);
	}

	public void shutdown()
	{
		stop();
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException ignored)
		{

		}
		// Remove any agents and close the listener thread that
		// listens for things like remote debugger connections.
		kernel.Shutdown();
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	public void setIntegerWME(String attribute, Integer value) {
		if (wmes.containsKey(attribute)) {
			wmes.get(attribute).DestroyWME();
		}
		wmes.put(attribute, inputLink.CreateIntWME(attribute, value));
	}
	
	public void setStringWME(String attribute, String value) {
		if (wmes.containsKey(attribute)) {
			wmes.get(attribute).DestroyWME();
		}
		wmes.put(attribute, inputLink.CreateStringWME(attribute, value));
	}
	
	public void setFloatWME(String attribute, Float value) {
		if (wmes.containsKey(attribute)) {
			wmes.get(attribute).DestroyWME();
		}
		wmes.put(attribute, inputLink.CreateFloatWME(attribute, value));
	}
	
	public abstract void treatCommand(Identifier command); 
	
}