package pers.sunke.applets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 暴力破解工具
 */
public class ViolentCrack {

	private static char[] CHAR_DICTIONARY = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*_"
			.toCharArray();

	public void post(String urlString, String formContent) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);

		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.print(formContent);
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		System.out.println(sb);
	}

	public static void main(String[] args) throws IOException {
		ViolentCrack violentCrack = new ViolentCrack();

		// String urlString = args[1];
		String urlString = "http://localhost:8080/sso/login";
		String formContent = "username=admin&password=123";

		violentCrack.post(urlString, formContent);
	}
}
