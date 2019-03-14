# spring cloud demo2

#### 介绍
spring cloud 练习

参考    http://www.ityouknow.com/spring-cloud.html

#### 使用说明

11   注册中心 Consul 
    
    1   下载 Consul  安装 （linux   win 都行）
    2   启动 consul， 进入consul目录,命令 consul agent -dev (这个好像启动是以客户端启动的)
    3   访问  http://localhost:8500  consul主页
    4    新建 module consul-provider  启动类添加注解    @EnableDiscoveryClient    添加依赖
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-actuator</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-web</artifactId>
                </dependency>
    5   添加配置 
        server:
          port: 8088
        spring:
          cloud:
            consul:
              host: localhost   consule 的ip
              port: 8500        consule 的 port   （可以自己修改）
              discovery:
                service-name: consul-service-producer       #  注册到注册中心的名字  servicesId
    6   新建 controller  提供一个接口 (/hello)  返回  hello consul
    7   新建 module 和 consul-provider一样   把 server.port 改成8089    提供一个接口 (/hello)  返回  22222222222222   helle consul
        
                我是这样启动 consul 才可以把 两个module 注册到 consul 去
                agent -server -bootstrap-expect=1 -data-dir=C:\anew_project\env\consul_1.4.3_windows_amd64 -node=consul
                -bind=127.0.0.1 -ui -client=0.0.0.0
    8   新建 module consul-consumer 和 maven 依赖和 consul-provider一样
    9   修改配置文件
        server:
          port: 9000
        spring:
          application:
            name: consule-consumer
          cloud:
            consul:
              host: localhost
              port: 8500
              discovery:
                register: false     # 设置不需要注册到 consul 中
    10  添加controller
        
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

    11  查看 consul-provider都注册上去了，然后访问
           http://localhost:9000/services           返回consul-provider服务的信息
           http://localhost:9000/discover           http://localhost:8088 http://localhost:8089  两个轮询返回
           http://localhost:9000/call               helle consul  和   22222222222222 helle consul  两个轮询返回
           

12  gateway-route   路由

    1   依赖  版本不匹配会报错
               <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-gateway</artifactId>
                </dependency>      
    2   yml  配置 
            server:
              port: 9001
            spring:
              cloud:
                gateway:
                  routes:
                  - id: gateway-route
                    uri: http://localhost:8088
                    predicates:
                    - Path=/hello
              
              #  上面这段配置的意思是，配置了一个 id 为 gateway-route 的路由规则，
              #  当访问地址 http://localhost:9001/hello 时会自动转发到地址：
              #  http://localhost:8088/hello
              
              #  id：我们自定义的路由 ID，保持唯一
              #  uri：目标服务地址
              #  predicates：路由条件，Predicate 接受一个输入参数，返回一个布尔值结果。该接口包含多种默认方法来将 Predicate 组合成其他复杂的逻辑（比如：与，或，非）。
              #  filters：过滤规则，本示例暂时没用。

    3   用 @bean  也可以实现 路由转发 eg:
            
               @Bean
                public RouteLocator routeLocator(RouteLocatorBuilder builder) {
            
                    return builder.routes()
                            .route("gateway-route1", r -> r.path("/about").uri("http://ityouknow.com"))
                            .build();
                }
    4   路由规则
           1    通过时间匹配                  在哪个时间之前或者之后才会经行转发   在那个时间端内进行 路由转发
           2    通过 Cookie 匹配             Cookie Route Predicate  可以接受两个参数  cookie name  和   正则  这两个匹配额进行路由
           3    通过 Header 属性匹配          Header Route Predicate  可以接受两个参数  header 中属性名称和一个正则    这两个匹配额进行路由
           4    通过 Host 匹配               Host Route Predicate   接收一组参数      一组匹配的域名列表
           5    通过请求方式匹配               POST、GET、PUT、DELETE    4中方式路由
           6    通过请求路径匹配               Path Route Predicate         接收一个匹配路径的参数来判断是否走路由。
           7    通过请求参数匹配            Query Route Predicate 支持传入两个参数，一个是属性名一个为属性值，属性值可以是正则表达式。
           8    通过请求 ip 地址进行匹配
           9    组合使用    1-8  组合使用
           