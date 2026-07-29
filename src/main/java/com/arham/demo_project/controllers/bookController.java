package com.arham.demo_project.controllers;
import com.arham.demo_project.model.book;
import com.arham.demo_project.model.userObject;
import com.arham.demo_project.services.bookService;
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
public class bookController {

    @Autowired
    private bookService service;
    @Autowired
    private userService userservice;
    private userObject usr=null;

    @GetMapping("/books")
    List<book> getBooks(@RequestHeader("Authorization") String authHeader){
        usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        return service.getAllBooks();
    }

    @GetMapping("/books/{id}")
    book getBookById(@RequestHeader("Authorization") String authHeader,@PathVariable Long id){
        usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        return service.getBook(id);
    }

    @PostMapping("/books")
    ResponseEntity<Map<String, Object>> addBooks(@RequestHeader("Authorization") String authHeader, @RequestBody book newbook) {
        usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);

        if("ADMIN".equals(usr.getRole()) && newbook.bookValidator(newbook)){
            book saved = service.addBook(newbook);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Book added successfully");
            response.put("book", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else
            throw new IllegalArgumentException("Either not authorized or invalid entry");
    }

    @PutMapping("/books/{id}")
    ResponseEntity<Map<String, Object>> editBook(@RequestHeader("Authorization") String authHeader,@RequestBody book info, @PathVariable Long id){
        usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);

        if("ADMIN".equals(usr.getRole()) && info.bookValidator(info)) {
            book saved = service.edit(id,info);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Book modified successfully");
            response.put("book", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else
            throw new IllegalArgumentException("Either not authorized or invalid entry");
    }

    @DeleteMapping("/books/{id}")
    ResponseEntity<Map<String, Object>> deleteBook(@RequestHeader("Authorization") String authHeader,@PathVariable Long id){
        usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);

        if("ADMIN".equals(usr.getRole())) {
            book deleted= service.delete(id);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Book deleted successfully");
            response.put("book", deleted);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete books");
    }
}