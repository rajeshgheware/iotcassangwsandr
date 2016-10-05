package iot.sensors.location.config;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	/**
	 * The registerStompEndpoints() method registers the "/sensor-websocket"
	 * endpoint, enabling SockJS fallback options so that alternate transports
	 * may be used if WebSocket is not available. The SockJS client will attempt
	 * to connect to "/gs-guide-websocket" and use the best transport available
	 * (websocket, xhr-streaming, xhr-polling, etc).
	 * 
	 * @param registry
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/sensor-websocket").addInterceptors(new IpHandshakeInterceptor()).withSockJS();
	}

}
class IpHandshakeInterceptor implements HandshakeInterceptor {

    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // Set ip attribute to WebSocket session
        attributes.put("address", request.getRemoteAddress());
        attributes.put("port", request.getRemoteAddress().getPort());

        return true;
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {          
    }
}