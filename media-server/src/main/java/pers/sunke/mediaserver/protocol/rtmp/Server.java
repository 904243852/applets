package pers.sunke.mediaserver.protocol.rtmp;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pers.sunke.mediaserver.core.ByteReader;
import pers.sunke.mediaserver.core.ByteWriter;
import pers.sunke.mediaserver.core.SocketChannelWrap;
import pers.sunke.mediaserver.core.TCPServer;
import pers.sunke.mediaserver.protocol.rtmp.Constants.*;

public class Server extends TCPServer {

	public Server(int port) throws IOException {
		super(port);
	}

	@Override
	protected void onAccept(SelectionKey sk, SocketChannel socketChannel) throws IOException {
		Session session = new Session(new SocketChannelWrap(socketChannel));
		sk.attach(session);
	}

	@Override
	protected void onRead(SelectionKey sk) throws IOException {
		Session session = (Session) sk.attachment();
		if (null == session) {
			sk.channel().close();
			return;
		}
		SocketChannelWrap socketChannelWrap = session.getSocketChannelWrap();

		try {
			int sessionStatus = session.getStatus();

			if (SessionStatus.NoHandshake == sessionStatus || SessionStatus.Handshaking == sessionStatus) {
				sessionStatus = doHandshake(socketChannelWrap, sessionStatus);
				if (-1 == sessionStatus)
					return;
				else
					session.setStatus(sessionStatus);
			} else if (SessionStatus.Handshaked == session.getStatus()) {
				Chunk chunk = new Chunk(socketChannelWrap, session.getChunk());
				session.setChunk(chunk);
				doHandle(session);
			}

			sk.interestOps(SelectionKey.OP_READ);
		} catch (IOException io) {
			sk.cancel();
			if (sk.channel() != null) {
				sk.channel().close();
			}
		}
	}

	private int doHandshake(SocketChannelWrap socketChannelWrap, int sessionStatus) throws IOException {
		if (SessionStatus.NoHandshake == sessionStatus) {
			// accept c0 package from client
			byte c0 = socketChannelWrap.readByte();
			if (c0 != 0x03) {
				socketChannelWrap.close();
				return -1;
			}

			byte s0 = c0;
			// send s0 package to client
			socketChannelWrap.writeByte(s0);

			byte[] s1 = new byte[1536];
			Random random = new Random();
			random.nextBytes(s1);
			s1[0] = 0x03;
			s1[1] = s1[2] = s1[3] = s1[4] = s1[5] = s1[6] = s1[7] = 0;
			// send s1 package to client
			socketChannelWrap.writeBytes(s1);

			// accept c0 package from client
			byte[] c1 = socketChannelWrap.readBytes(1536);

			byte[] s2 = c1;
			// send s2 package to client
			socketChannelWrap.writeBytes(s2);

			return SessionStatus.Handshaking;
		} else if (SessionStatus.Handshaking == sessionStatus) {
			// accept c2 package from client
			socketChannelWrap.readBytes(1536);
			return SessionStatus.Handshaked;
		}
		return -1;
	}

	private int doHandle(Session session) throws IOException {
		SocketChannelWrap socketChannelWrap = session.getSocketChannelWrap();
		Chunk chunk = session.getChunk();
		switch (chunk.getMessageTypeId()) {
		case MessageType.MSG_INVOKE:
		case MessageType.MSG_INVOKE3:
			ByteReader reader = new ByteReader(chunk.getData());
			String name = (String) AMF0.decode(reader);
			Double transactionId = ((Double) AMF0.decode(reader));
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> object = (LinkedHashMap<String, Object>) AMF0.decode(reader);
			List<Object> list = new ArrayList<Object>();
			while (reader.hasNext()) {
				list.add(AMF0.decode(reader));
			}
			Object[] args = list.toArray();

			ByteWriter writer = new ByteWriter();
			if (name.equals("connect")) {
				// WindowAckSize
				writer.writeInt(2500000);
				// SetPeerBw
				writer.writeInt(2500000);
				writer.write(2, 1);
				// Begin Stream
				writer.writeShort((short) 0);
				writer.writeInt(chunk.getMessageStreamId());
				// Command
				AMF0.encode(writer, "_result");
				AMF0.encode(writer, transactionId);
				AMF0.encode(writer, null);
				Map<String, Object> p = new LinkedHashMap<String, Object>() {
					private static final long serialVersionUID = 1L;
					{
						put("level", "_status");
						put("code", "NetConnection.Connect.Success");
						put("description", "Connection succeeded.");
						put("fmsVer", "FMS/3,5,1,516");
						put("capabilities", 31.0);
						put("mode", 1.0);
						put("objectEncoding", 0.0);
					}
				};
				AMF0.encode(writer, p);
				// onBWDone
				AMF0.encode(writer, "onBWDone");
				AMF0.encode(writer, 0);
				AMF0.encode(writer, null);
			} else if (name.equals("createStream")) {

			} else if (name.equals("play")) {

			} else if (name.equals("deleteStream")) {

			} else if (name.equals("closeStream")) {

			} else if (name.equals("pause")) {

			} else if (name.equals("seek")) {

			} else if (name.equals("publish")) {

			} else {

			}
			socketChannelWrap.writeBytes(writer.getBuffer());
			break;
		default:
			return -1;
		}
		return 1;
	}
}