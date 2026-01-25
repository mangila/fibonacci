package com.github.mangila.fibonacci.web.config;

import com.github.mangila.fibonacci.web.ws.AnonymousHandshakeHandler;
import com.github.mangila.fibonacci.web.ws.WebSocketEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;

    public WebSocketConfig(SimpleAsyncTaskExecutor ioAsyncTaskExecutor) {
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
    }

    @Bean
    public WebSocketEventListener webSocketEventListener() {
        return new WebSocketEventListener();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue", "/topic");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new AnonymousHandshakeHandler());
        registry.setErrorHandler(new StompSubProtocolErrorHandler());
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(1024 * 1024); // 1MB per message
        registration.setSendTimeLimit(20000); // 20 seconds
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Handles incoming CONNECT, SUBSCRIBE, and SEND from the client
        registration.executor(ioAsyncTaskExecutor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        // Handles outgoing MESSAGE and ERROR frames from the server
        registration.executor(ioAsyncTaskExecutor);
    }
}