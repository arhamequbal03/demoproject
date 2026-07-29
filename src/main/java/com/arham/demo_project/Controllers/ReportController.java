package com.arham.demo_project.Controllers;
import com.arham.demo_project.model.Report;
import com.arham.demo_project.model.UserObject;
import com.arham.demo_project.services.BookService;
import com.arham.demo_project.services.ReportService;
import com.arham.demo_project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/library")
public class ReportController {

    @Autowired
    private ReportService service;

    @Autowired
    private BookService bookservice;

    @Autowired
    private UserService userservice;

    private UserObject usr=null;

    @GetMapping("/reports")
    List<Report> viewIssuedAllBooks(@RequestHeader("Authorization") String authHeader){
        // only admin can see this info
        usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        if("ADMIN".equals(usr.getRole()))
            return service.viewbooks();
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you are not authorized to perform this operation");
    }

    @GetMapping("/reports/{userid}")
    List<Report> getBookByUserId(@RequestHeader("Authorization") String authHeader, @PathVariable Long userid){
        // books that user possess, this info what books are issued
        usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        // member ke auth se wo kisi k bhi dekh skta hai
        if("MEMBER".equals(usr.getRole())){
            if(userid != userservice.getId(usr.getName())){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you are not authorized to perform this action");
            }
        }
        if(service.findUserInReporttable(userid))
            return service.getissuedbook(userid);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User has not issued any book or Invalid id");
    }

    @GetMapping("/reports/v1/{book_id}")
    List<Report> viewBookStatus(@RequestHeader("Authorization") String authHeader, @PathVariable Long book_id){
        // admin- to see the books status
        usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        if(! "ADMIN".equals(usr.getRole())) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"you are not authorized");
        if(! service.findbookbyid(book_id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No entries exist");
        return service.getbooksinfo(book_id);
    }

    @PostMapping ("/reports")
    ResponseEntity<Map<String, Object>> issueBook(@RequestHeader("Authorization") String authHeader, @RequestBody Report r1) {
        // from requestbody u will get userId and bookId - only admin
        usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);

        if("ADMIN".equals(usr.getRole())){
            if(!bookservice.isavailable(r1.getBook_id())) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"book is unavailable");
            if(!service.howManybooks(r1.getMember_id()))  throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Borrow Limit already reached");
            if(service.doesbothexistsandhavenull(r1.getMember_id(),r1.getBook_id()))
                throw new ResponseStatusException(HttpStatus.FOUND,"Book is already with user");
            if(!userservice.validuser(r1.getMember_id())) throw new ResponseStatusException(HttpStatus.FOUND,"Invalid user");
            bookservice.increaseIssueQuantity(r1.getBook_id());
            service.insertentry(r1);
            Map<String ,Object> response= new LinkedHashMap<>();
            response.put("message","Book issued successfully");
            response.put("body",r1);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you are not authorized to perform this operation");
    }

    @PostMapping("/reports/{id}")
    ResponseEntity<Map<String,Object>> returnBook(@PathVariable("id") Long id,@RequestHeader("Authorization") String authHeader, @RequestBody Report r1){
        // get the userid in query
        // System.out.println("entered");
        usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        r1.setMember_id(id);
        if ("ADMIN".equals(usr.getRole())) {
            if (!service.doesbothexistsandhavenull(r1.getMember_id(), r1.getBook_id()))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not issued this book");
            if(!userservice.validuser(r1.getMember_id()))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid user");

            Timestamp issued = service.getissuedate(r1.getMember_id(), r1.getBook_id());
            bookservice.decreaseIssueQuantity(r1.getBook_id());
            long datediff = ChronoUnit.DAYS.between(issued.toLocalDateTime(), LocalDateTime.now());
            int value = (int) Math.max(0, datediff - 14) * 2;
            service.updatefine(r1.getMember_id(), value);
            userservice.updateTotalDues(r1.getMember_id(), value);
            service.updateReturnDate(r1.getMember_id(), r1.getBook_id());
            Map<String ,Object> response= new LinkedHashMap<>();
            response.put("message","Book returned successfully");
            response.put("body","member_id:"+r1.getMember_id()+", book_id : "+ r1.getBook_id()) ;
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not authorized to perform this operation");
        }
    }
}
/*
Caveats in implementation
Note:
    if book is not returned we should not show fine and return date
 */