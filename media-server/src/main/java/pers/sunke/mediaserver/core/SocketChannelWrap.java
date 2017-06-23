package pers.sunke.mediaserver.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelWrap {

	private SocketChannel channel;

	public SocketChannelWrap(SocketChannel channel) {
		this.channel = channel;
	}

	public byte[] readBytes(int length) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(length);
		channel.read(buffer);
		buffer.flip();
		return buffer.array();
	}

	public byte readByte() throws IOException {
		return readBytes(1)[0];
	}

	public byte[] readAllBytes() throws IOException {
		byte[] result = new byte[0];
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int got = 0;
		while ((got = channel.read(buffer)) > 0) {
			buffer.flip();
			byte[] newBytes = new byte[result.length + got];
			System.arraycopy(result, 0, newBytes, 0, result.length);
			System.arraycopy(buffer.array(), 0, newBytes, result.length, got);
			result = newBytes;
		}
		return result;
	}

	public int readBytesToInt(int length) throws IOException {
		byte[] buffer = readBytes(length);
		int result = buffer[length - 1] & 0xff;
		for (int i = length - 2; i >= 0; i--) {
			result = result | (buffer[i] << (8 * (length - i - 1)));
		}
		return result;
	}

	public int readBytesToIntReverse(int length) throws IOException {
		byte[] buffer = readBytes(length);
		int result = buffer[0] & 0xff;
		for (int i = 1; i < length; i++) {
			result = result | (buffer[i] << (8 * i));
		}
		return result;
	}

	public int writeBytes(byte[] data) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		return channel.write(buffer);
	}

	public int writeByte(byte data) throws IOException {
		return writeBytes(new byte[] { data });
	}

	public void close() throws IOException {
		channel.close();
	}
}