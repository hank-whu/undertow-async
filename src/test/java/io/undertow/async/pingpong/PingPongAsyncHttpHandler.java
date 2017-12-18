package io.undertow.async.pingpong;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import io.undertow.async.handler.AsyncHttpHandler;
import io.undertow.async.io.PooledByteBufferInputStream;
import io.undertow.async.io.PooledByteBufferOutputStream;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

public class PingPongAsyncHttpHandler extends AsyncHttpHandler {

	@Override
	protected void handleAsycnRequest(HttpServerExchange exchange, PooledByteBufferInputStream content)
			throws Exception {

		CompletableFuture.completedFuture(content)// init
				.thenApplyAsync(this::readBytesAndClose)// read
				.thenApplyAsync(bytes -> {// write
					ByteBufferPool byteBufferPool = exchange.getConnection().getByteBufferPool();
					PooledByteBufferOutputStream output = new PooledByteBufferOutputStream(byteBufferPool);
					write(output, bytes);
					return output;
				})
				.thenAcceptAsync(output -> send(exchange, StatusCodes.OK, output));
	}

	private byte[] readBytesAndClose(PooledByteBufferInputStream content) {
		try {
			byte[] bytes = new byte[content.available()];
			content.read(bytes);
			return bytes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {// must close it
				content.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void write(PooledByteBufferOutputStream output, byte[] bytes) {
		try {
			output.write("asycn response: ");
			output.write(bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
