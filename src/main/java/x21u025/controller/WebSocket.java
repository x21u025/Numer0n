package x21u025.controller;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;


@Configuration
@EnableWebSocket
public class WebSocket implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(queueHandler(), "/ws/Queue/*")
				.addInterceptors(queueInterceptor())
				.setAllowedOrigins("URL/");
		registry.addHandler(gameHandler(), "/ws/Game/**")
				.addInterceptors(gameInterceptor())
				.setAllowedOrigins("URL/");
		registry.addHandler(watchHandler(), "/ws/Watch/**")
				.addInterceptors(watchInterceptor())
				.setAllowedOrigins("URL/");
		registry.addHandler(chatHandler(), "/ws/Chat/**")
				.addInterceptors(chatInterceptor())
				.setAllowedOrigins("URL/");
	}

	@Bean
	public HandshakeInterceptor queueInterceptor() {
		return new HandshakeInterceptor() {
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
				WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

				String path = request.getURI().getPath();
				String hash = path.substring(path.lastIndexOf('/') + 1);

				attributes.put("hash", hash);
				return true;
			}

			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Exception exception) {
				// Nothing to do after handshake
			}
		};
	}

	@Bean
	public WebSocketHandler queueHandler() {
		//修正
		return new QueueWebSocket();

//		return new TextWebSocketHandler() {
//			public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//				// Retrieve the auction id from the websocket session (copied during the handshake)
//				String auctionId = (String) session.getAttributes().get("auctionId");
//
//				// Your business logic...
//			}
//		};
	}

	@Bean
	public HandshakeInterceptor gameInterceptor() {
		return new HandshakeInterceptor() {
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
				WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

				String path = request.getURI().getPath();
				String sessionId = path.substring(path.lastIndexOf('/') + 1);
				String secondPath = path.substring(0, path.lastIndexOf('/'));
				String gameId = secondPath.substring(secondPath.lastIndexOf('/') + 1);

				attributes.put("sessionId", sessionId);
				attributes.put("gameId", gameId);
				return true;
			}

			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Exception exception) {
				// Nothing to do after handshake
			}
		};
	}

	@Bean
	public WebSocketHandler gameHandler() {
		//修正
		return new GameWebSocket();
	}

	@Bean
	public HandshakeInterceptor watchInterceptor() {
		return new HandshakeInterceptor() {
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
				WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

				String path = request.getURI().getPath();
				String sessionId = path.substring(path.lastIndexOf('/') + 1);
				String secondPath = path.substring(0, path.lastIndexOf('/'));
				String gameId = secondPath.substring(secondPath.lastIndexOf('/') + 1);

				attributes.put("sessionId", sessionId);
				attributes.put("gameId", gameId);
				return true;
			}

			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Exception exception) {
				// Nothing to do after handshake
			}
		};
	}

	@Bean
	public WebSocketHandler watchHandler() {
		return new WatchWebSocket();
	}

	@Bean
	public HandshakeInterceptor chatInterceptor() {
		return new HandshakeInterceptor() {
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
				WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

				String path = request.getURI().getPath();
				String sessionId = path.substring(path.lastIndexOf('/') + 1);

				attributes.put("sessionId", sessionId);
				return true;
			}

			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Exception exception) {
				// Nothing to do after handshake
			}
		};
	}

	@Bean
	public WebSocketHandler chatHandler() {
		return new ChatWebSocket();
	}
}
