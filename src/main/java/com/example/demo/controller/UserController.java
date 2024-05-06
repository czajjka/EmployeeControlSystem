package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/employees")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        userService.addUser(user);

        return"success add user";

    }

    @GetMapping("/getUser")
    public User getUser(@RequestParam Integer id){
        return userService.getUser(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Integer id, @RequestParam User user){
        userService.updateUser(id, user);

        return ResponseEntity.noContent().build();
    }


}





