package commands.amdp.tools.parse;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.legacy.StateParser;
import commands.amdp.domain.CleanupL1AMDPDomain;
import commands.amdp.domain.CleanupWorld;
import java.util.List;
import java.util.Set;

/**
 * CleanupL1Parser.java
 *
 * State Parser for Cleanup World L1.
 *
 * Created by Sidd Karamcheti on 4/26/16.
 */
public class CleanupL1Parser implements StateParser {
    protected Domain domain;

    public CleanupL1Parser(Domain domain) {
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
            Set<String> conn = o.getAllRelationalTargets(CleanupL1AMDPDomain.ATT_CONNECTED);
            for (String str : conn) {
                buf.append(str).append(",");
            }
            buf.deleteCharAt(buf.length() - 1);
            buf.append(" ");
        }

        for(ObjectInstance o : doors){
            buf.append("door,");
            Set<String> conn = o.getAllRelationalTargets(CleanupL1AMDPDomain.ATT_CONNECTED);
            for (String str : conn) {
                buf.append(str).append(",");
            }
            buf.deleteCharAt(buf.length() - 1);
            buf.append(" ");
        }

        for (ObjectInstance o : blocks) {
            buf.append("block,");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_COLOR)).append(",");
            buf.append(o.getIntValForAttribute(CleanupWorld.ATT_SHAPE)).append(",");
            Set<String> conn = o.getAllRelationalTargets(CleanupL1AMDPDomain.ATT_IN_REGION);
            for (String str : conn) {
                buf.append(str).append(",");
            }
            buf.deleteCharAt(buf.length() - 1);
            buf.append(" ");
        }

        boolean first = true;
        for (ObjectInstance o : agents) {
            if (!first) {
                buf.append(" ");
            }
            buf.append("agent,");
            Set<String> conn = o.getAllRelationalTargets(CleanupL1AMDPDomain.ATT_IN_REGION);
            for (String str : conn) {
                buf.append(str).append(",");
            }
            buf.deleteCharAt(buf.length() - 1);
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
        for (int i = 0; i < objects.length; i++) {
            String[] splitobject = objects[i].split(",");
            if (splitobject[0].equals("room")) {
                ObjectInstance room = new MutableObjectInstance(this.domain.getObjectClass(CleanupWorld.CLASS_ROOM), CleanupWorld.CLASS_ROOM + rooms);
                room.setValue(CleanupWorld.ATT_COLOR, Integer.parseInt(splitobject[1]));
                for (int j = 2; j < splitobject.length; j++) {
                    room.addRelationalTarget(CleanupL1AMDPDomain.ATT_CONNECTED, splitobject[j]);
                }
                s.addObject(room);
                rooms++;
            } else if (splitobject[0].equals("door")) {
                ObjectInstance door = new MutableObjectInstance(this.domain.getObjectClass(CleanupWorld.CLASS_DOOR), CleanupWorld.CLASS_DOOR + doors);
                for (int j = 1; j < splitobject.length; j++) {
                    door.addRelationalTarget(CleanupL1AMDPDomain.ATT_CONNECTED, splitobject[j]);
                }
                s.addObject(door);
                doors++;
            } else if (splitobject[0].equals("block")) {
                ObjectInstance block = new MutableObjectInstance(this.domain.getObjectClass(CleanupWorld.CLASS_BLOCK), CleanupWorld.CLASS_BLOCK + blocks);
                block.setValue(CleanupWorld.ATT_COLOR, Integer.parseInt(splitobject[1]));
                block.setValue(CleanupWorld.ATT_SHAPE, Integer.parseInt(splitobject[2]));
                for (int j = 3; j < splitobject.length; j++) {
                    block.addRelationalTarget(CleanupL1AMDPDomain.ATT_IN_REGION, splitobject[j]);
                }
                s.addObject(block);
                blocks++;
            } else if (splitobject[0].equals("agent")) {
                ObjectInstance agent = new MutableObjectInstance(this.domain.getObjectClass(CleanupWorld.CLASS_AGENT), CleanupWorld.CLASS_AGENT + agents);
                for (int j = 1; j < splitobject.length; j++) {
                    agent.addRelationalTarget(CleanupL1AMDPDomain.ATT_IN_REGION, splitobject[j]);
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

        CleanupL1AMDPDomain cleanupWorldL1 = new CleanupL1AMDPDomain(domain);
        cleanupWorldL1.setLockableDoors(false);
        Domain domainL1 = cleanupWorldL1.generateDomain();

        State l1Initial = CleanupL1AMDPDomain.projectToAMDPState(cleanupInitial, domainL1);

        CleanupL1Parser parser = new CleanupL1Parser(domainL1);
        String parsed = parser.stateToString(l1Initial);
        System.out.println(parsed);
        System.out.println("\n");
        State parsedBack = parser.stringToState(parsed);
        System.out.println(l1Initial.equals(parsedBack));
        System.out.println(parser.stateToString(parsedBack));
    }
}
