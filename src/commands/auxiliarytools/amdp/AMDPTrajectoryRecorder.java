package commands.auxiliarytools.amdp;

import burlap.oomdp.auxiliary.StateGenerator;
import burlap.oomdp.core.Domain;
import burlap.oomdp.legacy.StateParser;
import commands.data.TrainingElement;
import commands.data.TrainingElementParser;
import commands.data.Trajectory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/21/16.
 */
public class AMDPTrajectoryRecorder {

    private static final long serialVersionUID = 1L;

    //Backend
    protected TrainingElementParser parser;

    protected List<String> dataFiles;

    protected TrainingElement trainEl;

    protected Trajectory trajectory;

    protected boolean								selectedOnNewTrajectory;

    protected Domain domain;
    protected StateGenerator sg;


    private Map<String, String> actionMap;

    protected String								directory;

    public AMDPTrajectoryRecorder(){
        actionMap = new HashMap<>();
        selectedOnNewTrajectory = false;
    }

    public void addKeyAction(String key, String action){
        actionMap.put(key, action);
    }

    public void init(Domain d, StateParser sp, StateGenerator sg, String dataDirectory){
        parser = new TrainingElementParser(d, sp);
        domain = d;
        this.sg = sg;

        directory = dataDirectory;

        //get rid of trailing / and pull out the file paths
        if(directory.charAt(directory.length()-1) == '/'){
            directory = directory.substring(0, directory.length());
        }

        dataFiles = new ArrayList<>();
    }


}
