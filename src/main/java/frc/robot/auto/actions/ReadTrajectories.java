package frc.robot.auto.actions;

import java.io.File;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.auto.util.Action;
import frc.robot.pathfinder.Pathfinder;
import frc.robot.pathfinder.Trajectory;

public class ReadTrajectories implements Action {

    boolean finished;

    public ReadTrajectories() {
        finished = false;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void start() {
        File outsideFile = new File("/home/lvuser/outside");
        File insideFile = new File("/home/lvuser/inside");

        try {
            Trajectory outside = Pathfinder.readFromFile(outsideFile);
            DriverStation.reportWarning("outread-" + outside.length(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Trajectory inside = Pathfinder.readFromFile(insideFile);
            DriverStation.reportWarning("inread-" + inside.length(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
		finished = true;
	}

	@Override
	public void update() {

	}

	@Override
	public void end() {
		//

	}

}