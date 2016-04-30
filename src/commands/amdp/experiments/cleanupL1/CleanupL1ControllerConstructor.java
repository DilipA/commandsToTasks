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
import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/26/16.
 */
public class CleanupL1ControllerConstructor implements ControllerConstructor{
    public static final String EXPERTDATASET = "data/amdpData/L1";

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


        this.sp = new CleanupL1Parser(this.domain);
        this.cacheStateParser = sp;
    }

    @Override
    public DomainGenerator buildDomainGenerator(){
        CleanupWorld cw = new CleanupWorld();
        cw.includeDirectionAttribute(true);
        cw.includePullAction(true);
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


    //TODO: Once dataset is compiled, fill with right answers!
    //This is how we get the right "answer" - the RF produced by the model is compared to the string here
    @Override
    public Map<String, String> getExpertDatasetRFLabels(){
        throw new UnsupportedOperationException();
//
//        Map<String, String> labels = new HashMap<String, String>();
//
//        ControllerConstructor.addLabelMappingForRange(labels, "block1ToRight_", "txt", 1, 21, "blockInRoom(block1, room2)");
//        ControllerConstructor.addLabelMappingForRange(labels, "block1ToBottom_", "txt", 1, 21, "blockInRoom(block1, room0)");
//        ControllerConstructor.addLabelMappingForRange(labels, "agentToLeft_", "txt", 1, 21, "agentInRoom(agent0, room1)");
//        ControllerConstructor.addLabelMappingForRange(labels, "agentToBottom_", "txt", 1, 21, "agentInRoom(agent0, room0)");
//        ControllerConstructor.addLabelMappingForRange(labels, "blockToLeft_", "txt", 1, 21, "blockInRoom(block0, room1)");
//        ControllerConstructor.addLabelMappingForRange(labels, "blockToRight_", "txt", 1, 21, "blockInRoom(block0, room2)");
//        ControllerConstructor.addLabelMappingForRange(labels, "blockToBottom_", "txt", 1, 21, "blockInRoom(block0, room0)");
//        ControllerConstructor.addLabelMappingForRange(labels, "agent2LNblock2R_", "txt", 1, 21, "agentInRoom(agent0, room1) blockInRoom(block0, room2)");
//        ControllerConstructor.addLabelMappingForRange(labels, "agent2RNblock2L_", "txt", 1, 21, "agentInRoom(agent0, room2) blockInRoom(block0, room1)");
//
//        return labels;
    }
}
