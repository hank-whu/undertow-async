package io.undertow.async.pingpong;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;

public class PingPongServer implements Closeable {

	private final Undertow undertow;

	public PingPongServer(String host, int port) {
		undertow = Undertow.builder()//
				.addHttpListener(port, host)//
				.setServerOption(UndertowOptions.DECODE_URL, false)//
				.setServerOption(UndertowOptions.ALWAYS_SET_DATE, false)//
				.setHandler(new PingPongAsyncHttpHandler())//
				.build();
	}

	public void start() {
		undertow.start();
		System.out.println("undertow server started.");
	}

	@Override
	public void close() throws IOException {
		try {
			undertow.stop();
		} catch (Throwable t) {
			throw new IOException(t);
		}
	}

	public static void main(String[] args) throws IOException {
		try (PingPongServer server = new PingPongServer("127.0.0.1", 8080); Scanner scanner = new Scanner(System.in);) {
			server.start();

			while ("stop".equals(scanner.nextLine())) {
				break;
			}
		}
	}
}