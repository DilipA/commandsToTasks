package commands.amdp.experiments.cleanupL0;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.legacy.StateParser;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import commands.amdp.domain.CleanupWorld;
import commands.amdp.experiments.ControllerConstructor;
import commands.amdp.tools.parse.CleanupL0Parser;
import commands.data.TrainingElement;
import commands.data.TrainingElementParser;
import commands.model3.GPConjunction;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/30/16.
 */
public class CleanupL0ControllerConstructor implements ControllerConstructor {
    public static final String EXPERTDATASET = "data/amdpData/L0/trajectory";

    public DomainGenerator domainGenerator;
    public Domain domain;
    public HashableStateFactory hashingFactory;
    public List<GPConjunction> liftedTaskDescriptions;
    public StateParser sp;
    public StateParser cacheStateParser;

    public CleanupL0ControllerConstructor(){
        this.domainGenerator = buildDomainGenerator();
        this.domain = this.domainGenerator.generateDomain();
        this.hashingFactory = new SimpleHashableStateFactory(false);
        this.liftedTaskDescriptions = new ArrayList<>(3);

        GPConjunction atr = new GPConjunction();
        atr.addGP(new GroundedProp(domain.getPropFunction(CleanupWorld.PF_AGENT_IN_ROOM), new String[]{"a", "r"}));
        this.liftedTaskDescriptions.add(atr);

        GPConjunction btr = new GPConjunction();
        btr.addGP(new GroundedProp(domain.getPropFunction(CleanupWorld.PF_BLOCK_IN_ROOM), new String[]{"b", "r"}));
        this.liftedTaskDescriptions.add(btr);

        GPConjunction abtr = new GPConjunction();
        abtr.addGP(new GroundedProp(domain.getPropFunction(CleanupWorld.PF_AGENT_IN_ROOM), new String[]{"a", "r1"}));
        abtr.addGP(new GroundedProp(domain.getPropFunction(CleanupWorld.PF_BLOCK_IN_ROOM), new String[]{"b", "r2"}));
        this.liftedTaskDescriptions.add(abtr);

        this.sp = new CleanupL0Parser(this.domain);
        this.cacheStateParser = sp;
    }

    @Override
    public DomainGenerator buildDomainGenerator() {
        CleanupWorld cw = new CleanupWorld();
        cw.includeDirectionAttribute(true);
        cw.includePullAction(false);
        cw.includeWallPF_s(true);
        cw.includeLockableDoors(false);
        cw.setLockProbability(0.0);
        return cw;
    }

    @Override
    public WeaklySupervisedController generateNewController(){
        WeaklySupervisedController controller = new WeaklySupervisedController(this.domain, liftedTaskDescriptions, hashingFactory, true);
        return controller;
    }


    @Override
    public List<TrainingElement> getTrainingDataset(String pathToDatasetDir){
        TrainingElementParser teparser = new TrainingElementParser(this.domain, this.sp);
        List<TrainingElement> dataset = teparser.getTrainingElementDataset(pathToDatasetDir, ".txt");
        return dataset;
    }

    @Override
    public Map<String, String> getExpertDatasetRFLabels() {

        Map<String, String> labels = new HashMap<>();

        labels.put("move|four|steps|south|four|steps|west.txt", "agentInRoom(agent0, room0)");
        labels.put("move|one|step|east|one|step|south|then|two|steps|north.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|north.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|north|one|step|west.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|north|two|steps|south.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|south|one|step|east.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|west.txt", "agentInRoom(agent0, room2)");
        labels.put("move|three|steps|south|one|step|east.txt", "agentInRoom(agent0, room0)");
        labels.put("move|three|steps|south|one|step|north.txt", "agentInRoom(agent0, room0)");
        labels.put("move|three|steps|south|one|step|west.txt", "agentInRoom(agent0, room0)");

        return labels;
    }

    @Override
    public StateParser getSP() {
        return this.sp;
    }

    @Override
    public StateParser getCacheStateParser() {
        return this.cacheStateParser;
    }
}
