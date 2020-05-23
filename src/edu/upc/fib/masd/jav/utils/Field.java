package edu.upc.fib.masd.jav.utils;

import edu.upc.fib.masd.jav.CollectorAgent;
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
    }

    public String getId() {
        return id;
    }

    public void update() {
        if (state == FieldState.SOWN) {
            if (sownRounds < 5) {
                sownRounds += 1;
            } else {
                state = FieldState.HARVESTABLE;
                sownRounds = 0;
                agent.getAgent().Update(stateWME, state.string);
            }
        }
    }
}
