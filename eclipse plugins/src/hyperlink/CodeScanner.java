package hyperlink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeScanner {

	protected Map<String, File> fileMap = new HashMap<String, File>();

	private void traversal(File directory) {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				traversal(files[i]);
			} else {
				fileMap.put(files[i].getPath(), files[i]);
			}
		}
	}

	public CodeScanner(File directory) {
		traversal(directory);
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

	protected List<String[]> match(String pattern, String content) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(content);
		List<String[]> results = new ArrayList<String[]>();
		while (m.find()) {
			String[] groups = new String[m.groupCount()];
			for (int i = 0; i < m.groupCount(); i++) {
				groups[i] = m.group(i + 1);
			}
			results.add(groups);
		}
		return results;
	}

	protected List<File> getFilesBySuffix(String suffix) {
		List<File> results = new ArrayList<File>();
		for (String filepath : fileMap.keySet()) {
			if ("*".equals(suffix) || filepath.endsWith(suffix)) {
				results.add(fileMap.get(filepath));
			}
		}
		return results;
	}

	protected void find(String suffix, String regex, FileIterator iterator) throws IOException {
		for (String filepath : fileMap.keySet()) {
			if ("*".equals(suffix) || filepath.endsWith(suffix)) {
				iterator.onFound(filepath, fileMap.get(filepath), match(regex, read(fileMap.get(filepath))));
			}
		}
	}

	protected void find(String regex, FileIterator iterator) throws IOException {
		find("*", regex, iterator);
	}

	public interface FileIterator {
		public void onFound(String path, File file, List<String[]> results);
	}
}