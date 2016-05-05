package commands.amdp.experiments.cleanupL2;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.legacy.StateParser;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import commands.amdp.domain.CleanupL1AMDPDomain;
import commands.amdp.domain.CleanupL2AMDPDomain;
import commands.amdp.experiments.ControllerConstructor;
import commands.amdp.tools.parse.CleanupL2Parser;
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
public class CleanupL2ControllerConstructor implements ControllerConstructor {
    public static final String EXPERTDATASET = "data/amdpData/L2/trajectory";

    public static final String L0CROSSDATASET = "data/amdpData/L0L2/trajectory";

    public static final String L1CROSSDATASET = "data/amdpData/L1L2/trajectory";

    public DomainGenerator domainGenerator;
    public Domain domain;
    public HashableStateFactory hashingFactory;
    public List<GPConjunction> liftedTaskDescriptions;
    public StateParser sp;
    public StateParser cacheStateParser;

    public CleanupL2ControllerConstructor(){
        this.domainGenerator = buildDomainGenerator();
        this.domain = this.domainGenerator.generateDomain();
        this.hashingFactory = new SimpleHashableStateFactory(false);
        this.liftedTaskDescriptions = new ArrayList<>(3);

        GPConjunction atr = new GPConjunction();
        atr.addGP(new GroundedProp(domain.getPropFunction(CleanupL1AMDPDomain.PF_AGENT_IN_REGION), new String[]{"a", "r"}));
        this.liftedTaskDescriptions.add(atr);

        GPConjunction btr = new GPConjunction();
        btr.addGP(new GroundedProp(domain.getPropFunction(CleanupL1AMDPDomain.PF_AGENT_IN_REGION), new String[]{"b", "r"}));
        this.liftedTaskDescriptions.add(btr);

        GPConjunction abtr = new GPConjunction();
        abtr.addGP(new GroundedProp(domain.getPropFunction(CleanupL1AMDPDomain.PF_AGENT_IN_REGION), new String[]{"a", "r1"}));
        abtr.addGP(new GroundedProp(domain.getPropFunction(CleanupL1AMDPDomain.PF_AGENT_IN_REGION), new String[]{"b", "r2"}));
        this.liftedTaskDescriptions.add(abtr);

        this.sp = new CleanupL2Parser(this.domain);
        this.cacheStateParser = sp;
    }

    @Override
    public DomainGenerator buildDomainGenerator() {
        return new CleanupL2AMDPDomain();
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
    public StateParser getSP() {
        return this.sp;
    }

    @Override
    public StateParser getCacheStateParser() {
        return this.cacheStateParser;
    }

//    @Override
//    public Map<String, String> getExpertDatasetRFLabels() {
//        Map<String, String> labels = new HashMap<>();
//
//        labels.put("go|to|the|green|room.txt", "agentInRegion(agent0, room1)");
//        labels.put("go|to|the|red|room.txt", "agentInRegion(agent0, room0)");
//        labels.put("move|bag|to|blue|room.txt", "blockInRegion(block0, room2)");
//        labels.put("move|bag|to|blue|room|and|then|go|to|the|green|room.txt", "agentInRegion" +
//                        "(agent0, room1) blockInRegion(block0, room2)");
//        labels.put("move|bag|to|blue|room|and|then|go|to|the|red|room.txt", "agentInRegion" +
//                        "(agent0, room0) blockInRegion(block0, room2)");
//        labels.put("move|bag|to|green|room.txt", "blockInRegion(block0, room1)");
//        labels.put("move|bag|to|green|room|and|then|go|to|the|blue|room.txt", "agentInRegion" +
//                "(agent0, room2) blockInRegion(block0, room1)");
//        labels.put("move|bag|to|green|room|then|move|bag|to|blue|room.txt", "blockInRegion" +
//                "(block0, room2)");
//        labels.put("move|bag|to|the|green|room|then|go|to|the|green|room.txt", "agentInRegion" +
//                "(agent0, room1) blockInRegion(block0, room1)");
//        labels.put("move|bag|to|the|green|room|then|go|to|the|red|room.txt", "agentInRegion" +
//                "(agent0, room0) blockInRegion(block0, room1)");
//        return labels;
//    }
}
