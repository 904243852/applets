package pers.sunke.applets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class BootstrapTest {

	@Test
	public void bootstrap() throws InterruptedException, IOException {
		// String[] commands = { "java -jar console.jar", "", "" };
		String[] commands = { "ipconfig", "", "" };
		Process process = Runtime.getRuntime().exec(commands);
		process.waitFor();
		InputStream inputStream = process.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line);
		}
	}

}