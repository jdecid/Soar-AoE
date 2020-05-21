package edu.upc.fib.masd.jav;

import java.util.Iterator;

import sml.Identifier;
import sml.IntElement;
import sml.Kernel;
import sml.WMElement;

public abstract class AoEAgent extends SoarAgent {

	private int food;
	private int foodSatiety;
	private WMElement foodWME;
	private WMElement foodSatietyWME;
	
	public AoEAgent(Kernel k, String agentName, String productionsFile) {
		super(k, agentName, productionsFile);
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

	public void initialize(int food, int foodSatiety) {
		this.food = food;
		this.foodSatiety = foodSatiety;
		
		setIntegerWME("food", food);
		setIntegerWME("food-satiety", foodSatiety);
	}
	
	public void decreaseSatiety() {
		this.foodSatiety -= 1;
		
		setIntegerWME("food-satiety", foodSatiety);
		
		System.out.println("Agent " + agent.GetAgentName() + " food-satiety: " + inputLink.GetParameterValue("food-satiety"));
	}
	
	public void eat() {
		this.food -= 1;
		this.foodSatiety += 5;
		
		setIntegerWME("food", food);
		setIntegerWME("food-satiety", foodSatiety);
		
		System.out.println("Agent " + agent.GetAgentName() + " eats.");
		System.out.println("Agent " + agent.GetAgentName() + " food: " + inputLink.GetParameterValue("food"));
		System.out.println("Agent " + agent.GetAgentName() + " food-satiety: " + inputLink.GetParameterValue("food-satiety"));
	}
	
	public void treatCommand(Identifier command){
		String name = command.GetCommandName();

		if (name.equals("eat-food"))
		{
			String eat = command.GetValueAsString();
			if (eat.equals("true")) {
				eat();
			}
			command.AddStatusComplete();
		}
		else {
			treatSpecificCommand(command);
		}
	}
	
	public abstract void treatSpecificCommand(Identifier command);

}
