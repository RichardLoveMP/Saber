package com.beyondli;

import com.beyondli.common.config.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class WebsocketDemoApplication {

	public static void main(String[] args) {
		try {
			Thread t = new GreetingServer(2345);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		SpringApplication.run(WebsocketDemoApplication.class, args);

	}

}
