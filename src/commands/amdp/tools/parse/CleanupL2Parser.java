package commands.amdp.tools.parse;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.legacy.StateParser;

/**
 * Created by Dilip Arumugam on 4/30/16.
 */
public class CleanupL2Parser implements StateParser {
    protected Domain domain;

    public CleanupL2Parser(Domain domain) {
        this.domain = domain;
    }

    @Override
    public String stateToString(State state) {
        return null;
    }

    @Override
    public State stringToState(String s) {
        return null;
    }
}
