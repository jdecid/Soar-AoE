package edu.upc.fib.masd.jav;

import sml.Kernel;
import sml.IntElement;
import sml.WMElement;

public abstract class GeneralAgent extends SoarAgent {
    protected int food;
    protected IntElement foodWME;
    protected int foodSatiety;
    protected IntElement foodSatietyWME;
    protected int wood;
    protected IntElement woodWME;

    protected final GUI gui = GUI.getInstance();

    public GeneralAgent(Kernel k, String agentName, String productionsFile, int food, int foodSatiety, int wood) {
        super(k, agentName, productionsFile);
        this.food = food;
        this.foodSatiety = foodSatiety;
        this.wood = wood;
        foodWME = inputLink.CreateIntWME("food", food);
        foodSatietyWME = inputLink.CreateIntWME("food-satiety", foodSatiety);
        woodWME = inputLink.CreateIntWME("wood", wood);
    }

    public void decreaseSatiety() {
        foodSatiety -= 1;
        agent.Update(foodSatietyWME, foodSatiety);
        if (foodSatiety < 0) {
            kill();
        }
        System.out.println("Agent " + agent.GetAgentName() + " food-satiety: " + inputLink.GetParameterValue("food-satiety"));
    }

    public void eat() {
        if (food > 0) {
            food -= 1;
            foodSatiety += 5;
            agent.Update(foodWME, food);
            agent.Update(foodSatietyWME, foodSatiety);

            updateInfoGUI("eats");
        } else {
            System.out.println("Agent " + agent.GetAgentName() + " doesn't have food.");
        }
    }

    public void treatCommand(WMElement command) {
        String name = command.GetAttribute();
        String val = command.GetValueAsString();

        if (name.equals("eat-food")) {
            eat();
        } else {
            treatSpecificCommand(command);
        }

        String info = "true".equals(val) ? name : name + " (" + val + ")";
        updateInfoGUI(info);
    }

    public abstract void treatSpecificCommand(WMElement command);

    protected void updateInfoGUI(String action) {
        String agentId = agent.GetAgentName();
        gui.setAgentAction(agentId, action);
        updateInfoGUI();
    }

    protected void updateInfoGUI() { //version with no action setting to handle material reception
        String agentId = agent.GetAgentName();
        gui.setAgentFood(agentId, inputLink.GetParameterValue("food"));
        gui.setAgentFoodSatiety(agentId, inputLink.GetParameterValue("food-satiety"));
        gui.setAgentWood(agentId, inputLink.GetParameterValue("wood"));
        if (this instanceof CollectorAgent) {
            gui.setAgentFields(agentId, ((CollectorAgent) this).getFields());
        }
    }
}
