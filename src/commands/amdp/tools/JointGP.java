package commands.amdp.tools;

import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.states.State;

/**
 * Created by Dilip Arumugam on 5/4/16.
 */
public class JointGP extends GroundedProp {

    public JointGP(PropositionalFunction p, String[] par) {
        this.pf = p;
        this.params = par;
    }

    @Override
    public boolean isTrue(State s){

    }

}
