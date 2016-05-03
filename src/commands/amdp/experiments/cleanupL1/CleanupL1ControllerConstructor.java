package commands.amdp.experiments.cleanupL1;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.legacy.StateParser;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import commands.amdp.domain.CleanupL1AMDPDomain;
import commands.amdp.domain.CleanupWorld;
import commands.amdp.experiments.ControllerConstructor;
import commands.amdp.tools.parse.CleanupL1Parser;
import commands.data.TrainingElement;
import commands.data.TrainingElementParser;
import commands.model3.GPConjunction;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/26/16.
 */
public class CleanupL1ControllerConstructor implements ControllerConstructor{
    public static final String EXPERTDATASET = "data/amdpData/L1/trajectory";

    public static final String L0CROSSDATASET = "data/amdpData/L0L1/trajectory";

    public static final String L2CROSSDATASET = "data/amdpData/L2L1/trajectory";

    public DomainGenerator domainGenerator;
    public Domain domain;
    public HashableStateFactory hashingFactory;
    public List<GPConjunction> liftedTaskDescriptions;
    public StateParser sp;
    public StateParser cacheStateParser;

    public CleanupL1ControllerConstructor(){

        this.domainGenerator = buildDomainGenerator();
        this.domain = this.domainGenerator.generateDomain();
        this.hashingFactory = new SimpleHashableStateFactory(false);
        this.liftedTaskDescriptions = new ArrayList<>(2);

        GPConjunction atr = new GPConjunction();
        atr.addGP(new GroundedProp(domain.getPropFunction(CleanupL1AMDPDomain.PF_AGENT_IN_REGION), new String[]{"a", "r"}));
        this.liftedTaskDescriptions.add(atr);

        GPConjunction btr = new GPConjunction();
        btr.addGP(new GroundedProp(domain.getPropFunction(CleanupL1AMDPDomain.PF_BLOCK_IN_REGION), new String[]{"b", "r"}));
        this.liftedTaskDescriptions.add(btr);

        GPConjunction abtr = new GPConjunction();
        abtr.addGP(new GroundedProp(domain.getPropFunction(CleanupL1AMDPDomain.PF_AGENT_IN_REGION), new String[]{"a", "r1"}));
        abtr.addGP(new GroundedProp(domain.getPropFunction(CleanupL1AMDPDomain.PF_BLOCK_IN_REGION), new String[]{"b", "r2"}));
        this.liftedTaskDescriptions.add(abtr);


        this.sp = new CleanupL1Parser(this.domain);
        this.cacheStateParser = sp;
    }

    @Override
    public DomainGenerator buildDomainGenerator(){
        CleanupWorld cw = new CleanupWorld();
        cw.includeDirectionAttribute(true);
        cw.includePullAction(false);
        cw.includeWallPF_s(true);
        cw.includeLockableDoors(false);
        cw.setLockProbability(0.0);
        Domain domain = cw.generateDomain();
        CleanupL1AMDPDomain cleanupWorldL1 = new CleanupL1AMDPDomain(domain);
        cleanupWorldL1.setLockableDoors(false);
        return cleanupWorldL1;
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
    public StateParser getSP(){
        return this.sp;
    }

    @Override
    public StateParser getCacheStateParser(){
        return this.cacheStateParser;
    }

    //This is how we get the right "answer" - the RF produced by the model is compared to the string here
    @Override
    public Map<String, String> getExpertDatasetRFLabels(){

        Map<String, String> labels = new HashMap<>();

        labels.put("go|to|red|door|go|into|red|room.txt", "agentInRegion(agent0, room0)");
        labels.put("go|to|red|door|go|into|red|room|go|to|green|door.txt", "agentInRegion(agent0, door1)");
        labels.put("go|to|red|door|go|into|red|room|go|to|green|door|go|into|green|room.txt", "agentInRegion(agent0, room1)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|green|door|go|to|red|door.txt", "agentInRegion(agent0, door0)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|green|door|move|to|green|door|move|bag|to|green|room.txt", "blockInRegion(block0, room1)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|red|door.txt", "blockInRegion(block0, door0)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|red|door|go|to|red|door.txt", "agentInRegion(agent0, door0) blockInRegion(block0, door0)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|red|door|move|to|red|door|move|bag|to|red|room.txt", "blockInRegion(block0, room0)");
        labels.put("go|to|red|door|go|to|blue|room.txt", "agentInRegion(agent0, room2)");
        labels.put("go|to|red|door|go|to|red|room|move|bag|to|green|door.txt", "blockInRegion(block0, door1)");

        return labels;
    }
}
