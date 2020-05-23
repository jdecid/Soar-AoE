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

    private final GUI gui = GUI.getInstance();

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

            String agentId = agent.GetAgentName();
            gui.setAgentAction(agentId, "Eats");
            gui.setAgentFood(agentId, inputLink.GetParameterValue("food"));
            gui.setAgentFoodSatiety(agentId, inputLink.GetParameterValue("food-satiety"));
        } else {
            System.out.println("Agent " + agent.GetAgentName() + " doesn't have food.");
        }
    }

    public void treatCommand(WMElement command) {
        String name = command.GetAttribute();

        if (name.equals("eat-food")) {
            eat();
        } else {
            treatSpecificCommand(command);
        }
    }

    public abstract void treatSpecificCommand(WMElement command);

}
