package com.arham.demo_project.controllers;

import com.arham.demo_project.model.member;
import com.arham.demo_project.model.userObject;
import com.arham.demo_project.repositry.memberRepositry;
import com.arham.demo_project.repositry.memberValidation;
import com.arham.demo_project.services.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/library")
public class userController {

    @Autowired
    private memberValidation mepo;

    @Autowired
    private memberRepositry temp;

    @Autowired
    private userService service;

    @GetMapping("/getuser")
    public List<member> getUser(){
        return temp.findAll();
    }

    @PostMapping("/login")
    userObject loginUser(@RequestHeader("Authorization") String authHeader) {
        // authHeader looks like: "Basic YXJoYW06YXJoYW0xMjM="
        userObject user=service.processInfo(authHeader);
        user=service.userValidation(user);
        return user;
    }

    @PostMapping("/users")
    ResponseEntity<Map<String,Object>> addUser(@RequestHeader("Authorization") String authHeader, @RequestBody member comingUser){
        userObject usr=service.processInfo(authHeader);
        usr=service.userValidation(usr);

        comingUser=comingUser.memberValidation(comingUser);
        if("ADMIN".equals(usr.getRole()) && comingUser !=null) {
            service.adduser(comingUser);
            Map<String,Object> response = new LinkedHashMap<>();
            response.put("messsage" , "User added successfully");
            response.put("body" , comingUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you are not authorized to perform this operation");
    }

     @PutMapping("/users/{id}")
     ResponseEntity<Map<String,Object>> updateUser(@RequestHeader("Authorization") String authHeader,@PathVariable Long id,@RequestBody member info){
        userObject usr=service.processInfo(authHeader);
        usr=service.userValidation(usr);

        info=info.memberValidation(info);
        if("ADMIN".equals(usr.getRole()) && info !=null) {
            service.editById(id, info);
            Map<String,Object> response = new LinkedHashMap<>();
            response.put("messsage" , "User added successfully");
            response.put("body" , info);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you are not authorized to perform this operation");
    }

    @DeleteMapping("/users/{id}")
    ResponseEntity<Map<String,Object>>deleteUser(@RequestHeader("Authorization") String authHeader,@PathVariable Long id){
        userObject usr=service.processInfo(authHeader);
        usr=service.userValidation(usr);
        if("ADMIN".equals(usr.getRole())) {
            member user =service.deleteById(id);
            Map<String,Object> response = new LinkedHashMap<>();
            response.put("messsage" , "User deleted successfully");
            response.put("body" , user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you are not authorized to perform this operation");
    }
}
/*
Notes:
 */