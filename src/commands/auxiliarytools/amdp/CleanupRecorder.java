package commands.auxiliarytools.amdp;

import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.common.NullRewardFunction;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.shell.EnvironmentShell;
import commands.amdp.domain.CleanupL1AMDPDomain;
import commands.amdp.domain.CleanupL2AMDPDomain;
import commands.amdp.domain.CleanupWorld;

/**
 * CleanupRecorder.java
 *
 * When run:
 *  "obs" Prints out the current state
 *  "lsa" Lists all available actions in the current state
 *  "rec -b -a" Starts episode recording and automatically starts recording the next episode when the environment is reset
 *  "reset" Resets the current environment
 *  "rec -w (path) (fileName)" Writes all recorded episodes to the indicated directory path with file name
 *      "fileName_i.episode" where i refers to the ith episode
 *  "cmds" Lists all commands
 *  "ex (action) (*args)" Executes the indicated action with the specified arguments
 *
 * Created by Sidd Karamcheti on 4/21/16.
 */
public class CleanupRecorder {

    public static void main(String[] args) {

        CleanupWorld cw = new CleanupWorld();
        cw.includeDirectionAttribute(true);
        cw.includePullAction(true);
        cw.includeWallPF_s(true);
        cw.includeLockableDoors(true);
        cw.setLockProbability(0.5);
        Domain domain = cw.generateDomain();

        State cleanupInitial = CleanupWorld.getExperimentState(domain);

//        SimulatedEnvironment simulatedEnvironment = new SimulatedEnvironment(domain, new NullRewardFunction(), new NullTermination(), cleanupInitial);
//        EnvironmentShell environmentShell = new EnvironmentShell(domain, simulatedEnvironment, System.in, System.out);
//        environmentShell.start();

        CleanupL1AMDPDomain cleanupWorldL1 = new CleanupL1AMDPDomain(domain);
        cleanupWorldL1.setLockableDoors(false);
        Domain domainL1 = cleanupWorldL1.generateDomain();


        State l1Initial = CleanupL1AMDPDomain.projectToAMDPState(cleanupInitial, domainL1);

        SimulatedEnvironment simulatedEnvironment = new SimulatedEnvironment(domainL1, new NullRewardFunction(), new NullTermination(), l1Initial);
        EnvironmentShell environmentShell = new EnvironmentShell(domainL1, simulatedEnvironment, System.in, System.out);
        environmentShell.start();

        CleanupL2AMDPDomain cleanupWorldL2 = new CleanupL2AMDPDomain();
        Domain domainL2 = cleanupWorldL2.generateDomain();

        State l2Initial = CleanupL2AMDPDomain.projectToAMDPState(l1Initial, domainL2);

//        SimulatedEnvironment simulatedEnvironment = new SimulatedEnvironment(domainL2, new NullRewardFunction(), new NullTermination(), l2Initial);
//        EnvironmentShell environmentShell = new EnvironmentShell(domainL2, simulatedEnvironment, System.in, System.out);
//        environmentShell.start();
    }
}
