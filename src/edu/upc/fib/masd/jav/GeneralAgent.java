package edu.upc.fib.masd.jav;

import sml.Kernel;
import sml.IntElement;
import sml.WMElement;

public abstract class GeneralAgent extends SoarAgent {
    private int food;
    private IntElement foodWME;
    private int foodSatiety;
    private IntElement foodSatietyWME;

    public GeneralAgent(Kernel k, String agentName, String productionsFile, int food, int foodSatiety) {
        super(k, agentName, productionsFile);
        this.food = food;
        this.foodSatiety = foodSatiety;
        foodWME = inputLink.CreateIntWME("food", food);
        foodSatietyWME = inputLink.CreateIntWME("food-satiety", foodSatiety);
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getFoodSatiety() {
        return foodSatiety;
    }

    public void setFoodSatiety(int foodSatiety) {
        this.foodSatiety = foodSatiety;
    }

    public void decreaseSatiety() {
        this.foodSatiety -= 1;
        agent.Update(foodSatietyWME, foodSatiety);

        System.out.println("Agent " + agent.GetAgentName() + " food-satiety: " + inputLink.GetParameterValue("food-satiety"));
    }

    public void eat() {
        if (this.food > 0) {
            this.food -= 1;
            this.foodSatiety += 5;
            agent.Update(foodWME, food);
            agent.Update(foodSatietyWME, foodSatiety);

            System.out.println("Agent " + agent.GetAgentName() + " eats.");
            System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
            System.out.println("Agent " + agent.GetAgentName() + " food-satiety: " + inputLink.GetParameterValue("food-satiety"));
        } else {
            System.out.println("Agent " + agent.GetAgentName() + " doesn't have food.");
            // TODO: Kill
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
