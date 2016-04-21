package commands.auxiliarytools.amdp;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.common.NullRewardFunction;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.shell.EnvironmentShell;

/**
 * Created by Dilip Arumugam on 4/21/16.
 */
public class ShellTest {

    public static void main(String[] args) {
        GridWorldDomain gridWorldDomain = new GridWorldDomain(11,11);

        Domain domain = gridWorldDomain.generateDomain();
        State s = GridWorldDomain.getOneAgentNoLocationState(domain, 0,0);
        SimulatedEnvironment simulatedEnvironment = new SimulatedEnvironment(domain, new NullRewardFunction(), new NullTermination(), s);
        EnvironmentShell environmentShell = new EnvironmentShell(domain, simulatedEnvironment, System.in, System.out);
        environmentShell.start();
    }
}
