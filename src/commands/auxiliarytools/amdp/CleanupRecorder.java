package commands.auxiliarytools.amdp;

import burlap.oomdp.auxiliary.StateGenerator;
import burlap.oomdp.auxiliary.StateMapping;
import burlap.oomdp.core.Domain;
import burlap.oomdp.legacy.StateParser;
import burlap.oomdp.visualizer.Visualizer;
import commands.amdp.domain.CleanupL1AMDPDomain;
import commands.amdp.domain.CleanupWorld;
import commands.amdp.framework.AMDPDomain;
import commands.auxiliarytools.TrajectoryRecorder;
import commands.auxiliarytools.sokoban.soko2turkexamples.Soko2Turk1;
import domain.singleagent.sokoban2.Sokoban2Domain;
import domain.singleagent.sokoban2.Sokoban2Visualizer;
import domain.singleagent.sokoban2.SokobanOldToNewParser;

/**
 * CleanupRecorder.java
 *
 * Created by Sidd Karamcheti on 4/21/16.
 */
public class CleanupRecorder {

    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Incorrect format; use:\n\tpathToDataDirectory");
            System.exit(0);
        }

        CleanupWorld cw = new CleanupWorld();
        cw.includeDirectionAttribute(true);
        cw.includePullAction(false);
        cw.includeWallPF_s(true);
        cw.includeLockableDoors(false);

        Domain domain = cw.generateDomain();
        CleanupL1AMDPDomain cleanupWorldL1 = new CleanupL1AMDPDomain(domain);
        cleanupWorldL1.setLockableDoors(false);
        Domain domainL1 = cleanupWorldL1.generateDomain();

//        StateGenerator sg = new Soko2Turk1(domain);
//        Visualizer v = Sokoban2Visualizer.getVisualizer(pathToRobotImagesDirectory);
//        //StateParser sp = new Sokoban2Parser(domain);
//        StateParser sp = new SokobanOldToNewParser(domain);
//
//        String datapath = args[0];
//
//        TrajectoryRecorder tr = new TrajectoryRecorder();
//        tr.addKeyAction("w", Sokoban2Domain.ACTIONNORTH);
//        tr.addKeyAction("s", Sokoban2Domain.ACTIONSOUTH);
//        tr.addKeyAction("d", Sokoban2Domain.ACTIONEAST);
//        tr.addKeyAction("a", Sokoban2Domain.ACTIONWEST);
//
//
//        tr.init(v, domain, sp, sg, datapath);
    }
}
