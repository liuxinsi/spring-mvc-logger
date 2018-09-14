# spring-mvc-logger
## Features

- **Logging**: Log requests and responses.
- **Custom Layout**: Your own custom Layout
- **Web-Flux** : 支持Netty的Reactive接口(注：没在生产环境下大规模的验证，慎用，2018-09-13在测。)。

## Sample
   GET Request
    
```

--------------------------------------------------------------- 
ID：3 
URL：http://127.0.0.1:8080/?a=456 
Method：GET 
Headers：{accept-language=zh-CN,zh;q=0.8, cookie=__utma=96992031.1401590855.1439100953.1439100953.1439100953.1, host=127.0.0.1:8080, upgrade-insecure-requests=1, connection=keep-alive, accept-encoding=gzip, deflate, sdch, br, user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36, accept=text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8} 
Payload： 
---------------------------------------------------------------

```
Response
```

--------------------------------------------------------------- 
ID：3 
RespCode：200 
Headers：{} 
Payload：test:456 
---------------------------------------------------------------

```

   Post Request
    
```

--------------------------------------------------------------- 
ID：4 
URL：http://127.0.0.1:8080/test2 
Method：POST 
Headers：{content-length=15, host=127.0.0.1:8080, content-type=application/json; charset=utf-8, connection=close, user-agent=Paw/3.0.14 (Macintosh; OS X/10.12.2) GCDHTTPRequest} 
Payload：{"name":"test"} 
---------------------------------------------------------------

```
Response
```

--------------------------------------------------------------- 
ID：4 
RespCode：200 
Headers：{} 
Payload：{"code":"123456","name":"test"} 
---------------------------------------------------------------

```

## How to use
### Maven Import
```
 <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
 </repositories>


 <dependencies>
    <dependency>
         <groupId>com.github.liuxinsi</groupId>
         <artifactId>spring-mvc-logger</artifactId>
         <version>1.2</version>
    </dependency>
 </dependencies>
```

```

    @Bean
    public Filter filter() {
        return new com.lxs.sml.filter.LoggingFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(Test.class, args);
    }
    
```

Reactive
```

    @Bean
    @Order(0)
    public Filter filter() {
        return new com.lxs.sml.filter.reactive.LoggingFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(Test.class, args);
    }
    
```

Or add to Web.xml

## Custom Layout
Default layout

```

 private static String DEFAULT_REQ_LAYOUT = "Request：[%r][%flag] [%r]ID：[%id] [%r]URL：[%url] [%r]Method：[%method] [%r]Headers：[%header] [%r]Payload：[%payload] [%r][%flag]";
 private static String DEFAULT_RESP_LAYOUT = "Response：[%r][%flag] [%r]ID：[%id] [%r]RespCode：[%status] [%r]Headers：[%header] [%r]Payload：[%payload] [%r][%flag]";

```

you can change the default layout 
```

    @Bean
    public Filter filter() {
        LoggingFormat.setDefaultReqLayout("收到请求：[%r]ID：[%id] [%r]URL：[%url] [%r]");
        return new com.lxs.sml.filter.LoggingFilter();
    }
```
output
```

21:15:54.766 [http-nio-8080-exec-1] DEBUG com.lxs.sml.filter.LoggingFilter - 收到请求：
ID：2 
URL：http://127.0.0.1:8080/?a=456 

```

## 
| Conversion Word    | Description                                            |
| ------------------:| -------------------------------------------------------|
| `[%r]`             | crlf. default: System.getProperty("line.separator").   |
| `[%flag]`          | line.                                                  |
| `[%id]`            | increment id,pre request/response has the same number. |
| `[%url]`           | request url.                                           |
| `[%method]`        | request method.                                        |
| `[%header]`        | http headers.                                          |
| `[%payload]`       | payload.                                               |
| `[%status]`        | http status code of a response.                        |
