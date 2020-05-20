package edu.upc.fib.masd.jav;

import sml.Kernel;

public class AoEAgent extends SoarAgent {

	private int food;
	private int foodSatiety;
	
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
		this.put("^food " + food + " ^food-satiety "+ foodSatiety);
	}
	
	public void decreaseSatiety() {
		this.foodSatiety -= 1;
		// TO-DO
		// Remove WMEs and generate new input
	}
	
	public void eat() {
		this.food -= 1;
		this.foodSatiety += 5;
		// TO-DO
		// Remove WMEs and generate new input
	}

}
