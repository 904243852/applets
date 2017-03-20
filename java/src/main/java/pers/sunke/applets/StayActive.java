package pers.sunke.applets;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.util.Date;

public class StayActive {

	public final int PERIOD = 60 * 5, RANGE = 1;

	private Thread thread, stopThread;

	private Robot robot;

	public StayActive() throws AWTException, InterruptedException {
		robot = new Robot();
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println(
						String.format("%s: applet 'stay active' started, the mouse will wheel every %s seconds.",
								new Date(), PERIOD));
				while (true) {
					robot.mouseWheel(RANGE);
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
		thread.stop();
		System.out.println(String.format("%s: applet 'stay active' has been stopped and exit.", new Date()));
	}

	public void exitUntil(int delay) throws InterruptedException {
		stopThread = new Thread(new Runnable() {

			@Override
			public void run() {
				Date stopTime = new Date(new Date().getTime() + delay * 1000);
				System.out
						.println(String.format("%s: applet 'stay active' will run for %s seconds, stop and exit at %s.",
								new Date(), delay, stopTime));
				try {
					for (int i = delay; i > 0; i--) {
						System.out.println(String.format("applet 'stay active' will stop in %s seconds.", i));
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

	public static void main(String[] args) throws InterruptedException, AWTException, IOException {
		StayActive activer = new StayActive();
		activer.start();

		if (args.length >= 2 && "-t".equals(args[0])) {
			int delay = Integer.parseInt(args[1]);
			activer.exitUntil(delay);
		} else {
			Thread.sleep(500);
			System.out.println("input any text to exit.");
			System.in.read();
			activer.exit();
		}

	}
}