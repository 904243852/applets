package pers.sunke.mediaserver;

import java.io.IOException;

import pers.sunke.mediaserver.protocol.rtmp.Server;

public class Bootstrap {

	public static void main(String[] args) throws IOException {
		Server server = new Server(1935);
		server.start();
	}
}