package com.arham.demo_project.Controllers;
import com.arham.demo_project.Model.Report;
import com.arham.demo_project.Model.UserObject;
import com.arham.demo_project.Services.CustomMessage;
import com.arham.demo_project.Services.ReportService;
import com.arham.demo_project.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/library")
public class ReportController {

    @Autowired
    private ReportService service;
    @Autowired
    private UserService userservice;
    @Autowired
    private CustomMessage MessageService;

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

        // Member is not authorized to see anyone's report
        if("MEMBER".equalsIgnoreCase(usr.getRole())){
            if(!userid.equals(userservice.getId(usr.getName()))){
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
        Map<String,Object> response=MessageService.GenerateCustomReport(reports);

        if(response.get("message").equals("No record found"))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping ("/reports")
    ResponseEntity<Map<String, Object>> issueBook(@RequestHeader("Authorization") String authHeader,@Valid @RequestBody Report r1) {
        // from requestbody u will get userId and bookId - only admin
        UserObject usr= userservice.processInfo(authHeader);
        usr=userservice.userValidation(usr);

        if("ADMIN".equalsIgnoreCase(usr.getRole())){
            Long MemberId=r1.getMember_id();
            if(MemberId==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"member_id is required");
            userservice.validuser(MemberId); // throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid user");
            // availability, borrow-limit and duplicate checks now run inside the transaction
            Report saved = service.issueBook(r1);
            Map<String,Object> response= MessageService.GenerateIssueMessage(saved);
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
            Long MemberId=r1.getMember_id();
            Long BookId=r1.getBook_id();
            if(!userservice.validuser(MemberId))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid user");
            if (!service.doesbothexistsandhavenull(MemberId, BookId))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not issued this book");

            service.returnBook(MemberId, BookId);
            Map<String ,Object> response= MessageService.GenerateReturnMessage(MemberId,BookId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not authorized to perform this operation");
        }
    }
}