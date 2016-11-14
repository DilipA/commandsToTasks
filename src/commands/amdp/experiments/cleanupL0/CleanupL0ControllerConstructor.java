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

    public static final String NEWAMTDATASET = "data/amdpDataNoIRL/L0";

    public static final String L1CROSSDATASET = "data/amdpData/L1L0/trajectory";

    public static final String L2CROSSDATASET = "data/amdpData/L2L0/trajectory";

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
    public Map<String, String> getNewAMTDatasetRFLabels(){
        Map<String, String> labels = new HashMap<>();
        return labels;
    }

    @Override
    public Map<String, String> getExpertDatasetRFLabels() {
        Map<String, String> labels = new HashMap<>();

        //L0
        labels.put("go|east|for|one|step|then|south|for|one|step|then|north|for|two|steps.txt", "agentInRoom(agent0, room2)");
        labels.put("go|north|for|one|step.txt", "agentInRoom(agent0, room2)");
        labels.put("go|north|for|one|step|then|go|south|for|two|steps.txt", "agentInRoom(agent0, room2)");
        labels.put("go|north|for|one|step|then|west|for|one|step.txt", "agentInRoom(agent0, room2)");
        labels.put("go|south|for|four|steps|then|west|for|four|steps.txt", "agentInRoom(agent0, room0)");
        labels.put("go|south|for|one|step|then|east|for|one|step.txt", "agentInRoom(agent0, room2)");
        labels.put("go|south|for|three|steps|then|go|east|for|one|step.txt", "agentInRoom(agent0, room0)");
        labels.put("go|south|for|three|steps|then|go|north|for|one|step.txt", "agentInRoom(agent0, room0)");
        labels.put("go|south|for|three|steps|then|go|west|for|one|step.txt", "agentInRoom(agent0, room0)");
        labels.put("go|west|for|one|step.txt", "agentInRoom(agent0, room2)");

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

        labels.put("walk|east|one|step|then|go|south|one|step|then|go|north|two|steps.txt", "agentInRoom(agent0, room2)");
        labels.put("walk|north|one|step.txt", "agentInRoom(agent0, room2)");
        labels.put("walk|north|one|step|then|go|south|one|step.txt", "agentInRoom(agent0, room2)");
        labels.put("walk|north|one|step|then|go|west|one|step.txt", "agentInRoom(agent0, room2)");
        labels.put("walk|south|four|steps|then|go|west|four|steps.txt", "agentInRoom(agent0, room0)");
        labels.put("walk|south|one|step|then|go|east|one|step.txt", "agentInRoom(agent0, room2)");
        labels.put("walk|south|three|steps|then|go|east|one|step.txt", "agentInRoom(agent0, room0)");
        labels.put("walk|south|three|steps|then|go|north|one|step.txt", "agentInRoom(agent0, room0)");
        labels.put("walk|south|three|steps|then|go|west|one|step.txt", "agentInRoom(agent0, room0)");
        labels.put("walk|west|one|step.txt", "agentInRoom(agent0, room2)");

        //L1
        labels.put("go|to|red|door|go|into|red|room.txt", "agentInRoom(agent0, room0)");
        labels.put("go|to|red|door|go|into|red|room|go|to|green|door.txt", "agentInDoor(agent0, door1)");
        labels.put("go|to|red|door|go|into|red|room|go|to|green|door|go|into|green|room.txt", "agentInRoom(agent0, room1)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|green|door|go|to|red|door.txt", "agentInDoor(agent0, door0)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|green|door|move|to|green|door|move|bag|to|green|room.txt", "blockInRoom(block0, room1)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|red|door.txt", "blockInDoor(block0, door0)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|red|door|go|to|green|door.txt", "agentInDoor(agent0, door0) blockInDoor(block0, door0)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|red|door|move|to|red|door|move|bag|to|red|room.txt", "blockInRoom(block0, room0)");
        labels.put("go|to|red|door|go|to|blue|room.txt", "agentInRoom(agent0, room2)");
        labels.put("go|to|red|door|go|to|red|room|move|bag|to|green|door.txt", "blockInDoor(block0, door1)");

        labels.put("move|through|red|door|to|the|blue|room.txt", "agentInRoom(agent0, room2)");
        labels.put("move|through|red|door|to|the|red|room.txt", "agentInRoom(agent0, room0)");
        labels.put("move|through|red|door|to|the|red|room|then|carry|bag|through|green|door|then|move|through|green|door|then|carry|bag|to|green|room.txt",
                "blockInRoom(block0, room1)");
        labels.put("move|through|red|door|to|the|red|room|then|carry|bag|through|red|door|then|move|through|red|door|then|carry|bag|to|red|room.txt",
                "blockInRoom(block0, room0)");
        labels.put("move|through|red|door|to|the|red|room|then|carry|the|bag|through|green|door.txt", "blockInDoor(block0, door1)");
        labels.put("move|through|red|door|to|the|red|room|then|carry|the|bag|through|green|door|then|move|through|red|door.txt",
                "agentInDoor(agent0, door0) blockInDoor(block0, door1)");
        labels.put("move|through|red|door|to|the|red|room|then|carry|the|bag|through|red|door.txt",
                "blockInDoor(block0, door0)");
        labels.put("move|through|red|door|to|the|red|room|then|carry|the|bag|through|red|door|then|move|through|green|door.txt",
                "agentInDoor(agent0, door1) blockInDoor(block0, door0)");
        labels.put("move|through|red|door|to|the|red|room|then|move|through|green|door.txt",
                "agentInDoor(agent0, door1)");
        labels.put("move|through|red|door|to|the|red|room|then|move|through|green|door|to|the|green|room.txt",
                "agentInRoom(agent0, room1)");

        labels.put("walk|to|the|red|door|then|walk|into|the|blue|room.txt", "agentInRoom(agent0, room2)");
        labels.put("walk|to|the|red|door|then|walk|into|the|red|room.txt", "agentInRoom(agent0, room0)");
        labels.put("walk|to|the|red|door|then|walk|into|the|red|room|and|then|take|the|bag|to|the|green|door.txt",
                "blockInDoor(block0, door1)");
        labels.put("walk|to|the|red|door|then|walk|into|the|red|room|and|then|take|the|bag|to|the|red|door.txt",
                "blockInDoor(block0, door0)");
        labels.put("walk|to|the|red|door|then|walk|into|the|red|room|and|then|walk|to|the|green|door.txt",
                "agentInDoor(agent0, door1)");
        labels.put("walk|to|the|red|door|then|walk|into|the|red|room|then|take|the|bag|to|the|green|door|and|then|walk|to|the|red|door.txt",
                "agentInDoor(agent0, door0) blockInDoor(block0, door1)");
        labels.put("walk|to|the|red|door|then|walk|into|the|red|room|then|take|the|bag|to|the|red|door|and|then|walk|to|the|green|door.txt",
                "agentInDoor(agent0, door1) blockInDoor(block0, door0)");
        labels.put("walk|to|the|red|door|then|walk|into|the|red|room|then|walk|to|the|green|door|and|then|walk|to|the|green|room.txt",
                "agentInRoom(agent0, room1)");

        //L2
        labels.put("carry|the|bag|to|the|blue|room.txt", "blockInRoom(block0, room2)");
        labels.put("carry|the|bag|to|the|blue|room|then|walk|to|the|green|room.txt",
                "agentInRoom(agent0, room1) blockInRoom(block0, room2)");
        labels.put("carry|the|bag|to|the|blue|room|then|walk|to|the|red|room.txt",
                "agentInRoom(agent0, room0) blockInRoom(block0, room2)");
        labels.put("carry|the|bag|to|the|green|room.txt",
                "blockInRoom(block0, room1)");
        labels.put("carry|the|bag|to|the|green|room|then|carry|the|bag|to|the|blue|room.txt",
                "blockInRoom(block0, room2)");
        labels.put("carry|the|bag|to|the|green|room|then|walk|to|the|blue|room.txt",
                "agentInRoom(agent0, room2) blockInRoom(block0, room1)");
        labels.put("carry|the|bag|to|the|green|room|then|walk|to|the|green|room.txt",
                "agentInRoom(agent0, room1) blockInRoom(block0, room1)");
        labels.put("carry|the|bag|to|the|green|room|then|walk|to|the|red|room.txt",
                "agentInRoom(agent0, room0) blockInRoom(block0, room1)");

        labels.put("go|to|the|green|room.txt", "agentInRoom(agent0, room1)");
        labels.put("go|to|the|red|room.txt", "agentInRoom(agent0, room0)");
        labels.put("move|bag|to|blue|room.txt", "blockInRoom(block0, room2)");
        labels.put("move|bag|to|blue|room|and|then|go|to|the|green|room.txt",
                "agentInRoom(agent0, room1) blockInRoom(block0, room2)");
        labels.put("move|bag|to|blue|room|and|then|go|to|the|red|room.txt",
                "agentInRoom(agent0, room0) blockInRoom(block0, room2)");
        labels.put("move|bag|to|green|room.txt", "blockInRoom(block0, room1)");
        labels.put("move|bag|to|green|room|and|then|go|to|the|blue|room.txt",
                "agentInRoom(agent0, room2) blockInRoom(block0, room1)");
        labels.put("move|bag|to|green|room|then|move|bag|to|blue|room.txt",
                "blockInRoom(block0, room2)");
        labels.put("move|bag|to|the|green|room|then|go|to|the|green|room.txt",
                "agentInRoom(agent0, room1) blockInRoom(block0, room1)");
        labels.put("move|bag|to|the|green|room|then|go|to|the|red|room.txt",
                "agentInRoom(agent0, room0) blockInRoom(block0, room1)");

        labels.put("move|to|green|room.txt", "agentInRoom(agent0, room1)");
        labels.put("move|to|red|room.txt", "agentInRoom(agent0, room0)");
        labels.put("take|bag|to|blue|room.txt", "blockInRoom(block0, room2)");
        labels.put("take|bag|to|blue|room|then|move|to|green|room.txt",
                "agentInRoom(agent0, room1) blockInRoom(block0, room2)");
        labels.put("take|bag|to|blue|room|then|move|to|red|room.txt",
                "agentInRoom(agent0, room0) blockInRoom(block0, room2)");
        labels.put("take|bag|to|green|room.txt", "blockInRoom(block0, room1)");
        labels.put("take|bag|to|green|room|then|move|to|blue|room.txt",
                "agentInRoom(agent0, room2) blockInRoom(block0, room1)");
        labels.put("take|bag|to|green|room|then|move|to|green|room.txt",
                "agentInRoom(agent0, room1) blockInRoom(block0, room1)");
        labels.put("take|bag|to|green|room|then|move|to|red|room.txt",
                "agentInRoom(agent0, room0) blockInRoom(block0, room1)");
        labels.put("take|bag|to|green|room|then|take|bag|to|blue|room.txt",
                "blockInRoom(block0, room2)");
        labels.put("walk|to|the|green|room.txt", "agentInRoom(agent0, room1)");
        labels.put("walk|to|the|red|room.txt", "agentInRoom(agent0, room0)");

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
