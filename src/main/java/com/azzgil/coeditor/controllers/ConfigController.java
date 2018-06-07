package com.azzgil.coeditor.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/config")
public class ConfigController {

    @Value("${coeditor.rest.push_interval}")
    private int pushInterval;

    @Value("${coeditor.rest.fetch_interval}")
    private int fetchInterval;

    @GetMapping("/push_interval")
    public int getPushInterval() {
        return pushInterval;
    }

    @GetMapping("/fetch_interval")
    public int getFetchInterval() {
        return fetchInterval;
    }
}
