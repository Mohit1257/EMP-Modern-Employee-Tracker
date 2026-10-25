package com.in.emptrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmpTrackApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmpTrackApplication.class, args);
      
        System.err.println("Application started at: http://localhost:8080");
    }
}
