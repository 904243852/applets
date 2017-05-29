package pers.sunke.applets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswdManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] iv;

	private List<PasswdInfo> passwds;

	private transient static String key = "0000000000000000";

	private transient final String PASSWD_FILEPATH = "./passwd";

	private static String k;

	public PasswdManager() throws IOException, ClassNotFoundException {
		File file = new File(PASSWD_FILEPATH);
		if (file.exists() && file.isFile()) {
			InputStream fis = new FileInputStream(file);
			// byte[] data = new byte[(int) file.length()];
			// fis.read(data);
			// fis.close();
			ObjectInputStream ois = new ObjectInputStream(fis);
			PasswdManager m = (PasswdManager) ois.readObject();
			ois.close();
			this.iv = m.iv;
			this.passwds = m.passwds;
		} else {
			this.iv = new byte[16];
			SecureRandom random = new SecureRandom();
			random.nextBytes(iv);
			this.passwds = new ArrayList<PasswdInfo>();
		}
	}

	public <T> void edit(T object, Scanner scanner) throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException, InvocationTargetException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if (null == object) {
			throw new NullPointerException();
		}

		boolean confirm = false;

		Class<?> clazz = object.getClass();

		while (!confirm) {
			for (Field field : clazz.getDeclaredFields()) {
				if (0x08 == (field.getModifiers() & 0x08) || "this$0".equals(field.getName())) {
					continue;
				}
				// field.setAccessible(true);
				char[] nameCharArray = field.getName().toCharArray();
				nameCharArray[0] -= 32;
				String name = String.valueOf(nameCharArray);
				System.out.println(String.format("please input the %s:", name));
				String input = scanner.nextLine();
				if (!input.matches("^\\s*$")) {
					// field.set(object, input);
					clazz.getMethod(String.format("set%s", name), String.class).invoke(object, input);
				}
			}

			System.out.println("complete to edit the entity object as below:");
			System.out.println(clazz.getMethod("toString").invoke(object));
			System.out.println("please confirm with the answer 'yes' or 'no'?");
			String answer = null;
			while (!(answer = scanner.nextLine().trim().toLowerCase()).matches("^yes|no$")) {
			}
			if ("yes".equals(answer))
				confirm = true;
		}
	}

	public void save() throws IOException {
		FileOutputStream fos = new FileOutputStream(PASSWD_FILEPATH);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException,
			InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		PasswdManager manager = new PasswdManager();
		Scanner scanner = new Scanner(System.in);
		String command = null;

		{
			System.out.println("please input passwd for the secret key:");
			k = null;
			while (0 >= (k = scanner.nextLine()).length()) {
			}

			char[] ks = k.toCharArray();
			char[] ksf = new char[16];
			for (int i = 0; i < 16; i++) {
				if (i < ks.length) {
					ksf[i] = ks[i];
				} else {
					ksf[i] = '0';
				}
			}
			key = new String(ksf);

		}

		while (!"exit".equals(command = scanner.nextLine().trim().toLowerCase())) {
			String[] commands = command.split("\\s");
			switch (commands[0]) {
			case "insert":
				PasswdInfo p = manager.new PasswdInfo();
				manager.edit(p, scanner);
				manager.passwds.add(p);
				break;
			case "delete": {
				int index = Integer.parseInt(commands[1]) - 1;
				PasswdInfo passwd = manager.passwds.get(index);
				System.out.println(passwd);
				break;
			}
			case "update": {
				int index = Integer.parseInt(commands[1]) - 1;
				PasswdInfo passwd = manager.passwds.get(index);
				manager.edit(passwd, scanner);
				break;
			}
			case "query": {
				if (1 == commands.length) {
					System.out.println(String.format("%d records found:", manager.passwds.size()));
					System.out.println("description\tname\tpassword\turl\ttype\tvalid\tremarks");
					for (PasswdInfo passwd : manager.passwds) {
						System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s", passwd.getDescription(),
								passwd.getName(), passwd.getPassword(), passwd.getUrl(), passwd.getType(),
								passwd.getValid(), passwd.getRemarks()));
					}
				}
				break;
			}
			case "detail": {
				int index = Integer.parseInt(commands[1]) - 1;
				PasswdInfo passwd = manager.passwds.get(index);
				System.out.println(passwd);
				break;
			}
			default:
				System.out.println("no this command.");
				continue;
			}
			manager.save();
		}
		scanner.close();
	}

	class PasswdInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private String description;

		private String name;

		private String password;

		private String url;

		private String type;

		private Boolean valid;

		private String remarks;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			try {
				byte[] raw = key.getBytes("UTF-8");
				SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				IvParameterSpec ivSpec = new IvParameterSpec(iv);
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
				byte[] encrypted1 = Base64.getDecoder().decode(password);
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original, "UTF-8");
				return originalString;
			} catch (Exception ex) {
				return null;
			}
		}

		public void setPassword(String password) throws NoSuchAlgorithmException, NoSuchPaddingException,
				UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException,
				IllegalBlockSizeException, BadPaddingException {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] raw = key.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
			byte[] encrypted = cipher.doFinal(password.getBytes("UTF-8"));
			this.password = new String(Base64.getEncoder().encode(encrypted), "UTF-8");
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Boolean getValid() {
			return valid;
		}

		public void setValid(String valid) {
			this.valid = "true".equals(valid.toLowerCase()) ? true : false;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		@Override
		public String toString() {
			return String.format(
					"\tdescription: %s\n\tname: %s\n\tpassword: %s\n\turl: %s\n\ttype: %s\n\tvalid: %s\n\tremarks: %s",
					getDescription(), getName(), getPassword(), getUrl(), getType(), getValid(), getRemarks());
		}
	}

}
