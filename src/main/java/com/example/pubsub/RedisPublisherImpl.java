package com.example.pubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicLong;

public class RedisPublisherImpl implements IRedisPublisher {
    private final RedisTemplate< String, String > template;
    private final ChannelTopic topic;
    private final AtomicLong counter = new AtomicLong( 0 );

    public RedisPublisherImpl( final RedisTemplate< String, String > template,
                               final ChannelTopic topic ) {
        this.template = template;
        this.topic = topic;
    }

    public void publish(String msg) {
        System.out.println("Publishing message by Sagar Mal");
        template.convertAndSend( topic.getTopic(), msg );
    }
}