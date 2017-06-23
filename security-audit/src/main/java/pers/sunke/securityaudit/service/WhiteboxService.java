package pers.sunke.securityaudit.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.sunke.securityaudit.domain.CodeVulnerability;
import pers.sunke.securityaudit.policy.writebox.InsecureEncryptionAlgorithmPolicy;
import pers.sunke.securityaudit.policy.writebox.Policy;

public class WhiteboxService {

	private List<Policy> policies = new ArrayList<Policy>();

	public WhiteboxService() {
		policies.add(new InsecureEncryptionAlgorithmPolicy());
	}

	private void traversal(File directory, FileIterator iterator) throws IOException {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				traversal(files[i], iterator);
			} else {
				iterator.analysisFile(files[i]);
			}
		}
	}

	public interface FileIterator {
		public void analysisFile(File file) throws IOException;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public void scan(String directory) throws IOException {

		final List<CodeVulnerability> vulnerabilitys = new ArrayList<CodeVulnerability>();

		traversal(new File(directory), new FileIterator() {

			public void analysisFile(File file) throws IOException {

				for (Policy policy : policies) {
					if (policy.needAnalysised(file)) {
						// System.out.println(String.format("analysising file: %s(%s)", file.getName(), file.getCanonicalPath()));
						List<CodeVulnerability> vs = policy.analysis(file);
						if (null != vs && 0 < vs.size())
							vulnerabilitys.addAll(vs);
					}
				}
			}
		});

		System.out.println(vulnerabilitys);
	}
}
