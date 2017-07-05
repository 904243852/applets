package pers.sunke.applets;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class httpd {
	private ServerSocket serverSocket;

	public void startServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("httpd startup on port " + port);
			ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
					new ArrayBlockingQueue<Runnable>(5));

			while (true) {
				Socket socket = serverSocket.accept();
				executor.submit(new Processor(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] argv) throws Exception {
		httpd server = new httpd();
		if (argv.length == 1) {
			server.startServer(Integer.parseInt(argv[0]));
		} else {
			server.startServer(80);
		}
	}

	static class Processor implements Runnable {
		private PrintStream output;
		private InputStream input;

		public Processor(Socket socket) {
			try {
				input = socket.getInputStream();
				output = new PrintStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				String fileName = parse(input);
				readFile(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private String parse(InputStream input) throws IOException {
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			String inputContent = in.readLine();
			if (inputContent == null || inputContent.length() == 0) {
				sendError(400, "Client invoke error");
				return null;
			}

			String request[] = inputContent.split(" ");
			if (request.length != 3) {
				sendError(400, "Client invoke error");
				return null;
			}
			String method = request[0];
			String fileName = request[1];
			String httpVersion = request[2];
			fileName = URLDecoder.decode(fileName, "UTF-8");
			System.out.println("Method: " + method + ", file name: " + fileName + ", HTTP version: " + httpVersion);
			return fileName;
		}

		private void readFile(String fileName) throws IOException {
			File file = new File(fileName);
			if (!file.exists()) {
				sendError(404, "File Not Found: " + file.getCanonicalPath());
				return;
			}
			byte content[] = null;
			if (file.isFile()) {
				InputStream in = new FileInputStream(file);
				content = new byte[(int) file.length()];
				in.read(content);
				in.close();
			} else {
				File[] files = file.listFiles();
				StringBuilder sb = new StringBuilder("<!DOCTYPE html><head><meta charset=\"UTF-8\"></head><body>");
				for (File f : files) {
					String filename = f.getName(),
							filepath = "/".equals(fileName) ? filename : fileName + "/" + f.getName();
					sb.append(String.format("<p><a href=\"%s\">%s</a></p>", filepath, filename));
				}
				sb.append("</body></html>");
				content = sb.toString().getBytes();
			}

			output.println("HTTP/1.0 200 sendFile");
			output.println("Content-length: " + content.length);
			output.println();
			output.write(content);
			output.flush();
			output.close();
		}

		private void sendError(int errNum, String errMsg) {
			output.println("HTTP/1.0 " + errNum + " " + errMsg);
			output.println("Content-type: text/html");
			output.println();
			output.println("<html>");
			output.println("<head><title>Error " + errNum + "--" + errMsg + "</title></head>");
			output.println("<h1>" + errNum + " " + errMsg + "</h1>");
			output.println("</html>");
			output.println();
			output.flush();
			output.close();
		}
	}
}