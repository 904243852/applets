package pers.sunke.applets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * generate markdown file with messages.xml in findsecbugs-plugin-*.jar
 */
public class FindsecbugsMarkdown {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

		args = new String[1];
		args[0] = "H:\\messages.xml";

		File file = new File(args[0]);

		if (file.exists() && !file.isDirectory()) {
			File md = new File(String.format("%s.md", file.getAbsolutePath()));
			OutputStream outputStream = new FileOutputStream(md);
			outputStream.write("# Bugs Patterns\n".getBytes("utf-8"));

			InputStream inputStream = new FileInputStream(file);
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(inputStream);

			Element messageCollection = doc.getDocumentElement();
			NodeList bugPatterns = messageCollection.getElementsByTagName("BugPattern");
			for (int i = 0, n = bugPatterns.getLength(); i < n; i++) {
				Element bugPattern = (Element) bugPatterns.item(i);
				String shortDescription = bugPattern.getElementsByTagName("ShortDescription").item(0).getTextContent()
						.replaceAll(" in \\{1\\}", "");
				String details = bugPattern.getElementsByTagName("Details").item(0).getTextContent();
				details = details.replaceAll("<pre>", "\r```java\n").replaceAll("</pre>", "\n\r```")
						.replaceAll("[\n\r]+<br/>[\n\r]+", "");
				String type = bugPattern.getAttribute("type");
				outputStream.write(String.format("\r## %s(%s)", shortDescription, type).getBytes("utf-8"));
				outputStream.write(details.getBytes("utf-8"));
			}
			outputStream.close();
		} else {
			System.out.println("error file path.");
		}
	}
}