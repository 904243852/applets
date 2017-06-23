package pers.sunke.mediaserver.protocol.rtmp;

/**
 * RTMP协议常量集合
 */
public class Constants {

	public static class SessionStatus {
		public static final int NoHandshake = 0;
		public static final int Handshaking = 1;
		public static final int Handshaked = 2;
	}

	public static class MessageType {
		public static final int MSG_SET_CHUNK = 0x01;
		// public static final int ABORT = 0x02;
		public static final int MSG_BYTES_READ = 0x03;
		public static final int MSG_USER_CONTROL = 0x04;
		public static final int MSG_RESPONSE = 0x05;
		public static final int MSG_REQUEST = 0x06;
		// unknown 0x07
		public static final int MSG_AUDIO = 0x08;
		public static final int MSG_VIDEO = 0x09;
		// unknown 0x0A - 0x0E
		// public static final int METADATA_AMF3 = 0x0F;
		// public static final int SHARED_OBJECT_AMF3 = 0x10;
		// AMF3
		public static final int MSG_INVOKE3 = 0x11;
		public static final int MSG_NOTIFY = 0x12;
		public static final int MSG_OBJECT = 0x13;
		// AMF0
		public static final int MSG_INVOKE = 0x14;
		public static final int MSG_FLASH_VIDEO = 0x16;
	}
}