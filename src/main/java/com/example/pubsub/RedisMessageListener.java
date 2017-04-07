package com.example.pubsub;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class RedisMessageListener implements MessageListener {
    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        System.out.println("Received Message as: " + new String(message.getBody()));
//        System.out.println("Message received: " + message.toString());
    }
}
