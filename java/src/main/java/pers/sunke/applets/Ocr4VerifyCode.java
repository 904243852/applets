package pers.sunke.applets;

/* 
 * 使用 com.asprise.ocr 识别验证码，需添加依赖如下：
	<dependency>
		<groupId>commons-httpclient</groupId>
		<artifactId>commons-httpclient</artifactId>
		<version>3.1</version>
	</dependency>
	<dependency>
		<groupId>commons-io</groupId>
		<artifactId>commons-io</artifactId>
		<version>2.5</version>
	</dependency>
	<dependency>
		<groupId>com.asprise.ocr</groupId>
		<artifactId>java-ocr-api</artifactId>
		<version>[15,)</version>
	</dependency>
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import com.asprise.ocr.Ocr;

public class Ocr4VerifyCode {

	public static void main(String[] args) throws IOException {
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod("http://dz.bjjtgl.gov.cn/service/checkCode.do");
		int statusCode = httpClient.executeMethod(getMethod);
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: " + getMethod.getStatusLine());
			return;
		}
		String picName = "F:\\img\\";
		File filepic = new File(picName);
		if (!filepic.exists())
			filepic.mkdir();
		File filepicF = new File(picName + new Date().getTime() + ".jpg");
		InputStream inputStream = getMethod.getResponseBodyAsStream();
		OutputStream outStream = new FileOutputStream(filepicF);
		IOUtils.copy(inputStream, outStream);
		outStream.close();

		Ocr.setUp(); // one time setup
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
		String s = ocr.recognize(new File[] { filepicF }, Ocr.RECOGNIZE_TYPE_TEXT, Ocr.OUTPUT_FORMAT_PLAINTEXT);

		System.out.println("The result recognized is: " + s);
		System.out.println("Corrent to the verify code is: "
				+ s.replace(",", "").replace("i", "1").replace(" ", "").replace("'", "").replace("o", "0")
						.replace("O", "0").replace("g", "6").replace("B", "8").replace("s", "5").replace("z", "2"));

		// ocr more images here ...
		ocr.stopEngine();
	}
}