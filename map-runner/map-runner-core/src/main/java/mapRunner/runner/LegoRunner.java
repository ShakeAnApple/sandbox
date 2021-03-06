package mapRunner.runner;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class LegoRunner implements Runner {

	final static int ForwardSpeed = 300;
	final static int RotationSpeed = 100;

	public void printData(float[] data) {
		GraphicsLCD lcd = ev3.getGraphicsLCD();
		lcd.setFont(Font.getSmallFont());

		lcd.clear();
		for (int i = 0; i < data.length; ++i) {
			lcd.drawString(String.format("%.2f", data[i]), lcd.getWidth() / 2, i * 10, GraphicsLCD.HCENTER);
		}
		lcd.refresh();
	}

	public void moveForward() {
		rightMotor.setSpeed(ForwardSpeed);
		leftMotor.setSpeed(ForwardSpeed);
		leftMotor.forward();
		rightMotor.forward();
	}

	public void stopMoving() {
		leftMotor.stop(true);
		rightMotor.stop(true);
	}

	public void turnDirection(Direction direction) {
		rightMotor.setSpeed(RotationSpeed);
		leftMotor.setSpeed(RotationSpeed);
		if (direction == Direction.LEFT) {
			rightMotor.forward();
			leftMotor.backward();
		} else {
			leftMotor.forward();
			rightMotor.backward();
		}
	}

	public LegoRunner() {
		ev3 = BrickFinder.getLocal();
	}

	Brick ev3;

	EV3ColorSensor colorSensor;
	SampleProvider colorIdSensor;

	RegulatedMotor leftMotor;
	RegulatedMotor rightMotor;

	public boolean isMarker(float[] data) {
		int colorId = (int) data[0];
		switch (colorId) {
		case lejos.robotics.Color.BLACK:
			return true;
		case lejos.robotics.Color.GREEN:
			return true;
		}
		return false;
	}

	public boolean isSignalMarker(float[] data) {
		int colorId = (int) data[0];
		switch (colorId) {
		case lejos.robotics.Color.GREEN:
			return true;
		}
		return false;
	}

	@Override
	public void rotate(int rotationNumber) {
		{
			int controlTime = 150;
			int rotationCounter = 0;
			boolean rotationFlag = false;
			Direction moveDirection = Direction.RIGHT;
			if (rotationNumber > 0) {
				moveDirection = Direction.LEFT;
			} else {
				moveDirection = Direction.RIGHT;
			}

			while (!ev3.getKey("Escape").isDown()) {
				float[] colorData = new float[colorIdSensor.sampleSize()];
				colorIdSensor.fetchSample(colorData, 0);
				/*
				 * проверка цветом счётчик вместо флага
				 */
				if (rotationCounter == Math.abs(rotationNumber)) {
					break;
				}

				if (!(isMarker(colorData))) {
					rotationFlag = true;
				}

				if (isMarker(colorData)) {
					if (rotationFlag) {
						rotationCounter++;
						rotationFlag = false;
					}
				}
				/*
				 * проверка цветом
				 */

				turnDirection(moveDirection);
				Delay.msDelay(controlTime);
			}
		}
		stopMoving();
	}

	@Override
	public void move(int signalMarkAmount) {
		leftMotor.setSpeed(ForwardSpeed);
		rightMotor.setSpeed(ForwardSpeed);

		Direction lastDirection = Direction.RIGHT;
		int turningTime = 1000;

		boolean turningMode = false;
		boolean alarmFlag = false;
		long startTime = 0;
		long signalMarkCounter = 0;

		while ((!ev3.getKey("Escape").isDown()) & (signalMarkCounter < signalMarkAmount)) {
			long currentTime = System.currentTimeMillis();

			float[] data = new float[colorIdSensor.sampleSize()];
			colorIdSensor.fetchSample(data, 0);

			printData(data);

			if (isMarker(data)) {
				if (turningMode) {
					stopMoving();
				}

				/* Проверка на прохождение метки */
				if (isSignalMarker(data)) {
					alarmFlag = true;
				} else {
					if (alarmFlag) {
						alarmFlag = false;
						signalMarkCounter++;
					}
				}

				moveForward();
				turningTime = 200;
				turningMode = false;
				startTime = currentTime;
			} else {
				if (currentTime - startTime > turningTime) {
					stopMoving();

					if (turningMode) {
						if (lastDirection == Direction.RIGHT) {
							lastDirection = Direction.LEFT;
						} else {
							lastDirection = Direction.RIGHT;
						}
					} else {
						turningMode = true;
					}

					startTime = currentTime;
					turningTime += 300;
					turnDirection(lastDirection);
					turningMode = true;
				}
			}
		}

		stopMoving();
	}

	@Override
	public void stop() {
		colorSensor.close();
		leftMotor.close();
		rightMotor.close();
	}

	@Override
	public void start() {
		colorSensor = new EV3ColorSensor(ev3.getPort("S1"));
		colorIdSensor = colorSensor.getColorIDMode();

		leftMotor = new EV3LargeRegulatedMotor(ev3.getPort("D"));
		rightMotor = new EV3LargeRegulatedMotor(ev3.getPort("A"));
	}
}
