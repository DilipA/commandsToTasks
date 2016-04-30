package commands.amdp.experiments.cleanupL2;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.legacy.StateParser;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import commands.amdp.domain.CleanupWorld;
import commands.amdp.experiments.ControllerConstructor;
import commands.amdp.tools.parse.CleanupL2Parser;
import commands.data.TrainingElement;
import commands.model3.GPConjunction;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/30/16.
 */
public class CleanupL2ControllerConstructor implements ControllerConstructor {
    public static final String EXPERTDATASET = "data/amdpData/L2";

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
        atr.addGP(new GroundedProp(domain.getPropFunction(), new String[]{"a", "r"}));
        this.liftedTaskDescriptions.add(atr);

        GPConjunction btr = new GPConjunction();
        btr.addGP(new GroundedProp(domain.getPropFunction(), new String[]{"b", "r"}));
        this.liftedTaskDescriptions.add(btr);

        GPConjunction abtr = new GPConjunction();
        abtr.addGP(new GroundedProp(domain.getPropFunction(), new String[]{"a", "r1"}));
        abtr.addGP(new GroundedProp(domain.getPropFunction(), new String[]{"b", "r2"}));
        this.liftedTaskDescriptions.add(abtr);

        this.sp = new CleanupL2Parser(this.domain);
        this.cacheStateParser = sp;
    }

    @Override
    public DomainGenerator buildDomainGenerator() {
        return null;
    }

    @Override
    public WeaklySupervisedController generateNewController() {
        return null;
    }

    @Override
    public List<TrainingElement> getTrainingDataset(String pathToDatasetDir) {
        return null;
    }

    @Override
    public Map<String, String> getExpertDatasetRFLabels() {
        return null;
    }

    @Override
    public StateParser getSP() {
        return null;
    }

    @Override
    public StateParser getCacheStateParser() {
        return null;
    }
}
