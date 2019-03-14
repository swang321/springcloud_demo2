package com.gatewayroute.demo.gatewayroute.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author whh
 */
@RestController
public class RouteController {


    @RequestMapping("/hello/route")
    public String hello() {
        return "hello route";
    }


}
