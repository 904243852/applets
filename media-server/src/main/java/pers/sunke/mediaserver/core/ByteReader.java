package pers.sunke.mediaserver.core;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ByteReader extends ByteWrap {

	public ByteReader(byte[] data) {
		super();
		this.buffer = data;
	}

	/**
	 * 是否可读
	 * 
	 * @return true表示可读，false表示不可读
	 */
	public boolean hasNext() {
		return pos < buffer.length - 1;
	}

	@Override
	protected int check(int offset) {
		int expect = pos + offset;
		if (0 > expect || expect > buffer.length)
			throw new IndexOutOfBoundsException("the index: out of index.");
		return expect;
	}

	/**
	 * 从当前位置起，跳过字节数（偏移量）
	 * 
	 * @param offset
	 *            索引偏移量
	 * @throws IndexOutOfBoundsException
	 */
	public void skip(int offset) throws IndexOutOfBoundsException {
		reindex(check(offset));
	}

	/**
	 * 从当前位置起，预读指定长度的数组(不移动索引)
	 * 
	 * @param length
	 *            预读数组的长度（字节数）
	 * @return 预读的数组
	 */
	public byte[] preview(int length) {
		return Arrays.copyOfRange(buffer, pos, check(length));
	}

	/**
	 * 从当前位置起，读取一个byte
	 * 
	 * @return
	 */
	public byte read() {
		check(1);
		return buffer[pos++];
	}

	/**
	 * 从当前位置起，读取指定长度的数组
	 * 
	 * @param length
	 *            读取数组的长度（字节数）
	 * @return 读取的数组
	 * @throws IndexOutOfBoundsException
	 */
	public byte[] read(int length) throws IndexOutOfBoundsException {
		byte[] cache = preview(length);
		skip(length);
		return cache;
	}

	// TODO 补充bit读写操作（静态方法）

	public long readLong(int length) {
		if (0 > length || 8 < length)
			throw new IndexOutOfBoundsException(
					"the length: long int type can not more than 8 bytes or less than 0 bytes.");
		int index = check(length);
		long result = buffer[index - 1] & 0xff;
		for (int i = length - 2; i >= 0; i--) {
			result = result | (buffer[pos + i] << (8 * (length - i - 1)));
		}
		reindex(index);
		return result;
	}

	public String readString(int length) throws UnsupportedEncodingException {
		byte[] cache = read(length);
		return new String(cache, "UTF-8");
	}

	public int readInt() {
		return (int) readLong(4);
	}

	public long readLong() {
		return readLong(8);
	}

	public short readShort() {
		return (short) readLong(2);
	}

	public int readUnsignedShort() {
		return readShort() & 0xFFFF;
	}
}
