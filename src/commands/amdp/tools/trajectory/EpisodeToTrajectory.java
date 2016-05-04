package commands.amdp.tools.trajectory;

import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.oomdp.core.Domain;
import commands.amdp.domain.CleanupL1AMDPDomain;
import commands.amdp.domain.CleanupL2AMDPDomain;
import commands.amdp.domain.CleanupWorld;
import commands.amdp.tools.parse.CleanupL0Parser;
import commands.amdp.tools.parse.CleanupL1Parser;
import commands.amdp.tools.parse.CleanupL2Parser;
import commands.data.Trajectory;
import commands.data.TrajectoryParser;

import java.io.*;
import java.util.Arrays;

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

        CleanupL2AMDPDomain cleanupWorldL2 = new CleanupL2AMDPDomain();
        Domain domainL2 = cleanupWorldL2.generateDomain();

        for (File f: episodes) {
            String pathName = f.getName();
            String[] splitPath = pathName.split("\\|");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < splitPath.length; i++) {
                if (i == splitPath.length - 1) {
                    String toAppend = splitPath[i].substring(0, splitPath[i].length() - 9);
                    builder.append(toAppend);
                    builder.append(" ");
                } else {
                    builder.append(splitPath[i]);
                    builder.append(" ");
                }
            }
            String naturalCommand = builder.toString();
            EpisodeAnalysis ep = EpisodeAnalysis.parseFileIntoEA(f.getAbsolutePath(), domainL2);
            Trajectory trajectory = new Trajectory(ep.stateSequence, ep.actionSequence);
            TrajectoryParser tp = new TrajectoryParser(domainL2, new CleanupL2Parser(domainL2));

            String trajectoryRep = tp.getStringRepForTrajectory(trajectory);

            pathName = pathName.substring(0, pathName.length() - 9);
            String fileName = outputDir + "/" + pathName + ".txt";
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)))) {
                bw.write(naturalCommand);
                bw.write("\n");
                bw.write(trajectoryRep);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String episodeDirectory = "/Users/sidd/Projects/commandsToTasks/data/amdpData/L2/ea";
        String outputDirectory = "/Users/sidd/Projects/commandsToTasks/data/amdpData/L2" +
                "/trajectory";

        File dir = new File(episodeDirectory);
        File[] episodes = dir.listFiles();

        parseEpisodes(episodes, outputDirectory);

    }
}
