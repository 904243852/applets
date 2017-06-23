package pers.sunke.mediaserver.protocol.rtmp;

import java.io.IOException;

import pers.sunke.mediaserver.core.SocketChannelWrap;

public class Chunk {

	// Basic Header
	private int fmt;

	private int cs_id;

	// Message Header
	private int timestamp;

	private int messageLength;

	private int messageTypeId;

	private int messageStreamId;

	private int timestampDelta;

	// Chunk Data
	private byte[] data;
	
	private void initBasicHeader(SocketChannelWrap channel) throws IOException {
		int f = channel.readByte();
		this.fmt = f >> 6;
		if (0 == (f & 0x3f)) {
			int basicHeader = (f & 0xff) << 8 | (channel.readByte() & 0xff);
			this.cs_id = 64 + (basicHeader & 0xff);
		} else if (1 == (f & 0x3f)) {
			int basicHeader = (f & 0xff) << 16 | (channel.readByte() & 0xff) << 8 | (channel.readByte() & 0xff);
			this.cs_id = 64 + ((basicHeader >> 8) & 0xff) + ((basicHeader & 0xff) << 8);
		} else {
			int basicHeader = f & 0xff;
			this.cs_id = (basicHeader & 0x3f);
		}
	}

	private void initMessageHeader(SocketChannelWrap channel, Chunk previous) throws IOException {
		switch (fmt) {
		case 0:
			timestamp = channel.readBytesToInt(3);
			messageLength = channel.readBytesToInt(3);
			messageTypeId = channel.readByte();
			messageStreamId = channel.readBytesToIntReverse(4);
			if (0xFFFFFF == timestamp)
				// Extended Timestamp
				timestamp = channel.readByte();
			break;
		case 1:
			timestampDelta = channel.readBytesToInt(3);
			messageLength = channel.readBytesToInt(3);
			messageTypeId = channel.readByte();
			messageStreamId = previous.messageStreamId;
			if (0xFFFFFF == timestampDelta)
				timestamp = channel.readByte();
			break;
		case 2:
			timestampDelta = channel.readBytesToInt(3);
			messageLength = previous.messageLength;
			messageTypeId = previous.messageTypeId;
			messageStreamId = previous.messageStreamId;
			if (0xFFFFFF == timestampDelta)
				timestamp = channel.readByte();
			break;
		case 3:
			fmt = previous.fmt;
			timestamp = previous.timestamp;
			timestampDelta = previous.timestampDelta;
			messageLength = previous.messageLength;
			messageTypeId = previous.messageTypeId;
			messageStreamId = previous.messageStreamId;
			break;
		}
	}

	public Chunk(SocketChannelWrap channel, Chunk previous) throws IOException {
		initBasicHeader(channel);
		initMessageHeader(channel, previous);
		// data = channel.readAllBytes();
		data = channel.readBytes(messageLength);
	}

	public int getFmt() {
		return fmt;
	}

	public void setFmt(int fmt) {
		this.fmt = fmt;
	}

	public int getCs_id() {
		return cs_id;
	}

	public void setCs_id(int cs_id) {
		this.cs_id = cs_id;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public int getMessageTypeId() {
		return messageTypeId;
	}

	public void setMessageTypeId(int messageTypeId) {
		this.messageTypeId = messageTypeId;
	}

	public int getMessageStreamId() {
		return messageStreamId;
	}

	public void setMessageStreamId(int messageStreamId) {
		this.messageStreamId = messageStreamId;
	}

	public int getTimestampDelta() {
		return timestampDelta;
	}

	public void setTimestampDelta(int timestampDelta) {
		this.timestampDelta = timestampDelta;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}