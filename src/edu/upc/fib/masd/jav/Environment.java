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
	// We keep references to Agents.
	private final ArrayList<VillagerAgent> villagers;

	// Create executor services to run Soar in since it blocks.
	private final ArrayList<ExecutorService> executors = new ArrayList<ExecutorService>();
	
	private final BufferedReader input = new BufferedReader(
            new InputStreamReader(System.in));

	private void delay(int ms) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

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

		// Start the input loop.
		run();
	}

	
	private void run()
	{
		try
		{
			// Initialize
			for(int i=0; i<this.villagers.size(); ++i) {
				//this.executors.get(i).execute(this.villagers.get(i));
				
				// Init agents
				this.villagers.get(i).initialize(5, 15);
			}
			
			// Necessary delay (milliseconds)
			delay(1000);

			// Loop
			while (!Thread.interrupted())
			{
				System.out.println("========================");
				System.out.println("Press enter to continue (or X to exit):");
				String line = input.readLine();
				
				if (line.equalsIgnoreCase("x")) {
					shutdown();
				}
				// Update environment state
				for(int i=0; i<this.villagers.size(); ++i) {
					this.villagers.get(i).runStep();
					// Decrease food-satiety
					this.villagers.get(i).decreaseSatiety();
				}	

				// Necessary delay (milliseconds)
				delay(1000);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finally
		{
			shutdown();
		}
	}
	
	private void shutdown() {
		// Shutdown the Soar interface and the executor service.
		for(int i=0; i<this.villagers.size(); ++i) {
			this.villagers.get(i).shutdown();
			this.executors.get(i).shutdown();
		}
		System.exit(0);
	}
	
	public static void main(String[] args)
	{

		// Create the kernel
		final int kernelPort = 27314;
		Kernel k = Kernel.CreateKernelInNewThread(kernelPort);
		if (k.HadError())
		{
			System.err.println("Error creating kernel: " + k.GetLastErrorDescription());
			System.exit(1);
		}


		// Create all the agents and load productions
		ArrayList<VillagerAgent> agentsArray = new ArrayList<VillagerAgent>();
		
		for(int i=4; i<6; ++i) {
			agentsArray.add(new VillagerAgent(k, "Agent_" + i, "SOAR_Codes/general-agent-AoE.soar"));
		}
		
		agentsArray.get(0).getAgent().SpawnDebugger(kernelPort, "libs/soar/SoarJavaDebugger.jar");

		// Create the Soar environment and add the agents
		new Environment(agentsArray);
	}
}

