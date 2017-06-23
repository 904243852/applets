package pers.sunke.mediaserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;

import org.junit.Test;

import pers.sunke.mediaserver.core.TCPServer;
import pers.sunke.mediaserver.protocol.rtmp.Session;

public class TCPServerTest {

	public class Server extends TCPServer {

		private Charset charset = Charset.forName("UTF-8");

		public Server(int port) throws IOException {
			super(port);
		}

		@Override
		protected void onAccept(SelectionKey sk, SocketChannel socketChannel) throws IOException {

		}

		@Override
		protected void onRead(SelectionKey sk) throws IOException {
			SocketChannel sc = (SocketChannel) sk.channel();
			Session session = (Session) sk.attachment();
			if (null == session) {
				sc.close();
				return;
			}

			try {
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				StringBuilder content = new StringBuilder();

				while (sc.read(buffer) > 0) {
					buffer.flip();
					content.append(charset.decode(buffer));
				}
				System.out.println(String.format("server is listening from client \"%s\", data received is \n%s",
						sc.getRemoteAddress(), content));

				sc.write(charset.encode((new Date()).toString()));
			} catch (IOException io) {
				sk.cancel();
				if (sk.channel() != null) {
					sk.channel().close();
				}
			}
		}
	}

	@Test
	public void test() throws IOException {
		Server server = new Server(80);
		server.start();
	}
}