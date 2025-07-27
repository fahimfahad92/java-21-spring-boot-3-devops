package rnd.fahim.java21springboot3devops;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class TestController {

    @GetMapping("/ping")
    public ResponseDto ping() {
        return new ResponseDto("pong at " + LocalDateTime.now());
    }

    @GetMapping("/welcome")
    public ResponseDto welcome() {
        return new ResponseDto("Welcome to Spring Boot 3.5 at " + LocalDateTime.now());
    }
}
