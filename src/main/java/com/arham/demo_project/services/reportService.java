package com.arham.demo_project.services;

import com.arham.demo_project.model.report;
import com.arham.demo_project.repositry.reportRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class reportService {

    @Autowired
    private reportRepositry repo;

    public List<report> viewbooks(){
        List<report> report= repo.findAll();
        if(! report.isEmpty()) return report;
        else
            throw new NoSuchElementException("Nothing borrowed till now");
    }

    public boolean findUserInReporttable(Long id){
        return repo.finduserbyid(id);
    }

    public List<report> getissuedbook(Long userid){
        return repo.getbooklistofuser(userid);
    }

    public boolean findbookbyid(Long book_id){
        return repo.findbookbyid(book_id);
    }

    public List<report> getbooksinfo(Long book_id){
        return repo.getbooksinfo(book_id);
    }

    public boolean doesbothexistsandhavenotnull(Long userid,Long book_id){
        return repo.doesbothexistsandhavenotnull(userid,book_id);
    }

    public boolean doesbothexistsandhavenull(Long userid,Long book_id){
        return repo.doesbothexistsandhavenull(userid,book_id);
    }

    public void insertentry(report r1){
        repo.save(r1);
    }

    public void updatefine(Long id ,int value){
        repo.updatefine(id,value);
    }

    public Timestamp getissuedate(Long userid,Long book_id){
        return repo.getissuedate(userid,book_id);
    }

    public boolean howManybooks(Long id){
        return repo.howManybooks(id);
    }

    public void updateReturnDate(Long userid, Long book_id){
        repo.updateReturnDate(userid,book_id);
    }
}
