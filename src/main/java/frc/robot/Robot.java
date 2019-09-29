/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.auto.modes.*;
import frc.robot.auto.util.*;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.controllers.PlasmaJoystick;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends TimedRobot {

    PlasmaJoystick joystick;
    PlasmaJoystick HABjoystick;
    DriveTrain driveTrain;
    HatchIntake hatchIntake;
    Elevator elevator;
    CargoIntake cargoIntake;
    HAB hab;

    Compressor compressor;

    AutoModeRunner autoModeRunner;
    AutoMode[] autoModes;
    int autoModeSelection;

    NetworkTable table;
    NetworkTableEntry tx;
    NetworkTableEntry ty;
    NetworkTableEntry ta;

    // CameraServer server;

    double elevatorTarget;
    double pivotTarget;
    double vision_X;
    double vision_Y;
    double vision_Area;

    @Override
    public void robotInit() {

        joystick = new PlasmaJoystick(Constants.JOYSTICK1_PORT);
        HABjoystick = new PlasmaJoystick(1);

        driveTrain = new DriveTrain(Constants.L_DRIVE_ID, Constants.L_DRIVE_MID_SLAVE_ID,
                Constants.L_DRIVE_FRONT_SLAVE_ID, Constants.R_DRIVE_ID, Constants.R_DRIVE_MID_SLAVE_ID,
                Constants.R_DRIVE_FRONT_SLAVE_ID);

        hatchIntake = new HatchIntake(Constants.CLAW_PISTON_ID, Constants.BACK_EXTENDER_ID,
                Constants.FRONT_EXTENDER_ID);

        elevator = new Elevator(Constants.L_ELEVATOR_ID, Constants.R_ELEVATOR_ID, Constants.ELEVATOR_LIMIT_ID);

        cargoIntake = new CargoIntake(Constants.PIVOT_ID, Constants.INTAKE_ID, Constants.PIVOT_LIMIT_ID,
                Constants.CAMERA_SERVO_ID);

        hab = new HAB(Constants.L_HAB_ARM_ID, Constants.R_HAB_ARM_ID, Constants.L_HAB_ELEVATOR_ID,
                Constants.R_HAB_ELEVATOR_ID, Constants.HAB_DRIVE, Constants.HAB_ELEVATOR_LIMIT_ID,
                Constants.HAB_ARM_LIMIT_ID, driveTrain);

        compressor = new Compressor(0);
        compressor.setClosedLoopControl(true);

        table = NetworkTableInstance.getDefault().getTable("limelight");
        tx = table.getEntry("tx");
        ty = table.getEntry("ty");
        ta = table.getEntry("ta");

        autoModeRunner = new AutoModeRunner();
        autoModes = new AutoMode[10];
        for (int i = 0; i < autoModes.length; i++) {
            autoModes[i] = new Nothing();
        }

        DriverStation.reportWarning("automode generated", false);
        autoModeSelection = 0;
        SmartDashboard.putNumber("Auto Mode", 0);

        driveTrain.resetEncoders();
        driveTrain.zeroGyro();

        CameraServer.getInstance().startAutomaticCapture("USB camera", 0);

        elevatorTarget = 0;
        pivotTarget = 0;
    }

    @Override
    public void robotPeriodic() {
        SmartDashboard.putNumber("GyroAngle", driveTrain.getGyroAngle());
        SmartDashboard.putNumber("Gyro pitch", driveTrain.getGyroPitch());

        vision_X = tx.getDouble(0.0);
        vision_Y = ty.getDouble(0.0);
        vision_Area = ta.getDouble(0.0);

        SmartDashboard.putNumber("LimelightX", vision_X);
        SmartDashboard.putNumber("LimelightY", vision_Y);
        SmartDashboard.putNumber("LimelighArea", vision_Area);

        // SmartDashboard.putBoolean("HAB Limit", hab.HABElevatorLimit.get());

    }

    public void disabledInit() {
        // compressor.start();
        autoModeRunner.stop();
        driveTrain.zeroGyro();
    }

    public void disabledPeriodic() {
        autoModeSelection = (int) SmartDashboard.getNumber("Auto Mode", 0);
    }

    @Override
    public void autonomousInit() {

        DriverStation.reportWarning("starting auto", false);
        driveTrain.resetEncoders();
        // compressor.start();
        driveTrain.zeroGyro();

        autoModes[0] = new CloseSideHatch(driveTrain, hatchIntake);

        autoModeRunner.chooseAutoMode(autoModes[0]);
        autoModeRunner.start();
    }

    @Override
    public void autonomousPeriodic() {
        driveTrain.getDistance();

    }

    @Override
    public void teleopPeriodic() {
        newDriverControls(joystick);
        // HABControls(HABjoystick);
    }

    public void newDriverControls(PlasmaJoystick joystick) {
        if (cargoIntake.getIsPivotUp() && joystick.A.isPressed()) {
            visionApproach();
        } else {
            driveTrain.FPSDrive(joystick.LeftY, joystick.RightX);
        }

        if (cargoIntake.getIsPivotUp()) { // hatch requirements
            if (joystick.dPad.getPOV() == 0) {
                hatchIntake.fullExtend();
            }
            if (joystick.dPad.getPOV() == 90 || joystick.dPad.getPOV() == 270) {
                hatchIntake.halfExtend();
            }
            if (joystick.dPad.getPOV() == 180) {
                hatchIntake.fullRetract();
            }

            if (joystick.L3.isPressed()) {
                hatchIntake.releaseHatch();
            }
            if (joystick.R3.isPressed()) {
                hatchIntake.grabHatch();
            }
        }

        if (!hatchIntake.getIsClamped()) { // pivot out requirements
            if (joystick.Y.isToggledOn()) {
                hatchIntake.fullRetract();
                hatchIntake.grabHatch();
                pivotTarget = 4000;
            }
        }

        if (!cargoIntake.getIsPivotUp()) { // pivot in requirements
            if (!joystick.Y.isToggledOn()) {
                pivotTarget = -200;
            }
        }
        cargoIntake.motionMagicPivot(pivotTarget, hatchIntake);

        if (joystick.RB.isPressed()) {
            cargoIntake.intakeCargo(-1);
        } else {
            cargoIntake.intakeCargo(0);
        }

        if (cargoIntake.getIsPivotUp()) {
            if ((joystick.LT.isPressed() && joystick.RT.isOffToOn())
                    || (joystick.RT.isPressed() && joystick.LT.isOffToOn())) {
                hatchIntake.grabHatch();
                elevatorTarget = 29000;
                DriverStation.reportWarning("Middle", false);
            } else if (joystick.LB.isPressed()) {
                hatchIntake.grabHatch();
                elevatorTarget = 13000;
                DriverStation.reportWarning("Cargo", false);
            } else {
                if (joystick.LT.isOffToOn()) {
                    elevatorTarget = -200;
                    DriverStation.reportWarning("Low", false);
                }
                if (joystick.RT.isOffToOn()) {
                    hatchIntake.grabHatch();
                    elevatorTarget = 56000;
                    DriverStation.reportWarning("High", false);
                }
            }
        }
        elevator.magicElevator(elevatorTarget);

        if (cargoIntake.getIsPivotUp()) {
            if (joystick.START.isPressed()) {
                hatchIntake.fullRetract();
                hatchIntake.grabHatch();
                hab.GyroHABClimb();
            } else if (joystick.BACK.isPressed()) {
                hatchIntake.releaseHatch();
                hab.lowerRobot(1);
                hab.GyroClimbReset();
            } else {
                hab.raiseRobot(0);
                hab.lowerRobot(0);
            }

            if (joystick.B.isPressed()) {
                hab.HABForward(0.5);
            } else if (joystick.X.isPressed()) {
                hab.HABForward(-0.5);
            } else {
                hab.HABForward(0);
            }

        }
    }

    public void HABControls(PlasmaJoystick joystick) {
        if (joystick.A.isPressed()) {
            hab.ArmsUp();
        }
        if (joystick.B.isPressed()) {
            hab.ArmsDown();
        }
    }

    public void visionApproach() {
        if (vision_Area == 0) {
            driveTrain.FPSDrive(0, 0);
        } else {
            double turnVal = vision_X / 25;
            turnVal = Math.min(turnVal, .4);
            turnVal = Math.max(-.4, turnVal);
            double forwardVal = .3;
            driveTrain.FPSDrive(forwardVal, turnVal);
        }

    }

    @Override
    public void testPeriodic() {
    }
}
