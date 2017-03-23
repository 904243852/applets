package pers.sunke.applets;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class WallPaperMaker {

	private static final String BingBaseUrl = "http://cn.bing.com";

	private static final String BingBackgroundImageRequestUrl = BingBaseUrl
			+ "/HPImageArchive.aspx?format=xml&idx=0&n=1";

	private static final String TargetDirectory = System.getProperty("user.home") + "\\Pictures\\wallpaper\\";

	private static final String WatermarkFilePath = TargetDirectory + "watermark.jpg";

	public static String get(String url) {
		StringBuilder result = new StringBuilder();
		BufferedReader in = null;
		try {
			URL u = new URL(url);

			URLConnection connection = u.openConnection();

			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

			connection.connect();

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			System.err.println("send get with exception: " + e);
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result.toString();
	}

	public static String simpleMatch(String pattern, String content) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(content);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	public static void AddWatermarkImage(BufferedImage sourceImage, File watermarkFile, File targetFile) {
		float alpha = 0.5f, scale = 1;

		try {
			int sourceWideth = sourceImage.getWidth(null);
			int sourceHeight = sourceImage.getHeight(null);

			BufferedImage targetImage = new BufferedImage(sourceWideth, sourceHeight, BufferedImage.TYPE_INT_RGB);

			Graphics2D graphics2D = targetImage.createGraphics();
			graphics2D.drawImage(sourceImage, 0, 0, sourceWideth, sourceHeight, null);

			Image watermarkImage = ImageIO.read(watermarkFile);
			int watermarkWidth = Integer.valueOf(new Float(watermarkImage.getWidth(null) * scale).intValue());
			int watermarkHeight = Integer.valueOf(new Float(watermarkImage.getHeight(null) * scale).intValue());

			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			graphics2D.drawImage(watermarkImage, sourceWideth - watermarkWidth, 0, watermarkWidth, watermarkHeight,
					null);
			graphics2D.dispose();

			ImageIO.write((BufferedImage) targetImage, "JPEG", targetFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File getTargetFile() throws IOException {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(currentTime);

		File targetFile = new File(String.format("%s%s.jpg", TargetDirectory, dateString));
		if (!targetFile.exists()) {
			mkdirs(targetFile.getParentFile());
			targetFile.createNewFile();
		}

		return targetFile;
	}

	private static void mkdirs(File dir) {
		if (!dir.getParentFile().exists()) {
			mkdirs(dir.getParentFile());
		}
		dir.mkdir();
	}

	public static void main(String[] args) throws IOException {
		// System.setProperty("http.proxyHost", "www.proxy.com");
		// System.setProperty("http.proxyPort", String.valueOf(8080));
		// System.setProperty("http.proxyUserName", "username");
		// System.setProperty("http.proxyPassword", "password");

		String x = get(BingBackgroundImageRequestUrl);
		String r = simpleMatch("<url>([^<]*)</url>", x);

		URL url = new URL(BingBaseUrl + r);
		URLConnection connection = url.openConnection();
		connection.connect();
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());

		File watermarkFile = new File(WatermarkFilePath);
		File targetFile = getTargetFile();

		if (watermarkFile.exists() && watermarkFile.isFile()) {
			BufferedImage sourceImage = ImageIO.read(bis);

			// File sourceFile = new File("H:/source.jpg");
			// BufferedImage sourceImage = ImageIO.read(sourceFile);
			AddWatermarkImage(sourceImage, watermarkFile, targetFile);
		} else {
			FileOutputStream fos = new FileOutputStream(targetFile);
			byte[] buf = new byte[1024];
			for (int len = 0; (len = bis.read(buf)) != -1;) {
				fos.write(buf, 0, len);
			}
			fos.close();
		}

		// Runtime runtime = Runtime.getRuntime();
		// // 设置背景图片
		// runtime.exec(String.format("reg add \"HKEY_CURRENT_USER\\Control Panel\\Desktop\" /v Wallpaper /t REG_SZ /d \"%s\" /f", targetFile.getPath()));
		// // 设置背景图片样式（2为拉伸）
		// runtime.exec("reg add \"HKEY_CURRENT_USER\\Control Panel\\Desktop\" /v WallpaperStyle /t REG_SZ /d \"2\" /f");
		// // 刷新命令需要通过批处理脚本调用
		// runtime.exec("RUNDLL32.EXE user32.dll,UpdatePerUserSystemParameters");
	}
}
