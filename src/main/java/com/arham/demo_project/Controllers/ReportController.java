package com.arham.demo_project.Controllers;
import com.arham.demo_project.Model.Report;
import com.arham.demo_project.Model.UserObject;
import com.arham.demo_project.Services.BookService;
import com.arham.demo_project.Services.ReportService;
import com.arham.demo_project.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    @GetMapping("/reports")
    List<Report> viewIssuedAllBooks(@RequestHeader("Authorization") String authHeader){
        // only admin can see this info
        UserObject usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        if("ADMIN".equalsIgnoreCase(usr.getRole()))
            return service.viewbooks();
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"you are not authorized to perform this operation");
    }

    @GetMapping("/reports/{userid}")
    List<Report> getBookByUserId(@RequestHeader("Authorization") String authHeader, @PathVariable Long userid){
        // books that user possess, this info what books are issued
        UserObject usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        // member ke auth se wo kisi k bhi dekh skta hai
        if("MEMBER".equalsIgnoreCase(usr.getRole())){
            if(userid != userservice.getId(usr.getName())){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"you are not authorized to perform this action");
            }
        }
        if(service.findUserInReporttable(userid))
            return service.getissuedbook(userid);
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User has not issued any book or Invalid id");
    }

    @GetMapping("/reports/v1/{book_id}")
    ResponseEntity<Map<String,Object>> viewBookStatus(@RequestHeader("Authorization") String authHeader, @PathVariable Long book_id){
        // admin- to see the books status
        UserObject usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        if(! "ADMIN".equalsIgnoreCase(usr.getRole())) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"you are not authorized");
        if(! service.findbookbyid(book_id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No entries exist");

        List<Report> reports= service.getbooksinfo(book_id);
        Map<String,Object> response=new LinkedHashMap<>();
        if(reports.isEmpty()){
            response.put("message","NO record found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("message","Records found");
        List<Map<String, Object>> reportList = new ArrayList<>();
        for (Report report : reports) {
            Map<String, Object> reportMap = new LinkedHashMap<>();
            reportMap.put("id", report.getBorrow_id());
            reportMap.put("book_id", report.getBook_id());
            reportMap.put("member_id", report.getMember_id());
            reportMap.put("issue_date", report.getIssue_date());

            if (report.getReturn_date() != null) {
                reportMap.put("return_date", report.getReturn_date());
                reportMap.put("fine_amount", report.getFine_amount());
            }
            reportList.add(reportMap);
        }

        response.put("Report",reportList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping ("/reports")
    ResponseEntity<Map<String, Object>> issueBook(@RequestHeader("Authorization") String authHeader,@Valid @RequestBody Report r1) {
        // from requestbody u will get userId and bookId - only admin
        UserObject usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);

        if("ADMIN".equalsIgnoreCase(usr.getRole())){
            if(r1.getMember_id()==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"member_id is required");
            if(!userservice.validuser(r1.getMember_id())) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid user");
            if(!bookservice.isavailable(r1.getBook_id())) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"book is unavailable");
            if(!service.howManybooks(r1.getMember_id()))  throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Borrow Limit already reached");
            if(service.doesbothexistsandhavenull(r1.getMember_id(),r1.getBook_id()))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Book is already with user");
            r1.setBorrow_id(null);
            r1.setReturn_date(null);
            r1.setFine_amount(0);
            bookservice.increaseIssueQuantity(r1.getBook_id());
            service.insertentry(r1);
            Map<String ,Object> response= new LinkedHashMap<>();
            response.put("message","Book issued successfully");
            response.put("body",r1);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"you are not authorized to perform this operation");
    }

    @PostMapping("/reports/{id}")
    ResponseEntity<Map<String,Object>> returnBook(@PathVariable("id") Long id,@RequestHeader("Authorization") String authHeader,@Valid @RequestBody Report r1){
        // get the userid in query
        // System.out.println("entered");
        UserObject usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);
        r1.setMember_id(id);
        if ("ADMIN".equalsIgnoreCase(usr.getRole())) {
            if(!userservice.validuser(r1.getMember_id()))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid user");
            if (!service.doesbothexistsandhavenull(r1.getMember_id(), r1.getBook_id()))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not issued this book");

            Timestamp issued = service.getissuedate(r1.getMember_id(), r1.getBook_id());
            bookservice.decreaseIssueQuantity(r1.getBook_id());
            long datediff = ChronoUnit.DAYS.between(issued.toLocalDateTime(), LocalDateTime.now());
            int value = (int) Math.max(0, datediff - 14) * 2;
            service.updatefine(r1.getMember_id(), r1.getBook_id(), value);
            userservice.updateTotalDues(r1.getMember_id(), value);
            service.updateReturnDate(r1.getMember_id(), r1.getBook_id());
            Map<String ,Object> response= new LinkedHashMap<>();
            response.put("message","Book returned successfully");
            response.put("body","member_id:"+r1.getMember_id()+", book_id : "+ r1.getBook_id()) ;
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not authorized to perform this operation");
        }
    }
}