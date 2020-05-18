package edu.upc.fib.masd.jav;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.upc.fib.masd.jav.AoeAgent;
import edu.upc.fib.masd.jav.AoeAgent.PrintListener;
import sml.Agent;
import sml.Identifier;
import sml.Kernel;
import sml.smlUpdateEventId;
import sml.Kernel.UpdateEventInterface;

public class Environment
{
	/*
	 * We keep a reference to the Soar interface.
	 */
	private final ArrayList<AoeAgent> agents;

	/*
	 * Create executor services to run Soar in since it blocks.
	 */
	private final ArrayList<ExecutorService> executors = new ArrayList<ExecutorService>();



	public Environment(ArrayList<AoeAgent> agents)
	{
		this.agents = agents;
		
		for(int i=0; i<this.agents.size(); ++i) {
			this.executors.add(Executors.newSingleThreadExecutor());
			this.agents.get(i).setPrintListener(new PrintListener()
			{
				public void printEvent(String message)
				{
					/*
					 * Clearly distinguish output from the agent. In a GUI this
					 * would go to its own text box. Reprint the prompt since it
					 * is likely clobbered.
					 */
					System.out.print(String.format("%nAgent %s: %s%n", agents.get(0).getAgent().GetAgentName(), message));
				}
			});
		}

		/*
		 * Start the input loop. Separated in to its own function call for
		 * clarity.
		 */
		run();
	}

	
	private void run()
	{
		try
		{
			// Initialize
			for(int i=0; i<this.agents.size(); ++i) {
				this.executors.get(i).execute(this.agents.get(i));
				
				// Init agents
				this.agents.get(i).put("^food 5 ^food-satiety 15");
			}

			// Loop
			while (true)
			{
				String destination = "";
				String message = "";
				
				// Update environment state
				for(int i=0; i<this.agents.size(); ++i) {
					// Decrease food-satiety
					//this.agents.get(i).put("");
				}
				
				
				// Communicate agents



				/*
				 * Necessary delay. (milliseconds)
				 */
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		finally
		{
			/*
			 * Shutdown the Soar interface and the executor service.
			 */
			for(int i=0; i<this.agents.size(); ++i) {
				this.agents.get(i).shutdown();
				this.executors.get(i).shutdown();
			}
		}
	}
	
	public static void main(String[] args)
	{

		/*
		 * Here creates the kernel
		 */

		Kernel k = Kernel.CreateKernelInNewThread();
		if (k.HadError())
		{
			System.err.println("Error creating kernel: "
					+ k.GetLastErrorDescription());
			System.exit(1);
		}


		/*
		 * Create all the agents and load productions
		 */
		ArrayList<AoeAgent> agentsArray = new ArrayList<AoeAgent>(2);
		
		for(int i=0; i<agentsArray.size(); ++i) {
			agentsArray.set(i, new AoeAgent(k, "SOAR_Codes/general-agent-AoE.soar", "Agent_" + i));
		}

		/*
		 * Create the Soar environment and add the agents.
		 */
		new Environment(agentsArray);
	}
}

