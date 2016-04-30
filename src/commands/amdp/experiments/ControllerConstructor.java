package commands.amdp.experiments;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.legacy.StateParser;
import commands.data.TrainingElement;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedController;

import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/30/16.
 */
public interface ControllerConstructor {

    public DomainGenerator buildDomainGenerator();

    public WeaklySupervisedController generateNewController();

    public List<TrainingElement> getTrainingDataset(String pathToDatasetDir);

    public Map<String, String> getExpertDatasetRFLabels();

    public StateParser getSP();

    public StateParser getCacheStateParser();

    static void addLabelMappingForRange(Map<String, String> labels, String baseIdentifier, String extension, int rangeStart, int rangeMax, String label){

        for(int i = rangeStart; i < rangeMax; i++){
            String name = baseIdentifier + i + "." + extension;
            labels.put(name, label);
        }

    }
}
