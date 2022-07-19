package frc.robot.auto.modes;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.DriveTrain;
import frc.robot.Elevator;
import frc.robot.HatchIntake;
import frc.robot.auto.actions.AutonTankDrive;
import frc.robot.auto.actions.CloseHatchIntake;
import frc.robot.auto.actions.GrabHatch;
import frc.robot.auto.actions.MoveElevator;
import frc.robot.auto.actions.PlaceHatch;
import frc.robot.auto.actions.TestTrajectory;
import frc.robot.auto.actions.VisionTrack;
import frc.robot.auto.util.AutoMode;
import frc.robot.auto.util.AutoModeEndedException;

/**
 *
 */
public class CloseSideHatch extends AutoMode {

	DriveTrain drive;
	HatchIntake hatchIntake;
	Elevator elevator;

	public CloseSideHatch(DriveTrain drive, HatchIntake hatchIntake, Elevator elevator) {
		this.drive = drive;
		this.hatchIntake = hatchIntake;
		this.elevator = elevator;
	}

	@Override
	protected void routine() throws AutoModeEndedException {
		DriverStation.reportWarning("attempting to place hatch on close side of rocket", false);
		runAction(new CloseHatchIntake(hatchIntake, false));
		runAction(new TestTrajectory("closeSideHatchFromHAB", drive));
		runAction(new VisionTrack(drive));
		runAction(new PlaceHatch(drive, hatchIntake));
		runAction(new TestTrajectory("pullAwayFromCloseSideRocket", drive));
		runAction(new TestTrajectory("loadingStationFromCloseSideRocket", drive));
		runAction(new VisionTrack(drive));
		runAction(new GrabHatch(drive, hatchIntake));
		runAction(new TestTrajectory("closeSideHatchFromLoadingStation", drive));
		runAction(new TestTrajectory("145Right", drive));
		// runAction(new VisionTrack(drive));
		// runAction(new MoveElevator(elevator, 29000));
		// runAction(new PlaceHatch(drive, hatchIntake));
		DriverStation.reportWarning("successfully placed hatch", false);

	}

}