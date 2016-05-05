package commands.amdp.experiments;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.legacy.StateParser;
import commands.data.TrainingElement;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dilip Arumugam on 4/30/16.
 */
public interface ControllerConstructor {

    public DomainGenerator buildDomainGenerator();

    public WeaklySupervisedController generateNewController();

    public List<TrainingElement> getTrainingDataset(String pathToDatasetDir);

    public static Map<String, String> getExpertDatasetRFLabels(){
        Map<String, String> labels = new HashMap<>();

        //L0
        labels.put("move|four|steps|south|four|steps|west.txt", "agentInRoom(agent0, room0)");
        labels.put("move|one|step|east|one|step|south|then|two|steps|north.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|north.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|north|one|step|west.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|north|two|steps|south.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|south|one|step|east.txt", "agentInRoom(agent0, room2)");
        labels.put("move|one|step|west.txt", "agentInRoom(agent0, room2)");
        labels.put("move|three|steps|south|one|step|east.txt", "agentInRoom(agent0, room0)");
        labels.put("move|three|steps|south|one|step|north.txt", "agentInRoom(agent0, room0)");
        labels.put("move|three|steps|south|one|step|west.txt", "agentInRoom(agent0, room0)");

        //L1
        labels.put("go|to|red|door|go|into|red|room.txt", "agentInRegion(agent0, room0)");
        labels.put("go|to|red|door|go|into|red|room|go|to|green|door.txt", "agentInRegion(agent0, door1)");
        labels.put("go|to|red|door|go|into|red|room|go|to|green|door|go|into|green|room.txt", "agentInRegion(agent0, room1)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|green|door|go|to|red|door.txt", "agentInRegion(agent0, door0)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|green|door|move|to|green|door|move|bag|to|green|room.txt", "blockInRegion(block0, room1)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|red|door.txt", "blockInRegion(block0, door0)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|red|door|go|to|green|door.txt", "agentInRegion(agent0, door0) blockInRegion(block0, door0)");
        labels.put("go|to|red|door|go|into|red|room|move|bag|to|red|door|move|to|red|door|move|bag|to|red|room.txt", "blockInRegion(block0, room0)");
        labels.put("go|to|red|door|go|to|blue|room.txt", "agentInRegion(agent0, room2)");
        labels.put("go|to|red|door|go|to|red|room|move|bag|to|green|door.txt", "blockInRegion(block0, door1)");

        //L2
        labels.put("go|to|the|green|room.txt", "agentInRegion(agent0, room1)");
        labels.put("go|to|the|red|room.txt", "agentInRegion(agent0, room0)");
        labels.put("move|bag|to|blue|room.txt", "blockInRegion(block0, room2)");
        labels.put("move|bag|to|blue|room|and|then|go|to|the|green|room.txt", "agentInRegion" +
                "(agent0, room1) blockInRegion(block0, room2)");
        labels.put("move|bag|to|blue|room|and|then|go|to|the|red|room.txt", "agentInRegion" +
                "(agent0, room0) blockInRegion(block0, room2)");
        labels.put("move|bag|to|green|room.txt", "blockInRegion(block0, room1)");
        labels.put("move|bag|to|green|room|and|then|go|to|the|blue|room.txt", "agentInRegion" +
                "(agent0, room2) blockInRegion(block0, room1)");
        labels.put("move|bag|to|green|room|then|move|bag|to|blue|room.txt", "blockInRegion" +
                "(block0, room2)");
        labels.put("move|bag|to|the|green|room|then|go|to|the|green|room.txt", "agentInRegion" +
                "(agent0, room1) blockInRegion(block0, room1)");
        labels.put("move|bag|to|the|green|room|then|go|to|the|red|room.txt", "agentInRegion" +
                "(agent0, room0) blockInRegion(block0, room1)");

        return labels;
    }

    public StateParser getSP();

    public StateParser getCacheStateParser();

    public static void addLabelMappingForRange(Map<String, String> labels, String baseIdentifier, String extension, int rangeStart, int rangeMax, String label){

        for(int i = rangeStart; i < rangeMax; i++){
            String name = baseIdentifier + i + "." + extension;
            labels.put(name, label);
        }

    }
}
