package com.linkdatabase.prog3jdbc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "API Spring Boot opérationnelle !";
    }
}
