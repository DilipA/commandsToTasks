package commands.amdp.experiments.cleanupL2;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.legacy.StateParser;
import commands.amdp.experiments.ControllerConstructor;
import commands.data.TrainingElement;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedController;

import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/30/16.
 */
public class CleanupL2ControllerConstructor implements ControllerConstructor {
    public static final String EXPERTDATASET = "data/amdpData/L2";

    public CleanupL2ControllerConstructor(){

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
