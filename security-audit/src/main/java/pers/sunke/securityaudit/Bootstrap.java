package pers.sunke.securityaudit;

import java.io.IOException;

import pers.sunke.securityaudit.service.WhiteboxService;

public class Bootstrap {

	public static void main(String[] args) throws IOException {
		WhiteboxService service = new WhiteboxService();
		service.scan("F:\\KF.Java\\helper\\src");
	}
}
