package com.oliveiradev.rest_with_boot_and_java.rest_with_boot_and_java;

import org.springframework.web.bind.annotation.RestController;

@RestController 
public class GreentingController {
    private static final String template = "Hello, %world";
}
