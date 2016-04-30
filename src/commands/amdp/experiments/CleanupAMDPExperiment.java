package commands.amdp.experiments;

import commands.amdp.experiments.cleanupL0.CleanupL0ControllerConstructor;
import commands.amdp.experiments.cleanupL1.CleanupL1ControllerConstructor;
import commands.amdp.experiments.cleanupL2.CleanupL2ControllerConstructor;
import commands.amdp.replicate.mt.IBM2;
import commands.data.TrainingElement;
import commands.model3.TaskModule;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedController;
import generativemodel.GMQueryResult;
import generativemodel.GenerativeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/30/16.
 */
public class CleanupAMDPExperiment {

    protected static final String L0_TRAJ_CACHE = "data/amdpData/trajectoryCache/L0";
    protected static final String L1_TRAJ_CACHE = "data/amdpData/trajectoryCache/L1";
    protected static final String L2_TRAJ_CACHE = "data/amdpData/trajectoryCache/L2";


    public static void createAndAddLanguageModel(WeaklySupervisedController controller){
        IBM2 model = new IBM2();
        System.out.println("Finished loading MT model...");
        //set our controller to use the MT model we created
        controller.setLanguageModel(model);
    }

    public static void cacheIRLResultsFor(ControllerConstructor constructor, String pathToDataset, String pathToCacheDirectory){
        //get our controller
        WeaklySupervisedController controller = constructor.generateNewController();

        //instantiate our MT language model
        createAndAddLanguageModel(controller);

        //get training data
        List<TrainingElement> dataset = constructor.getTrainingDataset(pathToDataset);

        //instantiate the weakly supervised language model dataset using IRL
        controller.createWeaklySupervisedTrainingDatasetFromTrajectoryDataset(dataset);

        controller.cacheIRLProbabilitiesToDisk(pathToCacheDirectory, constructor.getSP());

    }

    public static void LOOTest(ControllerConstructor constructor, String pathToDataset, String pathToIRLCache){
        Map<String,String> rfLabels = constructor.getExpertDatasetRFLabels();

        //get source training data
        List<TrainingElement> dataset = constructor.getTrainingDataset(pathToDataset);


        //start LOO loop
        int nc = 0;
        for(int i = 0; i < dataset.size(); i++){

            List<TrainingElement> trainingDataset = new ArrayList<TrainingElement>(dataset);
            trainingDataset.remove(i);

            //get our controller
            WeaklySupervisedController controller = constructor.generateNewController();

            //instantiate our MT language model
            createAndAddLanguageModel(controller);

            //load our IRL trajectory cache for fast IRL
			controller.loadIRLProbabiltiesFromDisk(pathToIRLCache, constructor.getCacheStateParser());

            //instantiate the weakly supervised language model dataset using IRL
            controller.createWeaklySupervisedTrainingDatasetFromTrajectoryDataset(trainingDataset);

            //perform learning
            controller.trainLanguageModel();

            //test it
            GenerativeModel gm = controller.getGM();
            TrainingElement queryElement = dataset.get(i);
            String rfLabel = rfLabels.get(queryElement.identifier);
            List<GMQueryResult> rfDist = controller.getRFDistribution(queryElement.trajectory.getState(0), queryElement.command);
            GMQueryResult predicted = GMQueryResult.maxProb(rfDist);

            TaskModule.RFConVariableValue gr = (TaskModule.RFConVariableValue)predicted.getQueryForVariable(gm.getRVarWithName(TaskModule.GROUNDEDRFNAME));
            String grs = gr.toString().trim();
            if(grs.equals(rfLabel)){
                nc++;
                System.out.println("Correct: " + queryElement.identifier);
            }
            else{
                System.out.println("Incorrect: " + queryElement.identifier);
            }

            System.out.println("Current accuracy: " + (double) nc / (double) (i+1));
        }

        double accuracy = (double)nc/(double)dataset.size();
        System.out.println(nc + "/" + dataset.size() + "; " + accuracy);
    }

    public static void main(String[] args) {

        boolean cacheIRLResults = true;
//        boolean cacheIRLResults = false;

//        CleanupL0ControllerConstructor l0Controller = new CleanupL0ControllerConstructor();
        CleanupL1ControllerConstructor l1Controller = new CleanupL1ControllerConstructor();
//        CleanupL2ControllerConstructor l2Controller = new CleanupL2ControllerConstructor();


        if(cacheIRLResults) {
//            cacheIRLResultsFor(l0Controller, l0Controller.EXPERTDATASET, L0_TRAJ_CACHE);
            cacheIRLResultsFor(l1Controller, l1Controller.EXPERTDATASET, L1_TRAJ_CACHE);
//            cacheIRLResultsFor(l2Controller, l2Controller.EXPERTDATASET, L2_TRAJ_CACHE);
        }

//        LOOTest(l0Controller, l0Controller.EXPERTDATASET, L0_TRAJ_CACHE);
        LOOTest(l1Controller, l1Controller.EXPERTDATASET, L1_TRAJ_CACHE);
//        LOOTest(l2Controller, l2Controller.EXPERTDATASET, L2_TRAJ_CACHE);
    }
}
