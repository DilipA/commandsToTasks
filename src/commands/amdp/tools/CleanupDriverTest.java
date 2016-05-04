package commands.amdp.tools;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.planning.stochastic.rtdp.BoundedRTDP;
import burlap.behavior.valuefunction.ValueFunctionInitialization;
import burlap.oomdp.auxiliary.common.GoalConditionTF;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.GoalBasedRF;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.visualizer.Visualizer;
import commands.amdp.domain.*;
import commands.amdp.hardcoded.FixedDoorCleanupEnv;

import java.util.Arrays;

/**
 * Created by Dilip Arumugam on 5/4/16.
 */
public class CleanupDriverTest {

    public static void main(String[] args) {

        boolean runGroundLevelBoundedRTDP = true;

        if(runGroundLevelBoundedRTDP){
            double lockProb = 0.0;

            CleanupWorld dgen = new CleanupWorld();
            dgen.includeDirectionAttribute(true);
            dgen.includePullAction(false);
            dgen.includeWallPF_s(true);
            dgen.includeLockableDoors(true);
            dgen.setLockProbability(lockProb);
            Domain domain = dgen.generateDomain();

            State s = CleanupWorld.getExperimentState(domain);

            GroundedProp groundedProp = new GroundedProp(domain.getPropFunction(CleanupWorld.PF_AGENT_IN_ROOM),  new String[]{"agent0", "room0"});


            StateConditionTest sc = new CleanupL1AMDPDomain.GroundedPropSC(groundedProp);
            RewardFunction heuristicRF = new PullCostGoalRF(sc, 1., 0.);


            CleanupL1AMDPDomain.GroundedPropSC l0sc = new CleanupL1AMDPDomain.GroundedPropSC(groundedProp);
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
            String prefix = "data/amdpData/L1L0/ea/";
            ea.writeToFile(prefix + "go|to|red|door|go|into|red|room");

            //		System.out.println(ea.getState(0).toString());
            System.out.println("total actions:" + ea.actionSequence.size());
        }
        else{
            //designWorld();
            //debug();
        }
    }
}
