package commands.amdp.tools.trajectory;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import commands.amdp.domain.CleanupL1AMDPDomain;
import commands.amdp.domain.CleanupL2AMDPDomain;
import commands.amdp.domain.CleanupWorld;
import commands.amdp.tools.parse.CleanupL1Parser;
import commands.amdp.tools.parse.CleanupL2Parser;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dilip Arumugam on 5/4/16.
 */
public class L1L2Mapper implements TrajectoryMapper {
    protected static final String outputDir = "data/amdpData/L1L2/trajectory/";

    protected final Domain source;

    protected final Domain target;

    protected final CleanupL1Parser sourceParser;

    protected final CleanupL2Parser targetParser;

    public L1L2Mapper(){
        CleanupWorld cw = new CleanupWorld();
        cw.includeDirectionAttribute(true);
        cw.includePullAction(false);
        cw.includeWallPF_s(true);
        cw.includeLockableDoors(false);
        cw.setLockProbability(0.0);
        Domain domain = cw.generateDomain();
        CleanupL1AMDPDomain cleanupWorldL1 = new CleanupL1AMDPDomain(domain);
        cleanupWorldL1.setLockableDoors(false);
        this.source = cleanupWorldL1.generateDomain();

        CleanupL2AMDPDomain cleanupWorldL2 = new CleanupL2AMDPDomain();
        this.target = cleanupWorldL2.generateDomain();
        this.sourceParser = new CleanupL1Parser(source);
        this.targetParser = new CleanupL2Parser(target);
    }

    @Override
    public String mapActionSequence(String actions) {
        String[] splitActions = actions.split(",");
        List<String> filtered = Arrays.stream(splitActions).filter(a -> !a.contains("Door")).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for(String s : filtered){
            sb.append(s);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    @Override
    public String mapState(String state) {
        return this.targetParser.stateToString(CleanupL2AMDPDomain.projectToAMDPState(this.sourceParser.stringToState(state), this.target));
    }


    @Override
    public void mapTrajectoryFile(File trajectory, String outputPath) {
        try(BufferedReader br = new BufferedReader(new FileReader(trajectory));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))){
            List<String> lines = br.lines().collect(Collectors.toList());

            bw.write(lines.get(0) + "\n");

            bw.write(this.mapActionSequence(lines.get(1)) + "\n");

            for(int i=2;i < lines.size();i++){
                bw.write(this.mapState(lines.get(i)) + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapTrajectoryDir(String path) {
        File trajectoryDir = new File(path);
        Collection<File> trajectoryFiles = Arrays.asList(trajectoryDir.listFiles()).stream().filter(f -> f.isFile()).collect(Collectors.toList());
        trajectoryFiles.stream().forEach(f -> this.mapTrajectoryFile(f, outputDir + f.getName()));
    }

    public static void main(String[] args) {
//        File test = new File("data/amdpData/L1/trajectory/go|to|red|door|go|into|red|room.txt");
//        String out = L1L2Mapper.outputDir + "go|to|red|door|go|into|red|room.txt";
        L1L2Mapper mapper = new L1L2Mapper();
//        mapper.mapTrajectoryFile(test, out);
        mapper.mapTrajectoryDir("data/amdpData/L1/trajectory");
    }
}
