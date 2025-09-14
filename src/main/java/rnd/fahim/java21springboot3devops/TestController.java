package rnd.fahim.java21springboot3devops;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class TestController {

    @GetMapping("/ping")
    public ResponseDto ping() {
        return new ResponseDto("pong at " + LocalDateTime.now());
    }

    @GetMapping("/welcome")
    public ResponseDto welcome() {
        String ip = "NONE";
        try {
            InetAddress ipAddress = InetAddress.getLocalHost();
            ip = ipAddress.getHostAddress();
        } catch (UnknownHostException e) {
            // Handle the exception, e.g., log it or return a default value
           
        }
        
        return new ResponseDto("Welcome to Spring Boot 3.5 at " + LocalDateTime.now() + " IP address " + ip);
    }
}
