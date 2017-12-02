
package org.usfirst.frc.team3470.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.CameraServer;

import org.usfirst.frc.team3470.robot.commands.ExampleCommand;
import org.usfirst.frc.team3470.robot.subsystems.ExampleSubsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	RobotDrive myRobot;
	Joystick joystick;
	XboxController controller;
    Timer timer;
    Timer shooterTimer;
    Timer autoTimer;
    
    DigitalInput light;
    DigitalInput light2;
    Spark armOne;
    Spark armTwo;
    
    Victor shooter;
    Victor loader;
	
	@Override
	public void robotInit() {
		oi = new OI();
		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
		myRobot = new RobotDrive(0,2,1,3);
		myRobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
		myRobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
		controller = new XboxController(0);
		joystick = new Joystick(1);
		timer = new Timer();
		timer.start();
		shooterTimer = new Timer();
		shooterTimer.start();
		
		autoTimer = new Timer();
		autoTimer.start();
		
		light = new DigitalInput(0);
		light2 = new DigitalInput(1);
		armOne = new Spark(4);
		armTwo = new Spark(5);
		
		shooter = new Victor(6);
		loader = new Victor(7);
		
		CameraServer.getInstance().startAutomaticCapture();
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	boolean driveStraight = false;
	@Override
	public void autonomousInit() {
		autonomousCommand = chooser.getSelected();
		autoTimer.reset();
		driveStraight = false;

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	double step = 0;
	int autoDir = 1;
	boolean driveYet = false;
	@Override
	public void autonomousPeriodic() {
		if (autoTimer.get() < 4) myRobot.mecanumDrive_Cartesian(0, -0.25, 0, 0);
		else myRobot.mecanumDrive_Cartesian(0, 0, 0, 0);
		/*if (SmartDashboard.getString("startPoint", "Center") == "Center") {
			if (autoTimer.get() < 0.5) {
				myRobot.mecanumDrive_Cartesian(0, 0.5, 0, 0);
				//driveYet = true;
			}
			if (SmartDashboard.getNumber("score", 0) > 85) { //Check if there is anything that is very likely the target.
	
				if (SmartDashboard.getNumber("rMid", 0) < SmartDashboard.getNumber("hMid", 0) - 15) { // If the center of the target is left of Autonomous Middle Point + a buffer of 25...
					myRobot.mecanumDrive_Cartesian(-0.5, 0, 0, 0); //Strafe Right
					autoDir = 1; // Make the robot turn left if it loses track of the target
				}
				else if (SmartDashboard.getNumber("rMid", 0) > SmartDashboard.getNumber("hMid", 0) + 15) { // If the center of the target is right of Autonomous Middle Point + a buffer of 25...
					myRobot.mecanumDrive_Cartesian(0.5, 0, 0, 0); //Strafe Left
					autoDir = -1; // Make the robot turn right if it loses track of the target
				}
				else {
					myRobot.mecanumDrive_Cartesian(0, -0.25, 0, 0); // Drive forward if the target is there.
				}
				if (SmartDashboard.getNumber("rYMid", 0) > 155) driveStraight = true;
			}
			else if (driveStraight) myRobot.mecanumDrive_Cartesian(0, -0.2, 0, 0);
			else {
				myRobot.mecanumDrive_Cartesian(0, 0, 0.25*autoDir, 0); // Turn at a moderate speed if you have no idea where the target is.
			}
		}
		else if (SmartDashboard.getString("startPoint", "Center") == "Right") {
			if (autoTimer.get() < 0.5) {
				myRobot.mecanumDrive_Cartesian(0, 1, SmartDashboard.getNumber("Turn lvl", 0.3), 0);
				driveYet = true;
			}
			if (SmartDashboard.getNumber("score", 0) > 85) { //Check if there is anything that is very likely the target.
				
				if (SmartDashboard.getNumber("rMid", 0) < SmartDashboard.getNumber("hMid", 0) - 15) { // If the center of the target is left of Autonomous Middle Point + a buffer of 25...
					myRobot.mecanumDrive_Cartesian(-0.5, 0, 0, 0); //Strafe Right
					autoDir = 1; // Make the robot turn left if it loses track of the target
				}
				else if (SmartDashboard.getNumber("rMid", 0) > SmartDashboard.getNumber("hMid", 0) + 15) { // If the center of the target is right of Autonomous Middle Point + a buffer of 25...
					myRobot.mecanumDrive_Cartesian(0.5, 0, 0, 0); //Strafe Left
					autoDir = -1; // Make the robot turn right if it loses track of the target
				}
				else {
					myRobot.mecanumDrive_Cartesian(0, -0.25, 0, 0); // Drive forward if the target is there.
				}
				if (SmartDashboard.getNumber("rYMid", 0) > 155) driveStraight = true;
			}
			else if (driveStraight) myRobot.mecanumDrive_Cartesian(0, -0.2, 0, 0);
			else {
				myRobot.mecanumDrive_Cartesian(0, 0, 0.25*autoDir, 0); // Turn at a moderate speed if you have no idea where the target is.
			}
		}
		else if (SmartDashboard.getString("startPoint", "Center") == "Left") {
			if (autoTimer.get() < 0.5) {
				myRobot.mecanumDrive_Cartesian(0, 1, -SmartDashboard.getNumber("Turn lvl", 0.3), 0);
				//driveYet = true;
			}
			if (SmartDashboard.getNumber("score", 0) > 85) { //Check if there is anything that is very likely the target.
				
				if (SmartDashboard.getNumber("rMid", 0) < SmartDashboard.getNumber("hMid", 0) - 15) { // If the center of the target is left of Autonomous Middle Point + a buffer of 25...
					myRobot.mecanumDrive_Cartesian(-0.5, 0, 0, 0); //Strafe Right
					autoDir = 1; // Make the robot turn left if it loses track of the target
				}
				else if (SmartDashboard.getNumber("rMid", 0) > SmartDashboard.getNumber("hMid", 0) + 15) { // If the center of the target is right of Autonomous Middle Point + a buffer of 25...
					myRobot.mecanumDrive_Cartesian(0.5, 0, 0, 0); //Strafe Left
					autoDir = -1; // Make the robot turn right if it loses track of the target
				}
				else {
					myRobot.mecanumDrive_Cartesian(0, -0.25, 0, 0); // Drive forward if the target is there.
				}
				if (SmartDashboard.getNumber("rYMid", 0) > 155) driveStraight = true;
			}
			else if (driveStraight) myRobot.mecanumDrive_Cartesian(0, -0.2, 0, 0);
			else {
				myRobot.mecanumDrive_Cartesian(0, 0, -0.25*autoDir, 0); // Turn at a moderate speed if you have no idea where the target is.
			}
		}
		else if (SmartDashboard.getString("autoSelection", "lazyCenter") == "lazyCenter") {
			if (autoTimer.get() < 3) myRobot.mecanumDrive_Cartesian(0, -0.25, 0, 0);
			else myRobot.mecanumDrive_Cartesian(0, 0, 0, 0);
		}
		else {
			if (autoTimer.get() < 3) myRobot.mecanumDrive_Cartesian(0, -0.25, 0, 0);
			else myRobot.mecanumDrive_Cartesian(0, 0, 0, 0);
		}*/
	}

	boolean shotToggle = false;

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		timer.reset();
		shooterTimer.reset();
		shotToggle = false;
		if (autonomousCommand != null)
			autonomousCommand.cancel();
	}

	/**
	 * This function is called periodically during operator control
	 */
	double LX = 0;
	double LY = 0;
	double RX = 0;
	double RY = 0;
	double strafeSpeed = 0;
	double LDeadZone = 0;
	double RDeadZone = 0;
	double LDist = 0;
	double RDist = 0;
	//boolean LB = false;
	//boolean RB = false;
	//double speedValue = 0.5;
	//double speedIncrement = 0;
	//boolean rJoystick = false;
	boolean spinning = false;
	boolean spinning90 = false;
	//boolean sFwd = false;
	//int climbSpeed = 0;
	//int dPadAngle = 0;
	boolean AB = false;
	boolean XB = false;
	//double[] wheelSpeeds = new double[4];
	double shootSpeed = 1;
	int loadDir = -1;
	boolean shotButton = false;

	@Override
	public void teleopPeriodic() {
		//Scheduler.getInstance().run();
		LDeadZone = SmartDashboard.getNumber("LDeadZone", 0.25);
		RDeadZone = SmartDashboard.getNumber("RDeadZone", 0.25);
		//LX = controller.getX(GenericHID.Hand.kLeft);
		LY = controller.getY(GenericHID.Hand.kLeft);
		RX = controller.getX(GenericHID.Hand.kRight);
		//RY = controller.getY(GenericHID.Hand.kRight);
		strafeSpeed = controller.getTriggerAxis(GenericHID.Hand.kRight) - controller.getTriggerAxis(GenericHID.Hand.kLeft);
		//LB = controller.getBumper(GenericHID.Hand.kLeft);
		//RB = controller.getBumper(GenericHID.Hand.kRight);
		AB = controller.getAButton();
		XB = controller.getXButton();
		//LDist = Math.sqrt( Math.pow(LX, 2) + Math.pow(LY, 2) );
		//RDist = Math.sqrt( Math.pow(RX, 2) + Math.pow(RY, 2) );
		//rJoystick = controller.getStickButton(GenericHID.Hand.kRight);
		
		//Speed Controls
		/*if (controller.getBumper(GenericHID.Hand.kLeft)) {
			LB = true;
			if (!LB && speedValue > 0.25) {
				speedValue = speedValue - 0.25;
			}
		}
		else LB = false;
		if (controller.getBumper(GenericHID.Hand.kRight)) {
			RB = true;
			if (!RB && speedValue < 1) {
				speedValue = speedValue + 0.25;
			}
		}
		else RB = false;*/
		//speedIncrement = speedValue + (controller.getTriggerAxis(GenericHID.Hand.kRight) - controller.getTriggerAxis(GenericHID.Hand.kLeft))*0.1;
		//SmartDashboard.putNumber("speedValue", speedValue);
		
		//Drive Controls
		if (Math.abs(LY) < LDeadZone) LY = 0;
		if (Math.abs(RX) < RDeadZone) RX = 0;
		if (LY + RX + strafeSpeed != 0) {
			myRobot.mecanumDrive_Cartesian(0, LY, RX * 0.65, 0);
		}

		// Here is your spinny code. There we go~
		if (controller.getStickButton(GenericHID.Hand.kRight)) {
			timer.reset();
			spinning = true;
		}
		if (spinning == true) {
			myRobot.mecanumDrive_Cartesian(0, 0, 1, 0);
		}
		if (timer.get() > 0.15) {
			spinning = false;
		}
		// Here is your spinny code. There we go~
		if (controller.getStickButton(GenericHID.Hand.kLeft)) {
			timer.reset();
			spinning90 = true;
		}				
		if (spinning90 == true) {
			myRobot.mecanumDrive_Cartesian(0, 0, 1, 0);
		}
		if (timer.get() > 0.075) {					
			spinning90 = false;
		}
		
		SmartDashboard.putBoolean("Light", light.get());
		SmartDashboard.putBoolean("Light 2", light2.get());

		//Arm Controls
		if (controller.getStartButton()) {
			armOne.set(-1);
			armTwo.set(-1);
		}
		else if (controller.getBackButton()) {
			armOne.set(-0.5);
			armTwo.set(-0.5);
		}
		else {
			armOne.set(0);
			armTwo.set(0);
		}
	
		/*dPadAngle = controller.getPOV();
		if (RDist < RDeadZone && LDist < LDeadZone) {
			if (dPadAngle >= 315 || (dPadAngle < 45 && dPadAngle >= 0)) {
				myRobot.mecanumDrive_Cartesian(0, -0.65, 0, 0);
			}
			else if (dPadAngle >= 45 && dPadAngle < 135) {
				myRobot.mecanumDrive_Cartesian(0, 0, 0.65, 0);
			}
			else if (dPadAngle >= 135 && dPadAngle < 225) {
				myRobot.mecanumDrive_Cartesian(0, 0.65, 0, 0);
			}
			else if (dPadAngle >= 225 && dPadAngle < 315) {
				myRobot.mecanumDrive_Cartesian(0, 0, -0.65, 0);
			}
		}*/
		
		//Shooter Controls
		shootSpeed = -joystick.getThrottle()/4+0.75;
		
		if (joystick.getTrigger()) {
			loadDir = -1;
		}
		else {
			loadDir = 0;
		}
		if (joystick.getRawButton(2)) {
			if (!shotButton) {
				if (shotToggle) shotToggle = false;
				else shotToggle = true;
				shotButton = true;
			}
		}
		else shotButton = false;
		if (!shotToggle) {
			if (joystick.getRawButton(3)) {
				shootSpeed = -1;
				loadDir = 1;
			}
			else shootSpeed = 0;
		}
		shooter.set(shootSpeed);
		loader.set(loadDir);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
} 


