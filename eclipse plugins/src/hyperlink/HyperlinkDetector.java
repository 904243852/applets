package hyperlink;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import hyperlink.CodeScanner.FileIterator;

public class HyperlinkDetector extends AbstractHyperlinkDetector {

	private static CodeScanner scanner = null;

	private static String currentProject = null;

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer paramITextViewer, IRegion paramIRegion, boolean paramBoolean) {
		IDocument document = paramITextViewer.getDocument();
		try {
			String line = DetectorHelper.getSelectString(paramIRegion, document);
			String filename = DetectorHelper.getFile(document).toFile().getName();

			File project = DetectorHelper.getProjectDirectory(document);
			if (null == currentProject || !currentProject.equals(project.getName())) {
				currentProject = project.getName();
				scanner = new CodeScanner(project);
			}

			scanner.find("*", filename, new FileIterator() {

				@Override
				public void onFound(String path, File file, List<String[]> results) {
					// TODO
					System.out.println(path);
					// DetectorHelper.openEditor(file);
				}
			});

		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}