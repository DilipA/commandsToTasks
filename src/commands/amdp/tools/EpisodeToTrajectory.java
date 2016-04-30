package commands.amdp.tools;

import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.oomdp.core.Domain;
import commands.amdp.domain.CleanupL1AMDPDomain;
import commands.amdp.domain.CleanupWorld;
import commands.amdp.tools.CleanupL1Parser;
import commands.data.Trajectory;
import commands.data.TrajectoryParser;

import java.io.File;

/**
 * EpisodeToTrajectory.java
 *
 * Reads in a directory of Episode Analysis objects, turns it into a commandsToTask flavored
 * Trajectory.
 *
 * Created by Sidd Karamcheti on 4/26/16.
 */
public class EpisodeToTrajectory {

    public static void parseEpisodes(File[] episodes, String outputDir) {
        CleanupWorld cw = new CleanupWorld();
        cw.includeDirectionAttribute(true);
        cw.includePullAction(true);
        cw.includeWallPF_s(true);
        cw.includeLockableDoors(true);
        cw.setLockProbability(0.5);

        Domain domain = cw.generateDomain();
        CleanupL1AMDPDomain cleanupWorldL1 = new CleanupL1AMDPDomain(domain);
        cleanupWorldL1.setLockableDoors(false);
        Domain domainL1 = cleanupWorldL1.generateDomain();

        for (File f: episodes) {
            String pathName = f.getName();
            String[] splitPath = pathName.split("|");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < splitPath.length - 1; i++) {
                builder.append(splitPath[i]);
                builder.append(" ");
            }
            String naturalCommand = builder.toString();
            EpisodeAnalysis ep = EpisodeAnalysis.parseFileIntoEA(pathName, domainL1); // TODO
            Trajectory trajectory = new Trajectory(ep.stateSequence, ep.actionSequence);
            TrajectoryParser tp = new TrajectoryParser(domainL1, new CleanupL1Parser(domainL1));
        }
    }

    public static void main(String[] args) {
        String episodeDirectory = ""; // TODO
        String outputDirectory = ""; // TODO

        File dir = new File(episodeDirectory);
        File[] episodes = dir.listFiles();

        parseEpisodes(episodes, outputDirectory);

    }
}
