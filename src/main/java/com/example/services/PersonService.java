package com.example.services;

import com.example.domain.Person;
import com.example.pubsub.IRedisPublisher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PersonService {
    IRedisPublisher iRedisPublisher;

    PersonService(IRedisPublisher iRedisPublisher) {
        this.iRedisPublisher = iRedisPublisher;
    }

    @Cacheable(value = "person", key = "'persons'")
    public Person save() {
        Person person = new Person();
        person.setName("shyam");
        person.setAge(25);
        person.setDept("Computer Dept");
        person.setSalary(27500);
        iRedisPublisher.publish("new persons key is save");
        return person;
    }

    @CachePut(value = "person", key = "'persons'")
    public Person update(String name) {
        Person person = new Person();
        person.setName(name);
        person.setAge(25);
        person.setDept("Computer Dept");
        person.setSalary(27500);
        iRedisPublisher.publish("persons key is updated");
        return person;
    }

    @CacheEvict(value = "person", key = "'persons'")
    public HashMap delete() {
        HashMap map = new HashMap();
        map.put("status", true);
        map.put("message", "Deleted successfully");
        iRedisPublisher.publish("persons key is deleted");
        return map;
    }
}
