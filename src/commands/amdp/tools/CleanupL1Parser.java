package commands.amdp.tools;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
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
    public State stringToState(String s) {
        return null;
    }
}
