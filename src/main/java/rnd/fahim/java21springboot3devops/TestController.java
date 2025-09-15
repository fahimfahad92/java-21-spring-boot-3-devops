package rnd.fahim.java21springboot3devops;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.time.LocalDateTime;

@RestController
public class TestController {

    @GetMapping("/ping")
    public ResponseDto ping() {
        return new ResponseDto("pong at " + LocalDateTime.now());
    }

    @GetMapping("/welcome")
    public ResponseDto welcome() {
        return new ResponseDto("Welcome to Spring Boot 3.5 at " + LocalDateTime.now() + " IP address " + getLocalIpAddress());
    }

    String getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "NONE";
        }
    }
}
