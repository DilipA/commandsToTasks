package commands.amdp.tools.parse;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.legacy.StateParser;
import commands.amdp.domain.CleanupWorld;

import java.util.List;

/**
 * Created by Dilip Arumugam on 4/30/16.
 */
public class CleanupL0Parser implements StateParser {
    protected Domain domain;

    public CleanupL0Parser(Domain domain) {
        this.domain = domain;
    }

    @Override
    public String stateToString(State s) {
        StringBuffer buf = new StringBuffer();
        List<ObjectInstance> rooms = s.getObjectsOfClass(CleanupWorld.CLASS_ROOM);
        List<ObjectInstance> doors = s.getObjectsOfClass(CleanupWorld.CLASS_DOOR);
        List<ObjectInstance> blocks = s.getObjectsOfClass(CleanupWorld.CLASS_BLOCK);
        List<ObjectInstance> agents = s.getObjectsOfClass(CleanupWorld.CLASS_AGENT);

        for(ObjectInstance o : rooms){
            buf.append("room,");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_COLOR)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_LEFT)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_TOP)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_RIGHT)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_BOTTOM)).append(" ");
        }
        for(ObjectInstance o : doors){
            buf.append("door,");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_LEFT)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_TOP)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_RIGHT)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_BOTTOM)).append(" ");
        }
        for(ObjectInstance o : blocks){
            buf.append("block,");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_COLOR)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_SHAPE)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_X)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_Y)).append(" ");
        }
        boolean first = true;
        for(ObjectInstance o : agents){
            if(!first){
                buf.append(" ");
            }
            buf.append("agent,");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_X)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_Y));
            first = false;
        }

        return buf.toString();
    }

    @Override
    public State stringToState(String str) {
        State s = new MutableState();
        int rooms = 0;
        int doors = 0;
        int blocks = 0;
        int agents = 0;
        String[] objects = str.split(" ");
        for (int i = 0; i < objects.length; i++){
            String[] splitobject = objects[i].split(",");
            if (splitobject[0].equals("room")){
                ObjectInstance room = new MutableObjectInstance(this.domain.getObjectClass(CleanupWorld.CLASS_ROOM), CleanupWorld.CLASS_ROOM + rooms);
                room.setValue(CleanupWorld.ATT_COLOR, CleanupWorld.COLORS[Integer.parseInt(splitobject[1])]);
                room.setValue(CleanupWorld.ATT_LEFT, Integer.parseInt(splitobject[2]));
                room.setValue(CleanupWorld.ATT_TOP, Integer.parseInt(splitobject[3]));
                room.setValue(CleanupWorld.ATT_RIGHT, Integer.parseInt(splitobject[4]));
                room.setValue(CleanupWorld.ATT_BOTTOM, Integer.parseInt(splitobject[5]));
                s.addObject(room);
                rooms++;
            }
            else if (splitobject[0].equals("door")){
                ObjectInstance door = new MutableObjectInstance(this.domain.getObjectClass(CleanupWorld.CLASS_DOOR), CleanupWorld.CLASS_DOOR + doors);
                door.setValue(CleanupWorld.ATT_LEFT, Integer.parseInt(splitobject[1]));
                door.setValue(CleanupWorld.ATT_TOP, Integer.parseInt(splitobject[2]));
                door.setValue(CleanupWorld.ATT_RIGHT, Integer.parseInt(splitobject[3]));
                door.setValue(CleanupWorld.ATT_BOTTOM, Integer.parseInt(splitobject[4]));
                s.addObject(door);
                doors++;
            }
            else if (splitobject[0].equals("block")){
                ObjectInstance block = new MutableObjectInstance(this.domain.getObjectClass(CleanupWorld.CLASS_BLOCK), CleanupWorld.CLASS_BLOCK + blocks);
                block.setValue(CleanupWorld.ATT_COLOR, CleanupWorld.COLORS[Integer.parseInt(splitobject[1])]);
                block.setValue(CleanupWorld.ATT_SHAPE, CleanupWorld.SHAPES[Integer.parseInt(splitobject[2])]);
                block.setValue(CleanupWorld.ATT_X, Integer.parseInt(splitobject[3]));
                block.setValue(CleanupWorld.ATT_Y, Integer.parseInt(splitobject[4]));
                s.addObject(block);
                blocks++;
            }
            else if (splitobject[0].equals("agent")){
                ObjectInstance agent = new MutableObjectInstance(this.domain.getObjectClass(CleanupWorld.CLASS_AGENT), CleanupWorld.CLASS_AGENT + agents);
                agent.setValue(CleanupWorld.ATT_X, Integer.parseInt(splitobject[1].trim()));
                agent.setValue(CleanupWorld.ATT_Y, Integer.parseInt(splitobject[2].trim()));

                if(this.domain.getAttribute(CleanupWorld.ATT_DIR) != null){
                    agent.setValue(CleanupWorld.ATT_DIR, "south");
                }

                s.addObject(agent);
                agents++;
            }
        }

        return s;
    }

    public static void main(String[] args) {
        CleanupWorld cw = new CleanupWorld();
        cw.includeDirectionAttribute(true);
        cw.includePullAction(false);
        cw.includeWallPF_s(true);
        cw.includeLockableDoors(false);
        cw.setLockProbability(0.0);
        Domain domain = cw.generateDomain();

        State cleanupInitial = CleanupWorld.getExperimentState(domain);

        CleanupL0Parser parser = new CleanupL0Parser(domain);
        String parsed = parser.stateToString(cleanupInitial);
        System.out.println(parsed);
        State parsedBack = parser.stringToState(parsed);
        System.out.println(parser.stateToString(parsedBack));
        System.out.println("\n");
        System.out.println(cleanupInitial.equals(parsedBack));
        System.out.println(parsed.equals(parser.stateToString(parsedBack)));
    }
}
