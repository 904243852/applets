package hyperlink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

public class DetectorHelper {

	/**
	 * 获取点击选中链接的内容
	 * 
	 * @param region
	 * @param document
	 * @return
	 * @throws BadLocationException
	 */
	public static String getSelectString(IRegion region, IDocument document) throws BadLocationException {
		int offset = region.getOffset();
		IRegion lineInfo = document.getLineInformationOfOffset(offset);
		String line = document.get(lineInfo.getOffset(), lineInfo.getLength());
		return line;
	}

	public static File getProjectDirectory(IDocument document) {
		IPath c = DetectorHelper.getFile(document);
		IPath r = DetectorHelper.getWorkspace().getRoot().getLocation();
		String d = c.makeRelativeTo(r).toString().split("/")[0];
		File p = r.makeAbsolute().append(d).toFile();
		return p;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static IPath getFile(IDocument document) {
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(document);
		return textFileBuffer.getLocation();
	}

	public static IEditorPart openEditor(File file) {
		String editorId = getEditorId(file);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IFileStore fileStore;
		try {
			fileStore = EFS.getLocalFileSystem().getStore(new Path(file.getCanonicalPath()));
			IEditorInput input = new FileStoreEditorInput(fileStore);
			return page.openEditor(input, editorId);
		} catch (IOException e) {
			System.err.println("Could not get canonical file path: " + e);
		} catch (CoreException e) {
			System.err.println("Could not get canonical file path: " + e);
		}
		return null;
	}

	private static String getEditorId(File file) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(file.getName(), getContentType(file));
		if (descriptor != null) {
			return descriptor.getId();
		}
		return EditorsUI.DEFAULT_TEXT_EDITOR_ID;
	}

	private static IContentType getContentType(File file) {
		if (file == null) {
			return null;
		}

		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			return Platform.getContentTypeManager().findContentTypeFor(stream, file.getName());
		} catch (IOException e) {
			System.err.println("Operation failed: " + e);
			return null;
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				System.err.println("Operation failed: " + e);
			}
		}
	}
}
