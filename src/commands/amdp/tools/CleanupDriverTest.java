package commands.amdp.tools;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.planning.stochastic.rtdp.BoundedRTDP;
import burlap.behavior.valuefunction.ValueFunctionInitialization;
import burlap.oomdp.auxiliary.common.GoalConditionTF;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.GoalBasedRF;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import commands.amdp.domain.*;
import commands.amdp.hardcoded.FixedDoorCleanupEnv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dilip Arumugam on 5/4/16.
 */
public class CleanupDriverTest {

    public static void main(String[] args) {
        double lockProb = 0.0;

        CleanupWorld dgen = new CleanupWorld();
        dgen.includeDirectionAttribute(true);
        dgen.includePullAction(false);
        dgen.includeWallPF_s(true);
        dgen.includeLockableDoors(true);
        dgen.setLockProbability(lockProb);
        Domain domain = dgen.generateDomain();

        State s = CleanupWorld.getExperimentState(domain);

        List<PropositionalFunction> pfs = new ArrayList<>();

        pfs.add(domain.getPropFunction(CleanupWorld.PF_BLOCK_IN_ROOM));
        pfs.add(domain.getPropFunction(CleanupWorld.PF_AGENT_IN_ROOM));

        StringBuilder sb = new StringBuilder();
        pfs.stream().map(PropositionalFunction::getName).forEach(n -> sb.append(n).append(" "));
        String name = sb.toString().trim();
        List<String> paramClasses = new ArrayList<>();
        pfs.stream().map(PropositionalFunction::getParameterClasses).forEach(p -> Arrays.stream(p).forEach(paramClasses::add));
        String[] parameterClasses = paramClasses.toArray(new String[paramClasses.size()]);


        GroundedProp groundedProp = new GroundedProp(new JointPF(name, domain, parameterClasses, pfs), new String[]{"block0", "room1", "agent0", "room0"});


//        GroundedProp groundedProp = new GroundedProp(domain.getPropFunction(CleanupWorld.PF_BLOCK_IN_ROOM),  new String[]{"block0", "room2"});


//        StateConditionTest sc = new CleanupL1AMDPDomain.GroundedPropSC(groundedProp);
        StateConditionTest sc = new CleanupL2AMDPDomain.GroundedPropSC(groundedProp);
        RewardFunction heuristicRF = new PullCostGoalRF(sc, 1., 0.);


        CleanupL2AMDPDomain.GroundedPropSC l0sc = new CleanupL2AMDPDomain.GroundedPropSC(groundedProp);
        GoalBasedRF l0rf = new GoalBasedRF(l0sc, 1., 0.);
        GoalConditionTF l0tf = new GoalConditionTF(l0sc);

        FixedDoorCleanupEnv env = new FixedDoorCleanupEnv(domain, l0rf, l0tf, s);

        long startTime = System.currentTimeMillis();

        ValueFunctionInitialization heuristic = CleanupDomainDriver.getL0Heuristic(s, heuristicRF);
        BoundedRTDP brtd = new BoundedRTDP(domain, l0rf, l0tf,0.99, new SimpleHashableStateFactory(false), new ValueFunctionInitialization.ConstantValueFunctionInitialization(0.), heuristic, 0.01, 500);
        brtd.setMaxRolloutDepth(50);
        brtd.toggleDebugPrinting(false);
        Policy P = brtd.planFromState(s);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println("total time: " + duration);
        EpisodeAnalysis ea = P.evaluateBehavior(env);

        System.out.println(ea.stateSequence);
        System.out.println(ea.actionSequence);
        String prefix = "data/amdpData/L2L0/ea/";
        ea.writeToFile(prefix + "move|bag|to|the|green|room|then|go|to|the|red|room" + "0");

        //		System.out.println(ea.getState(0).toString());
        System.out.println("total actions:" + ea.actionSequence.size());

    }
}
