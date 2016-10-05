package iot.sensors.location;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.integration.ip.tcp.serializer.AbstractByteArraySerializer;
import org.springframework.integration.ip.tcp.serializer.SoftEndOfStreamException;
import org.springframework.stereotype.Component;

@Component(value="serializeDeserialize")
public class Serializer extends AbstractByteArraySerializer {

	public void serialize(byte[] bytes, OutputStream outputStream)
			throws IOException {
		outputStream.write(bytes);
		outputStream.flush();
	}

	public byte[] deserialize(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[this.maxMessageSize];
		int n = 0;
		int bite = 0;
		if (logger.isDebugEnabled()) {
			logger.debug("Available to read:" + inputStream.available());
		}
		while (bite >= 0) {
			bite = inputStream.read();
			if (bite < 0) {
				if (n == 0) {
					throw new SoftEndOfStreamException("Stream closed between payloads");
				}
				break;
			}
			buffer[n++] = (byte) bite;
			/*if (n >= this.maxMessageSize) {
				throw new IOException("Socket was not closed before max message length: "
						+ this.maxMessageSize);
			}*/
//			System.out.print(deviceMsg);
			if(bite == (byte) 0x0a){ //new line feed inspired by ByteArrayLfSerializer
				byte[] assembledData = new byte[n];
				System.arraycopy(buffer, 0, assembledData, 0, n);
				String deviceMsg = new String(assembledData);
				logger.info("Socket Stream:"+new String(assembledData));
				return assembledData;								
			}
		};
		byte[] assembledData = new byte[n];
		System.arraycopy(buffer, 0, assembledData, 0, n);
		logger.info("Socket Stream:"+new String(assembledData));
		return assembledData;
	}

}

