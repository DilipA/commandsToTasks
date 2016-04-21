package commands.amdp.framework;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.FullActionModel;
import burlap.oomdp.singleagent.GroundedAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AMDPSimpleDeterministicAction extends AMDPSimpleAction implements FullActionModel{


//	public abstract RewardFunction getRF(GroundedAction ga);
//	public abstract TerminalFunction getTF(GroundedAction ga);

	public AMDPSimpleDeterministicAction() {

	}

	public AMDPSimpleDeterministicAction(String name, Domain domain){
		super(name, domain);
	}

	@Override
	public List<GroundedAction> getAllApplicableGroundedActions(State s) {
		GroundedAction ga = new SimpleGroundedAMDPAction(this);
		return this.applicableInState(s, ga) ? Arrays.asList(ga) : new ArrayList<GroundedAction>(0);
	}

	@Override
	public List<TransitionProbability> getTransitions(State s, GroundedAction groundedAction) {
		return this.deterministicTransition(s, groundedAction);
	}
}
