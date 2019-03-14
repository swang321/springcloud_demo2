package com.consuleprovider.demo.consulprovider.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author whh
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello() {
        return "hello consul";
    }

}
