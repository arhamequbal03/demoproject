package com.arham.demo_project.Controllers;
import com.arham.demo_project.Model.Book;
import com.arham.demo_project.Model.UserObject;
import com.arham.demo_project.Services.BookService;
import com.arham.demo_project.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/library")
public class BookController {

    private final BookService service;
    private final UserService userservice;

    public BookController(BookService service, UserService userservice) {
        this.service = service;
        this.userservice = userservice;
    }

    @GetMapping("/books")
    List<Book> getBooks(@RequestHeader("Authorization") String authHeader){
        UserObject usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        return service.getAllBooks();
    }

    @GetMapping("/books/{id}")
    Book getBookById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        UserObject usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        return service.getBook(id);
    }

    @PostMapping("/books")
    ResponseEntity<Map<String, Object>> addBooks(@RequestHeader("Authorization") String authHeader,@Valid @RequestBody Book newbook) {
        UserObject usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);

        if("ADMIN".equalsIgnoreCase(usr.getRole()) && newbook.bookValidator(newbook)){
            Book saved = service.addBook(newbook);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Book added successfully");
            response.put("book", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not authorized to perform this operation");
    }

    @PutMapping("/books/{id}")
    ResponseEntity<Map<String, Object>> editBook(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody Book info, @PathVariable Long id){
        UserObject usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);

        if("ADMIN".equalsIgnoreCase(usr.getRole()) && info.bookValidator(info)) {
            Book saved = service.edit(id,info);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Book modified successfully");
            response.put("book", saved);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not authorized to perform this operation");
    }

    @DeleteMapping("/books/{id}")
    ResponseEntity<Map<String, Object>> deleteBook(@RequestHeader("Authorization") String authHeader,@PathVariable Long id){
        UserObject usr=userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);

        if("ADMIN".equalsIgnoreCase(usr.getRole())) {
            Book deleted= service.delete(id);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Book deleted successfully");
            response.put("book", deleted);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete books");
    }
}