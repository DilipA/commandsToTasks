package commands.amdp.tools.trajectory;

import java.io.File;
import java.util.Collection;

/**
 * Created by Dilip Arumugam on 5/3/16.
 */
public interface TrajectoryMapper {

    public String mapActionSequence(String actions);

    public String mapState(String state);

    public void mapTrajectoryFile(File trajectory, String outputPath);

    public void mapTrajectoryDir(String path);

}
