package com.consulprovider2.demo.consulprovider2.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author whh
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello() {
        return "22222222222222   helle consul";
    }

}
