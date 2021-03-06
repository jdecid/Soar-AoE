package edu.upc.fib.masd.jav.agents;

import sml.*;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SoarAgent {
    protected final Kernel kernel;

    protected final Agent agent;

    // Input-link WME so that we can quickly add additional messages as they come in.
    protected Identifier inputLink;

    // Output-link WME
    protected Identifier outputLink;

    // To ask Soar to stop executing.
    private final AtomicBoolean stopSoar = new AtomicBoolean(true);

    protected String actionsThisTurn;


    public SoarAgent(Kernel k, String agentName, String productionsFile) {
        kernel = k;
        agent = kernel.CreateAgent(agentName);

        if (agent == null) {
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
    }

    protected void kill() {
        kernel.DestroyAgent(agent);
    }

    public void runStep() {
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
        } catch (InterruptedException ignored) {
        }
        kernel.Shutdown();
    }

    public Agent getAgent() {
        return agent;
    }

    public void readAndTreatOutput() {
        actionsThisTurn = "";
        // Iterate through the commands on the output link.
        if (agent.GetOutputLink() != null) {
            for (int index = 0; index < agent.GetOutputLink().GetNumberChildren(); ++index) {
                // Get command
                WMElement command = agent.GetOutputLink().GetChild(index);
                treatCommand(command);
            }
        }
        checkFlags();
    }

    public abstract void treatCommand(WMElement command);

    protected abstract void checkFlags();

    public void clearOutput() {
        WMElement wme = inputLink.CreateStringWME("clear", "output");
        this.agent.RunSelf(1);
        wme.DestroyWME();
    }

    protected void exciseAgent() { //deletes all information inside the agent: productions and inputs
        agent.ExecuteCommandLine("excise --all");
        int nInputs = inputLink.GetNumberChildren();
        WMElement wme;
        for (int i=nInputs-1; i >= 0; i--) {
            wme = inputLink.GetChild(i);
            wme.DestroyWME();
            //no need to destroy each children, as they will become disconnected from state
        }
    }
}
