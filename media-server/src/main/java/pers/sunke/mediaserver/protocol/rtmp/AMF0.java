package pers.sunke.mediaserver.protocol.rtmp;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import pers.sunke.mediaserver.core.ByteReader;
import pers.sunke.mediaserver.core.ByteWriter;

public class AMF0 {
	public static enum Type {
		NUMBER(0x00),
        BOOLEAN(0x01),
        STRING(0x02),
        OBJECT(0x03),
        NULL(0x05),
        UNDEFINED(0x06),
        REFERENCE(0x07),
        MAP(0x08),
        OBJECT_END(0x09),
        ARRAY(0x0A),
        DATE(0x0B),
        LONG_STRING(0x0C),
        UNSUPPORTED(0x0D),
        RECORDSET(0x0E),
        XML_DOCUMENT(0x0F),
        TYPED_OBJECT(0x10);

		private final int value;

		private Type(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static Type toEnum(int value) {
			switch (value) {
			case 0x00:
				return Type.NUMBER;
			case 0x01:
				return Type.BOOLEAN;
			case 0x02:
				return Type.STRING;
			case 0x03:
				return Type.OBJECT;
			case 0x05:
				return Type.NULL;
			case 0x06:
				return Type.UNDEFINED;
			case 0x07:
				return Type.REFERENCE;
			case 0x08:
				return Type.MAP;
			case 0x09:
				return Type.OBJECT_END;
			case 0x0A:
				return Type.ARRAY;
			case 0x0B:
				return Type.DATE;
			case 0x0C:
				return Type.LONG_STRING;
			case 0x0D:
				return Type.UNSUPPORTED;
			case 0x0E:
				return Type.RECORDSET;
			case 0x0F:
				return Type.XML_DOCUMENT;
			case 0x10:
				return Type.TYPED_OBJECT;
			default:
				return null;
			}
		}
		
		private static Type typeOf(final Object value) {
            if (value == null) {
                return NULL;
            } else if (value instanceof String) {
                return STRING;
            } else if (value instanceof Number) {
                return NUMBER;
            } else if (value instanceof Boolean) {
                return BOOLEAN;
            } else if (value instanceof LinkedHashMap) {
            	@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> obj = (LinkedHashMap<String, Object>) value;
                if (obj.containsKey("classname"))
                    return TYPED_OBJECT;
                else
                    return OBJECT;
            } else if (value instanceof Map) {
                return MAP;
            } else if (value instanceof Object[]) {
                return ARRAY;
            } else if(value instanceof Date) {
                return DATE;
            } else {
                throw new RuntimeException("unexpected type: " + value.getClass());
            }
        }
	}
	
	public static Object decode(ByteReader reader) {
        Type type = Type.toEnum(reader.read());
        Object value = decode(reader, type);
        return value;
    }
	
	private static Object decode(ByteReader reader, Type type) {
    	String decodedString = "";
        switch (type) {
            case NUMBER: return Double.longBitsToDouble(reader.readLong());
            case BOOLEAN: return reader.read() == 0x01;
            case STRING: 
            	try {
            		decodedString = decodeString(reader);
            	} catch(Exception e) {
                	decodedString = new String();
            	}
            	return decodedString;
            case ARRAY:
                final int arraySize = reader.readInt();
                final Object[] array = new Object[arraySize];
                for (int i = 0; i < arraySize; i++) {
                    array[i] = decode(reader);
                }
                return array;
            case MAP:
            	reader.readInt(); // should always be 0
            case OBJECT:
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                byte[] endMarker = null;
                while (reader.hasNext()) {
                	endMarker = reader.preview(3);
                    if (Arrays.equals(endMarker, new byte[]{0x00, 0x00, 0x09})) {
                    	reader.skip(3);
                        break;
                    }
                	try {
                		decodedString = decodeString(reader);
                	} catch(Exception e) {
                    	decodedString = new String();
                	}
                    map.put(decodedString, decode(reader));
                }
                return map;
            case DATE:
                long dateValue = reader.readLong();
                reader.readShort(); // consume the timezone
                return new Date((long) Double.longBitsToDouble(dateValue));
            case LONG_STRING:
                int stringSize = reader.readInt();
                byte[] bytes = reader.read(stringSize);
                return new String(bytes); // TODO UTF-8 ?
            case NULL:
            case UNDEFINED:
            case UNSUPPORTED:
                return null;
            case TYPED_OBJECT:
            	try {
            		decodedString = decodeString(reader);
            	} catch(Exception e) {
                	decodedString = new String();
            	}
                String classname = decodedString;
                @SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> object = (LinkedHashMap<String, Object>) decode(reader, Type.OBJECT);
                object.put("classname", classname);
                return object;
            default:
                throw new RuntimeException("unexpected type: " + type);
        }
    }
	
	private static String decodeString(ByteReader reader) {
        final int size = reader.readUnsignedShort();
        final byte[] bytes = reader.read(size);
        return new String(bytes); // TODO UTF-8 ?
    }
	
	public static void encode(ByteWriter writer, Object value) {
        Type type = Type.typeOf(value);
        writer.write(type.getValue(), 1);
        switch (type) {
            case NUMBER:
                if(value instanceof Double)
                	writer.writeLong(Double.doubleToLongBits((Double) value));
                else
                	writer.writeLong(Double.doubleToLongBits(Double.valueOf(value.toString())));
                return;
            case BOOLEAN:                
            	writer.write((Boolean) value ? 0x01 : 0x00, 1);
                return;
            case STRING:
                encodeString(writer, (String) value);
                return;
            case NULL:
                return;
            case MAP:
            	writer.writeInt(0);
                // no break; remaining processing same as OBJECT
            case OBJECT:
                encodeObject(writer, value);
                return;
            case ARRAY:
                final Object[] array = (Object[]) value;
                writer.writeInt(array.length);
                for(Object o : array) {
                    encode(writer, o);
                }
                return;
            case DATE:
                final long time = ((Date) value).getTime();
                writer.writeLong(Double.doubleToLongBits(time));
                writer.writeShort((short) 0);
                return;
            case TYPED_OBJECT:
                @SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) value;
                encodeString(writer, (String) map.remove("classname"));
                encodeObject(writer, value);
                return;
            default:
                // ignoring other types client doesn't require for now
                throw new RuntimeException("unexpected type: " + type);
        }
    }
	
	private static void encodeString(ByteWriter writer, String value) {
        final byte[] bytes = value.getBytes(); // TODO UTF-8 ?
        writer.writeShort((short) bytes.length);
        writer.write(bytes);
    }
	
	private static void encodeObject(ByteWriter writer, Object value) {
        @SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) value;
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            encodeString(writer, entry.getKey());
            encode(writer, entry.getValue());
        }
        writer.write(new byte[]{0x00, 0x00, 0x09});
    }
}