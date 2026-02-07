package com.example.staffguard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;   // <--- THIS WAS MISSING
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public String getMe() {
        // In real project, return logged-in user info
        return "This is a protected endpoint. User info goes here.";
    }
}
