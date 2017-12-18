package io.undertow.async.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.DefaultByteBufferPool;

public class IOTest {

	@Test
	public void test() throws Exception {
		ByteBufferPool byteBufferPool = new DefaultByteBufferPool(true, 2, -1, 4);
		PooledByteBufferOutputStream output = new PooledByteBufferOutputStream(byteBufferPool);

		String hello = "hello, 世界！";
		output.write(hello);
		output.flip();

		PooledByteBuffer[] buffers = output.getPooledByteBuffers();

		try (PooledByteBufferInputStream input = new PooledByteBufferInputStream(buffers);) {
			byte[] bytes = new byte[input.available()];
			input.read(bytes);
			String str = new String(bytes, "UTF-8");

			assertEquals(hello, str);
		}

		output.release();
		output.close();
	}

}
