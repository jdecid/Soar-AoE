package edu.upc.fib.masd.jav;
import java.util.ArrayList;
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

public class SoarAgent implements Runnable
{
	/*
	 * When Soar issues a print, it makes a call to this interface which must be
	 * registered beforehand.
	 */
	public interface PrintListener
	{
		public void printEvent(String message);
	}

	/*
	 * Cleans up the related code.
	 */
	public static final PrintListener nullListener = new PrintListener()
	{
		public void printEvent(String message)
		{
		}
	};
	
	
	private final String PRINT = "print";

	private final String CLEAR = "clear";

	private final Kernel kernel;

	private final Agent agent;

	/*
	 * We cache the root of the messages WME on the input-link so that we
	 * can quickly add additional messages as they come in.
	 */
	private final Identifier messagesRoot;

	/*
	 * We hold on to each message's WME as we add it so that we can easily
	 * remove them when a clear command is issued, without having to remove
	 * and re-add the root messages WME.
	 */
	private final List<Identifier> messages = new ArrayList<Identifier>();

	/*
	 * Here is the shared state that we must synchronize between the
	 * environment and the Soar interface. These are messages that have been
	 * entered in to standard in but not yet added to the input-link.
	 */
	private final BlockingQueue<String> lines = new LinkedBlockingQueue<String>();

	/*
	 * This variable is used to gracefully ask Soar to stop executing.
	 */
	private final AtomicBoolean stopSoar = new AtomicBoolean(true);

	/*
	 * Output from the Soar interface gets sent to this print listener so
	 * that it may be clearly distinguished from other print calls.
	 */
	private PrintListener pl = nullListener;
	
	/*
	 * Output from the Soar interface gets sent to this print listener so
	 * that it may be clearly distinguished from other print calls.
	 */
	private String outputMessage;


	public SoarAgent(Kernel k, String agentName, String productionsFile)
	{
		kernel = k;

		agent = kernel.CreateAgent(agentName);

		/*
		 * The agent, however, does return null on error. Use kernel to get
		 * diagnostic information.
		 */
		if (agent == null)
		{
			System.err.println("Error creating agent: "
					+ kernel.GetLastErrorDescription());
			System.exit(1);
		}

		/*
		 * Load the productions.
		 */
		if (!agent.LoadProductions(productionsFile)) {
			System.err.println("Can't load " + productionsFile + " file.");
			System.exit(1);
		}


		/*
		 * Create and cache the root messages WME on the input-link so that
		 * we can quickly add messages later.
		 */
		messagesRoot = agent.GetInputLink().CreateIdWME("messages");

		/*
		 * Register for update event that fires after our agent passes its
		 * output phase. Our update handler will post new messages on the
		 * input-link and read any commands off of the output link.
		 */
		kernel.RegisterForUpdateEvent(
				smlUpdateEventId.smlEVENT_AFTER_ALL_OUTPUT_PHASES,
				new UpdateEventInterface()
				{
					public void updateEventHandler(int eventID,
							Object data, Kernel kernel, int runFlags)
					{
						/*
						 * Pull each line out of the queue and post it on
						 * the input-link.
						 */
						for (String line = lines.poll(); line != null; line = lines
								.poll())
						{
							/*
							 * Each message has its own message WME off of
							 * the messages root.
							 */
							Identifier message = messagesRoot
									.CreateIdWME("message");
							/*
							 * On each message WME there are two attribute
							 * value pairs: one is the integer id number of
							 * the message and the other is its string
							 * content.
							 */
							message.CreateIntWME("id", messages.size());
							message.CreateStringWME("content", line);
							/*
							 * Store the identifier for easy clearing later.
							 */
							messages.add(message);
						}

						/*
						 * Iterate through the commands on the output link.
						 */
						for (int index = 0; index < agent
								.GetNumberCommands(); ++index)
						{
							/*
							 * Get the command by index. Note: avoid storing
							 * this identifier because the agent created it
							 * and may delete it at any time. If you need to
							 * store it across decision cycles, you'll need
							 * to do a lot more work to make sure it is
							 * valid before attempting to use it in future
							 * updates.
							 */
							Identifier command = agent.GetCommand(index);

							/*
							 * This is the attribute of the command
							 * identifier's WME.
							 */
							String name = command.GetCommandName();

							if (name.equals(PRINT))
							{
								/*
								 * Save output-link.
								 */
								outputMessage = command.GetParameterValue("content");
								command.AddStatusComplete();

							}
							else if (name.equals(CLEAR))
							{
								/*
								 * Iterate through stored message WMEs and
								 * delete them. Deleting a WME deletes all
								 * of its children (in this case the id and
								 * content WMEs).
								 */
								for (Identifier message : messages)
								{
									message.DestroyWME();
								}

								/*
								 * Clear our cache now that it has been
								 * invalidated.
								 */
								messages.clear();

								/*
								 * Issue feedback and mark status complete.
								 */
								pl.printEvent("Messages cleared.");
								command.AddStatusComplete();

							}
							else
							{
								/*
								 * Issue error feedback message and mark
								 * status error..
								 */
								//pl.printEvent("Unknown command received: " + name);
								command.AddStatusError();
							}
						}

						/*
						 * This marks any commands on the output-link as
						 * seen so they will not be encountered via
						 * GetCommand on future updates if they are still on
						 * the output-link then.
						 */
						agent.ClearOutputLinkChanges();

						/*
						 * Finally, check to see if we have been asked to
						 * stop and issue a stop if so.
						 */
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

	public void run()
	{
		pl.printEvent("Starting Soar Agent.");

		/*
		 * Reset the request to stop just before we start up.
		 */
		stopSoar.set(false);
		/*
		 * This run call blocks, hopefully you're in a separate thread or
		 * things will hang here.
		 */
		kernel.RunAllAgentsForever();
		pl.printEvent("Stopping Soar Agent.");
	}

	public void stop()
	{
		/*
		 * Politely ask the agent to stop itself during its next update
		 * event.
		 */
		stopSoar.set(true);
	}

	public void put(String line)
	{
		/*
		 * Queue the line for addition to the messages input-link. The queue
		 * handles synchronization issues.
		 */
		try
		{
			lines.put(line);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void shutdown()
	{
		/*
		 * In case things are running, make a half-hearted attempt to stop
		 * them first. This is a hack. Instead, you should be registering
		 * for kernel events that tell you when Soar starts and stops so
		 * that you know when you need to stop Soar and when it actually
		 * stops. See kernel.RegisterForSystemEvent()
		 */
		stop();
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException ignored)
		{

		}
		/*
		 * This will remove any agents and close the listener thread that
		 * listens for things like remote debugger connections.
		 */
		kernel.Shutdown();
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	public String getOutputMessage() {
		return outputMessage;
	}
	
	public void setOutputMessage(String msg) {
		this.outputMessage = msg;
	}
	
}