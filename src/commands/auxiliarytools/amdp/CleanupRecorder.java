package commands.auxiliarytools.amdp;

import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.common.NullRewardFunction;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.shell.EnvironmentShell;
import commands.amdp.domain.CleanupL1AMDPDomain;
import commands.amdp.domain.CleanupWorld;

/**
 * CleanupRecorder.java
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
        CleanupL1AMDPDomain cleanupWorldL1 = new CleanupL1AMDPDomain(domain);
        cleanupWorldL1.setLockableDoors(false);
        Domain domainL1 = cleanupWorldL1.generateDomain();

        State cleanupInitial = CleanupWorld.getClassicState(domain);

        State l1Initial = CleanupL1AMDPDomain.projectToAMDPState(cleanupInitial, domain);
        SimulatedEnvironment simulatedEnvironment = new SimulatedEnvironment(domainL1, new NullRewardFunction(), new NullTermination(), l1Initial);
        EnvironmentShell environmentShell = new EnvironmentShell(domain, simulatedEnvironment, System.in, System.out);
        environmentShell.start();
    }
}
