package edu.upc.fib.masd.jav;

import sml.*;
import sml.Agent.OutputEventInterface;
import sml.Agent.RunEventInterface;

public class AoEWorld implements RunEventInterface, OutputEventInterface {
    final protected String CMD_ROTATE = "rotate";
    final protected String CMD_FORWARD = "forward";


    public AoEWorld(Agent agent) {
        agent.RegisterForRunEvent(smlRunEventId.smlEVENT_BEFORE_INPUT_PHASE, this, null);
        agent.AddOutputHandler(CMD_ROTATE, this, null);
        agent.AddOutputHandler(CMD_FORWARD, this, null);
        agent.RunSelf(1, smlRunStepSize.sml_ELABORATION);
    }

    private boolean isDone() {
        return false;
    }

    private void _updateState() {
    }

    private void _updateSoar() {
    }

    private void _visualizeState() {

    }

    @Override
    public void runEventHandler(int eventID, Object data, Agent agent, int phase) {
        _updateState();
        _updateSoar();
        _visualizeState();
    }

    @Override
    public void outputEventHandler(Object data, String agentName, String attributeName, WMElement pWmeAdded) {
        final Identifier id = pWmeAdded.ConvertToIdentifier();
        if (id != null) {
            boolean good = false;
        }
    }
}