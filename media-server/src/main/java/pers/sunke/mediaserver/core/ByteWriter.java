package pers.sunke.mediaserver.core;

import java.io.UnsupportedEncodingException;

public class ByteWriter extends ByteWrap {

	public ByteWriter() {
		super();
		this.buffer = new byte[0];
	}

	@Override
	protected int check(int offset) {
		int expect = pos + offset;
		if (0 > expect || expect > buffer.length) {
			byte[] swap = buffer;
			buffer = new byte[expect];
			System.arraycopy(swap, 0, buffer, 0, swap.length);
		}
		return expect;
	}

	/**
	 * 从当前位置起，写入指定的数组
	 * 
	 * @param data
	 *            要写入的数组
	 * @throws IndexOutOfBoundsException
	 */
	public void write(byte[] data) throws IndexOutOfBoundsException {
		int length = data.length;
		int expect = check(length);
		for (int i = 0; i < length; i++) {
			buffer[pos + i] = data[i];
		}
		reindex(expect);
	}

	public void write(long value, int length) {
		if (0 > length || 8 < length)
			throw new IndexOutOfBoundsException(
					"the length: long int type can not more than 8 bytes or less than 0 bytes.");
		int index = check(length);
		byte[] cache = new byte[length];
		for (int i = 0; i < length; i++) {
			cache[i] = (byte) ((value >> (8 * (length - 1 - i))) & 0xff);
		}
		write(cache);
		reindex(index);
	}

	public void writeString(String value) throws UnsupportedEncodingException {
		byte[] cache = value.getBytes("UTF-8");
		write(cache);
	}

	public void writeInt(int value) {
		write(value, 4);
	}

	public void writeLong(long value) {
		write(value, 8);
	}

	public void writeShort(short value) {
		write(value, 2);
	}
}
