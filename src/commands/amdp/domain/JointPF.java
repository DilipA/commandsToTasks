package commands.amdp.domain;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.states.State;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Dilip Arumugam on 5/5/16.
 */
public class JointPF extends PropositionalFunction {

    Collection<PropositionalFunction> pfs;

    public JointPF(String name, Domain domain, String[] parameterClasses, Collection<PropositionalFunction> pfs){
        super(name, domain, parameterClasses);
        this.pfs = pfs;
    }

    @Override
    public boolean isTrue(State state, String... strings) {
        boolean ret = true;
        int paramCounter = 0;
        for(PropositionalFunction pf : pfs){
            int numParams = pf.getParameterClasses().length;
            ret &= pf.isTrue(state, Arrays.copyOfRange(strings, paramCounter, paramCounter + numParams));
            paramCounter += numParams;
        }

        return ret;
    }
}
