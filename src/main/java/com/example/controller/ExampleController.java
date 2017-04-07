package com.example.controller;

import com.example.domain.Person;
import com.example.services.PersonService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class ExampleController {
    @Autowired
    PersonService personService;
    private static final Logger log = Logger.getLogger(ExampleController.class);

    @RequestMapping(value = "/person/save", method = RequestMethod.GET)
    public Person save() {
        log.info("Creating Person Object and save into the redis.");
        return personService.save();
    }

    @RequestMapping(value = "/person/update", method = RequestMethod.POST)
    public Person update(String name) {
        return personService.update(name);
    }


    @RequestMapping(value = "/person/delete", method = RequestMethod.DELETE)
    public HashMap delete() {
        return personService.delete();
    }

}