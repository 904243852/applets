package pers.sunke.securityaudit.policy.writebox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pers.sunke.securityaudit.domain.CodeVulnerability;
import pers.sunke.securityaudit.domain.TupleEntry;

public abstract class Policy {

	private Pattern lp = Pattern.compile("\n");

	private int getLine(String str) {
		int l = 1;
		if (str == null) {
			return l;
		}
		Matcher m = lp.matcher(str);
		while (m.find()) {
			l++;
		}
		return l;
	}

	/**
	 * 
	 * @param pattern
	 * @param content
	 * @return
	 */
	protected List<Entry<Integer, String>> match(String pattern, String content) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(content);
		List<Entry<Integer, String>> results = new ArrayList<Entry<Integer, String>>();
		while (m.find()) {
			results.add(new TupleEntry<Integer, String>(getLine(content.substring(0, m.start())), m.group()));
		}
		return results;
	}

	protected String read(File file) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		FileInputStream fileInputStream = new FileInputStream(file);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
		try {
			int ch = 0;
			while ((ch = inputStreamReader.read()) != -1) {
				stringBuilder.append((char) ch);
			}
		} catch (IOException exception) {
			throw exception;
		} finally {
			inputStreamReader.close();
		}
		return stringBuilder.toString();
	}

	public List<CodeVulnerability> analysis(File file) throws IOException {
		String content = read(file);
		return analysis(content);
	}

	public abstract boolean needAnalysised(File file);

	public abstract List<CodeVulnerability> analysis(String content);
}
