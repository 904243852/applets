package pers.sunke.securityaudit.policy.writebox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import pers.sunke.securityaudit.domain.CodeVulnerability;

public class InsecureEncryptionAlgorithmPolicy extends Policy {

	@Override
	public boolean needAnalysised(File file) {
		if (file.getName().endsWith("java"))
			return true;
		return false;
	}

	@Override
	public List<CodeVulnerability> analysis(File file) throws IOException {
		String content = read(file);

		List<Entry<Integer, String>> result = match("\"MD2\"|\"MD4\"|\"MD5\"", content);
		List<CodeVulnerability> vulnerabilitys = null;
		if (result.size() > 0) {
			vulnerabilitys = new ArrayList<CodeVulnerability>();
			vulnerabilitys.add(new CodeVulnerability(file, result));
		}
		return vulnerabilitys;
	}

	@Override
	public List<CodeVulnerability> analysis(String content) {
		return null;
	}

}
