package pers.sunke.applets;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.Date;
import java.util.Scanner;

public class KeepWake {

	private Thread thread, stopThread;

	private Robot robot;

	private int y, flag;

	private boolean needRun;

	public KeepWake() throws AWTException, InterruptedException {
		robot = new Robot();
		y = 100;
		flag = -1;
		needRun = true;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (needRun) {
					robot.mouseMove(100, y);
					flag *= -1;
					y += 100 * flag;
					try {
						Thread.sleep(1000 * 5);
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

	public void until(long delay) throws InterruptedException {
		stopThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000 * delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("time up! waiting to exit.");
				needRun = false;
			}
		});
		stopThread.start();
		thread.join();
	}

	public static void main(String[] args) throws InterruptedException, AWTException {
		KeepWake keepWake = new KeepWake();
		keepWake.start();

		if ("-t".equals(args[1])) {
			long delay = Long.parseLong(args[2]);
			Date stopTime = new Date(new Date().getTime() + delay);
			System.out.println(
					String.format("keep wake for \"%s\" seconds, this app will exit at \"%s\".", delay, stopTime));
			keepWake.until(delay);
		} else {
			System.out.println("press any key to exit.");
			new Scanner(System.in).next();
		}

	}
}