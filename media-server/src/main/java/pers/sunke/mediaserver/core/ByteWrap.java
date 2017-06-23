package pers.sunke.mediaserver.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ByteWrap {
	/**
	 * byte数组对象
	 */
	protected byte[] buffer;

	/**
	 * 索引
	 */
	protected int pos;

	public ByteWrap() {
		this.pos = 0;
	}

	public byte[] getBuffer() {
		return this.buffer;
	}

	/**
	 * 获取索引位置
	 * 
	 * @return
	 */
	public int getPos() {
		return this.pos;
	}

	/**
	 * 设置索引位置
	 * 
	 * @param index
	 *            设置索引的位置
	 */
	protected void reindex(int index) {
		pos = index;
	}

	/**
	 * 检查索引偏移量是否合法
	 * 
	 * @param offset
	 *            索引偏移量
	 * @return 偏移后的索引
	 */
	protected abstract int check(int offset);

	@Override
	public String toString() {
		List<String> r = new ArrayList<String>();
		for (int i = 0; i < buffer.length; i++)
			r.add(Integer.toHexString(buffer[i]));
		return Arrays.toString(r.toArray());
	}
}