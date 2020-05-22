package edu.upc.fib.masd.jav;

import sml.Identifier;
import sml.Kernel;
import sml.WMElement;

import java.util.HashMap;
import java.util.Map;

public class BaronAgent extends GeneralAgent {
    private Map<String, CollectorAgent> collectors;
    private Map<String, BuilderAgent> builders;
    private Identifier subordinatesWME;

    public BaronAgent(Kernel k, String agentName, String productionsFile, int food, int foodSatiety) {
        super(k, agentName, productionsFile, food, foodSatiety);
        collectors = new HashMap<String, CollectorAgent>();
        builders = new HashMap<String, BuilderAgent>();

        subordinatesWME = this.inputLink.CreateIdWME("subordinates");
    }

    public void addCollector(CollectorAgent collector) {
        collectors.put(collector.getAgent().GetAgentName(), collector);

        Identifier subordinate = subordinatesWME.CreateIdWME("subordinate");
        subordinate.CreateStringWME("id", collector.getAgent().GetAgentName());
        subordinate.CreateStringWME("type", "collector");
    }

    public void addBuilder(BuilderAgent builder) {
        builders.put(builder.getAgent().GetAgentName(), builder);
    }

    public void treatSpecificCommand(WMElement command) {
        String name = command.GetAttribute();
        if (name.equals("demand-food")) {

        } else if (name.equals("demand-wood")) {

        } else if (name.equals("bestow-food")) {

        } else if (name.equals("bestow-wood")) {

        } else if (name.equals("demand-change-profession")) {

        } else if (name.equals("demand-build-house")) {

        } else {
            System.out.println("Command " + name + " not implemented");
        }
    }

}
