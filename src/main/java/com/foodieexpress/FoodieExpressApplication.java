package com.foodieexpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoodieExpressApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodieExpressApplication.class, args);
        System.out.println("==================================================");
        System.out.println(" FoodieExpress Backend is running on port 8080! ");
        System.out.println(" Test API: http://localhost:8080/api/health       ");
        System.out.println(" Web UI:   http://localhost:8080/                ");
        System.out.println("==================================================");
    }
}

