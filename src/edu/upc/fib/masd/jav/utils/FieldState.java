package edu.upc.fib.masd.jav.utils;

public enum FieldState {
	DRY("dry"),
	SOWN("sown"),
	HARVESTABLE("harvestable");
	
	public final String string;

	FieldState(String string) {
		this.string = string;
	}
}
