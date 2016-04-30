package commands.amdp.experiments.cleanupL0;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.legacy.StateParser;
import burlap.oomdp.statehashing.HashableStateFactory;
import commands.amdp.domain.CleanupWorld;
import commands.amdp.experiments.ControllerConstructor;
import commands.data.TrainingElement;
import commands.model3.GPConjunction;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedController;

import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/30/16.
 */
public class CleanupL0ControllerConstructor implements ControllerConstructor {
    public static final String EXPERTDATASET = "data/amdpData/L0";


    public DomainGenerator domainGenerator;
    public Domain domain;
    public HashableStateFactory hashingFactory;
    public List<GPConjunction> liftedTaskDescriptions;
    public StateParser sp;
    public StateParser cacheStateParser;

    public CleanupL0ControllerConstructor(){

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
