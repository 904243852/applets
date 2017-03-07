package pers.sunke.applets;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.Date;
import java.util.Scanner;

public class KeepWake {

	public final int PERIOD = 30, ALPHA = -1, RANGE = 100;

	private Thread thread, stopThread;

	private Robot robot;

	private int y, flag;

	public KeepWake() throws AWTException, InterruptedException {
		robot = new Robot();
		y = 100;
		flag = ALPHA;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					robot.mouseMove(100, y);
					flag *= ALPHA;
					y += RANGE * flag;
					try {
						Thread.sleep(1000 * PERIOD);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void start() throws InterruptedException {
		thread.start();
	}

	@SuppressWarnings("deprecation")
	public void exit() {
		System.out.println("waiting to exit.");
		thread.stop();
	}

	public void until(int delay) throws InterruptedException {
		stopThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					for (int i = delay; i > 0; i--) {
						System.out.print(String.format("exit in '%s' seconds.", i));
						Thread.sleep(1000);
						System.out.print("\r");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				exit();
			}
		});
		stopThread.start();
		thread.join();
	}

	public static void main(String[] args) throws InterruptedException, AWTException {
		KeepWake keepWake = new KeepWake();
		keepWake.start();

		if (args.length >= 3 && "-t".equals(args[1])) {
			int delay = Integer.parseInt(args[2]);
			Date stopTime = new Date(new Date().getTime() + delay * 1000);
			System.out.println(
					String.format("keep wake for \"%s\" seconds, this app will exit at \"%s\".", delay, stopTime));
			keepWake.until(delay);
		} else {
			System.out.println("input any text to exit.");
			new Scanner(System.in).next();
			keepWake.exit();
		}

	}
}