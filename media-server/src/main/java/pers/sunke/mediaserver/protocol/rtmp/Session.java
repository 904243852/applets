package pers.sunke.mediaserver.protocol.rtmp;

import pers.sunke.mediaserver.core.SocketChannelWrap;

public class Session {

	private SocketChannelWrap socketChannelWrap;
	
	private int status;

	private Chunk chunk;
	
	public Session(SocketChannelWrap socketChannelWrap) {
		this.socketChannelWrap = socketChannelWrap;
	}
	
	public SocketChannelWrap getSocketChannelWrap() {
		return socketChannelWrap;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}
}