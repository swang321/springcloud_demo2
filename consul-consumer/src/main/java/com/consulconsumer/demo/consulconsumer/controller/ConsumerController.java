package com.consulconsumer.demo.consulconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author whh
 */
@RestController
public class ConsumerController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 获取所有服务
     */
    @RequestMapping("services")
    private Object services() {
        return discoveryClient.getInstances("consul-service-producer");
    }

    /**
     * 从所有服务中选择一个服务（轮询选择）
     */
    @RequestMapping("discover")
    public Object discover() {
        return loadBalancerClient.choose("consul-service-producer").getUri().toString();

        //  返回
        //  http://localhost:8089           http://localhost:8088
        //  两个轮询返回
    }


    @RequestMapping("/call")
    public String call() {

        ServiceInstance serviceInstance = loadBalancerClient.choose("consul-service-producer");
        String url = serviceInstance.getUri().toString();

        System.out.println(serviceInstance.getUri());
        System.out.println(serviceInstance.getServiceId());

        String callServiceResult = new RestTemplate().getForObject(url + "/hello", String.class);

        System.out.println(callServiceResult);

        return callServiceResult;

    }

}
