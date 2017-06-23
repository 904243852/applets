package pers.sunke.mediaserver.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public abstract class TCPServer {

	private Selector selector;

	protected ServerSocketChannel serverSocketChannel;

	private InetSocketAddress serverSocketAddress;

	public TCPServer(int port) throws IOException {
		selector = SelectorProvider.provider().openSelector();

		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);

		serverSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), port);
	}

	public void start() throws IOException {
		serverSocketChannel.socket().bind(serverSocketAddress);

		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		System.out.println(String.format("listen in %s:%s", serverSocketAddress.getAddress().getHostAddress(), serverSocketAddress.getPort()));

		while (selector.select() > 0) {
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

			while (iterator.hasNext()) {
				SelectionKey sk = (SelectionKey) iterator.next();
				iterator.remove();

				if (sk.isAcceptable()) {
					SocketChannel socketChannel = serverSocketChannel.accept();
					socketChannel.configureBlocking(false);
					SelectionKey csk = socketChannel.register(selector, SelectionKey.OP_READ);
					onAccept(csk, socketChannel);
					sk.interestOps(SelectionKey.OP_ACCEPT);
				}
				if (sk.isReadable()) {
					onRead(sk);
				}
			}
		}
	}

	protected abstract void onAccept(SelectionKey sk, SocketChannel socketChannel) throws IOException;

	protected abstract void onRead(SelectionKey sk) throws IOException;
}