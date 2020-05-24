package edu.upc.fib.masd.jav.utils;

import edu.upc.fib.masd.jav.CollectorAgent;
import edu.upc.fib.masd.jav.Environment;
import sml.Identifier;
import sml.IntElement;
import sml.StringElement;

public class Field {
    private CollectorAgent agent;
    private Identifier fieldRootWME;
    private String id;
    private StringElement idWME;
    private FieldState state;
    private StringElement stateWME;
    private int yield;
    private IntElement yieldWME;
    private int sownRounds;
    private int rounds;

    public Field(CollectorAgent agent, Identifier fieldRootWME, String id, FieldState state, int yield) {
        super();
        this.agent = agent;
        this.fieldRootWME = fieldRootWME;
        Identifier field = this.fieldRootWME.CreateIdWME("field");
        this.id = id;
        idWME = field.CreateStringWME("id", id);
        this.state = state;
        stateWME = field.CreateStringWME("state", state.string);
        this.yield = yield;
        yieldWME = field.CreateIntWME("yield", yield);
        this.sownRounds = 0;
        this.rounds = 0;
    }

    public String getId() {
        return id;
    }

    public FieldState getState() {
        return state;
    }

    public int getYield() {
        return yield;
    }

    public void increaseYield() {
        this.yield += 1;
        agent.getAgent().Update(yieldWME, this.yield);
    }

    public void decreaseYield() {
        if (this.yield > Environment.minYield) {
            this.yield -= 1;
            agent.getAgent().Update(yieldWME, this.yield);
        }
    }

    public void changeState(FieldState state){
        this.state = state;
        agent.getAgent().Update(stateWME, state.string);
    }

    public void update() {
        if (state == FieldState.SOWN) {
            if (sownRounds < Environment.sownRounds) {
                sownRounds += 1;
            }
            else {
                changeState(FieldState.HARVESTABLE);
                sownRounds = 0;
            }
        }

        rounds += 1;
        if (rounds % Environment.increaseYieldRounds == 0) {
            increaseYield();
        }
    }
}
