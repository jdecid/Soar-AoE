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

import edu.upc.fib.masd.jav.SoarAgent;
import edu.upc.fib.masd.jav.VillagerAgent;
import edu.upc.fib.masd.jav.SoarAgent.PrintListener;
import sml.Agent;
import sml.Identifier;
import sml.Kernel;
import sml.smlUpdateEventId;
import sml.Kernel.UpdateEventInterface;

public class Environment
{
	/*
	 * We keep references to Agents.
	 */
	private final ArrayList<VillagerAgent> villagers;

	/*
	 * Create executor services to run Soar in since it blocks.
	 */
	private final ArrayList<ExecutorService> executors = new ArrayList<ExecutorService>();



	public Environment(ArrayList<VillagerAgent> villagers)
	{
		this.villagers = villagers;
		
		for(int i=0; i<this.villagers.size(); ++i) {
			this.executors.add(Executors.newSingleThreadExecutor());
			this.villagers.get(i).setPrintListener(new PrintListener()
			{
				public void printEvent(String message)
				{
					/*
					 * Clearly distinguish output from the agent. In a GUI this
					 * would go to its own text box. Reprint the prompt since it
					 * is likely clobbered.
					 */
					System.out.print(String.format("%nMessage: %s%n", message));
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
			for(int i=0; i<this.villagers.size(); ++i) {
				this.executors.get(i).execute(this.villagers.get(i));
				
				// Init agents
				this.villagers.get(i).initialize(5, 15);
			}

			// Loop
			while (true)
			{
				String destination = "";
				String message = "";
				
				// Update environment state
				for(int i=0; i<this.villagers.size(); ++i) {
					// Decrease food-satiety
					this.villagers.get(i).decreaseSatiety();
				}
				
				// Get agents outputs
				for(int i=0; i<this.villagers.size(); ++i) {
					System.out.println("Villager " + this.villagers.get(i).getAgent().GetAgentName());
					System.out.println("===> Food: " + this.villagers.get(i).getFood());
					System.out.println("===> Satiety: " + this.villagers.get(i).getFoodSatiety());
					System.out.println("===> Message: " + this.villagers.get(i).getOutputMessage());
				}

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
			for(int i=0; i<this.villagers.size(); ++i) {
				this.villagers.get(i).shutdown();
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
		ArrayList<VillagerAgent> agentsArray = new ArrayList<VillagerAgent>();
		
		for(int i=0; i<2; ++i) {
			agentsArray.add(new VillagerAgent(k, "Agent_" + i, "SOAR_Codes/general-agent-AoE.soar"));
		}

		/*
		 * Create the Soar environment and add the agents.
		 */
		new Environment(agentsArray);
	}
}

