package commands.amdp.experiments;

import commands.amdp.experiments.cleanupL0.CleanupL0ControllerConstructor;
import commands.amdp.experiments.cleanupL1.CleanupL1ControllerConstructor;
import commands.amdp.experiments.cleanupL2.CleanupL2ControllerConstructor;
import commands.amdp.replicate.mt.IBM2;
import commands.data.TrainingElement;
import commands.model3.TaskModule;
import commands.model3.mt.Tokenizer;
import commands.model3.weaklysupervisedinterface.MTWeaklySupervisedModel;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedController;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedLanguageModel;
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
//        Tokenizer tokenizer = new Tokenizer(true, true);
//        tokenizer.addDelimiter("-");
//
//        WeaklySupervisedLanguageModel model = new MTWeaklySupervisedModel(controller, tokenizer, 10);

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
//        Map<String,String> rfLabels = constructor.getExpertDatasetRFLabels();
        Map<String,String> rfLabels = constructor.getNewAMTDatasetRFLabels();


        //get source training data
        List<TrainingElement> dataset = constructor.getTrainingDataset(pathToDataset);


        //start LOO loop
        int nc = 0;
        System.out.println("Starting LOO cross validation test on " + dataset.size() + " samples");
        for(int i = 0; i < dataset.size(); i++){

            List<TrainingElement> trainingDataset = new ArrayList<TrainingElement>(dataset);
            trainingDataset.remove(i);

            //get our controller
            WeaklySupervisedController controller = constructor.generateNewController();

            //instantiate our MT language model
            createAndAddLanguageModel(controller);

            //load our IRL trajectory cache for fast IRL
//			controller.loadIRLProbabiltiesFromDisk(pathToIRLCache, constructor.getCacheStateParser());

            //instantiate the weakly supervised language model dataset using IRL
            controller.createWeaklySupervisedTrainingDatasetFromTrajectoryDataset(trainingDataset);

            //perform learning
            controller.trainLanguageModel();

            //test it
            GenerativeModel gm = controller.getGM();
            TrainingElement queryElement = dataset.get(i);
            String rfLabel = rfLabels.get(queryElement.identifier);
            if(rfLabel == null){
                System.out.println(queryElement.identifier);
                throw new RuntimeException("Encountered element of dataset without corresponding true RF label!");
            }
            List<GMQueryResult> rfDist = controller.getRFDistribution(queryElement.trajectory.getState(0), queryElement.command);
            GMQueryResult predicted = GMQueryResult.maxProb(rfDist);

            TaskModule.RFConVariableValue gr = (TaskModule.RFConVariableValue)predicted.getQueryForVariable(gm.getRVarWithName(TaskModule.GROUNDEDRFNAME));
            String grs = gr.toString().trim();
            if(grs.equals(rfLabel)){
                nc++;
                System.out.println("Correct: " + queryElement.identifier);
                System.out.println("Expected: " + rfLabel);
            }
            else{
                System.out.println("Incorrect: " + queryElement.identifier);
                System.out.println("Expected: " + rfLabel);
                System.out.println("Found: " + grs);
            }

            System.out.println("Current accuracy: " + (double) nc / (double) (i+1));
        }

        double accuracy = (double)nc/(double)dataset.size();
        System.out.println(nc + "/" + dataset.size() + "; " + accuracy);
    }

    public static void main(String[] args) {

        /**
         * Initial experimental results (10 samples per dataset -- 9 datasets total)
         * In pairs, first argument refers to level and second argument refers to language
         * NOTE: These results may be invalid since the RF label mappings were incorrect
         * L0 = 0.3
         * L1 = 0.0
         * L2 = 0.1
         * L0-L1 = 0.0
         * L0-L2 = 0.0
         * L1-L0 = 0.0
         * L1-L2 = 0.2
         * L2-L0 = 0.0
         * L2-L1 = 0.1
         */

        /**
         * Initial experimental results (30 samples per dataset -- 9 datasets total)
         * In pairs, first argument refers to level and second argument refers to language
         * L0 = 0.367
         * L1 = 0.0357 (only 28 samples?) *
         * L2 = 0.1 *
         * L0-L1 = 0.107 (only 28 samples?)
         * L0-L2 = 0.467
         * L1-L0 = 0.0
         * L1-L2 = 0.267
         * L2-L0 = 0.4
         * L2-L1 = 0.143 (only 28 samples?)
         */

        boolean cacheIRLResults = true;
//        boolean cacheIRLResults = false;

        CleanupL0ControllerConstructor l0Controller = new CleanupL0ControllerConstructor();
        CleanupL1ControllerConstructor l1Controller = new CleanupL1ControllerConstructor();
        CleanupL2ControllerConstructor l2Controller = new CleanupL2ControllerConstructor();


        if(cacheIRLResults) {
//            cacheIRLResultsFor(l0Controller, l0Controller.EXPERTDATASET, L0_TRAJ_CACHE);
//            cacheIRLResultsFor(l1Controller, l1Controller.EXPERTDATASET, L1_TRAJ_CACHE);
//            cacheIRLResultsFor(l2Controller, l2Controller.EXPERTDATASET, L2_TRAJ_CACHE);
        }

//        LOOTest(l0Controller, l0Controller.EXPERTDATASET, "data/jerryTrajectoryCache"); //L0
//        LOOTest(l1Controller, l1Controller.NEWAMTDATASET, L1_TRAJ_CACHE); //L1
        LOOTest(l2Controller, l2Controller.NEWAMTDATASET, L2_TRAJ_CACHE); //L2

//        LOOTest(l0Controller, l0Controller.L1CROSSDATASET, L0_TRAJ_CACHE); //L0-L1
//        LOOTest(l0Controller, l0Controller.L2CROSSDATASET, L0_TRAJ_CACHE); //L0-L2
//
//        LOOTest(l1Controller, l1Controller.L0CROSSDATASET, L1_TRAJ_CACHE); //L1-L0
//        LOOTest(l1Controller, l1Controller.L2CROSSDATASET, L1_TRAJ_CACHE); //L1-L2

//        LOOTest(l2Controller, l2Controller.L0CROSSDATASET, L2_TRAJ_CACHE); //L2-L0
//        LOOTest(l2Controller, l2Controller.L1CROSSDATASET, L2_TRAJ_CACHE); //L2-L1
    }
}
