package frc.robot.auto.actions;

import frc.robot.DriveTrain;
import frc.robot.auto.util.Action;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;

/**
 *
 */
public class VisionTrack implements Action {

	DriveTrain drive;
	NetworkTable table;
	NetworkTableEntry tx;
	NetworkTableEntry ty;

	double vision_X;
	double vision_Y;

	boolean timedOut;
	int timeOutCounter;

	public VisionTrack(DriveTrain drive) {

		this.drive = drive;
		table = NetworkTableInstance.getDefault().getTable("limelight");
		tx = table.getEntry("tx");
		ty = table.getEntry("ty");

		timedOut = false;
		timeOutCounter = 0;

	}

	@Override
	public boolean isFinished() {
		if (vision_X >= 1.0 && vision_Y <= -6.5) {
			return true;
		} else if (timedOut) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void start() {

		vision_X = tx.getDouble(0.0);
		vision_Y = ty.getDouble(0.0);

	}

	@Override
	public void update() {

		vision_X = tx.getDouble(0.0);
		vision_Y = ty.getDouble(0.0);

		double turnVal = vision_X / 25;
		turnVal = Math.min(turnVal, .4);
		turnVal = Math.max(-.4, turnVal);
		double forwardVal = .4;
		drive.FPSDrive(forwardVal, turnVal);

		if (timeOutCounter == 92) {
			timedOut = true;
		}
		timeOutCounter++;
		DriverStation.reportWarning("timeOutCounter = " + timeOutCounter, false);
	}

	@Override
	public void end() {
		drive.stopDrive();
	}

}
