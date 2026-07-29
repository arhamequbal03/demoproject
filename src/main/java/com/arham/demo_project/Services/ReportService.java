package com.arham.demo_project.Services;

import com.arham.demo_project.Model.Report;
import com.arham.demo_project.Repositry.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReportService {

    @Autowired
    private ReportRepository repo;

    public List<Report> viewbooks(){
        List<Report> report= repo.findAll();
        if(! report.isEmpty()) return report;
        else
            throw new NoSuchElementException("Nothing borrowed till now");
    }

    public boolean findUserInReporttable(Long id){
        return repo.finduserbyid(id);
    }

    public List<Report> getissuedbook(Long userid){
        return repo.getbooklistofuser(userid);
    }

    public boolean findbookbyid(Long book_id){
        return repo.findbookbyid(book_id);
    }

    public List<Report> getbooksinfo(Long book_id){
        return repo.getbooksinfo(book_id);
    }

    public boolean doesbothexistsandhavenull(Long userid,Long book_id){
        return repo.doesbothexistsandhavenull(userid,book_id);
    }

    public void insertentry(Report r1){
        repo.save(r1);
    }

    public void updatefine(Long member_id, Long book_id, int value){
        repo.updatefine(member_id, book_id, value);
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
