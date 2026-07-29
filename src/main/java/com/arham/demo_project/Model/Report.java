package com.arham.demo_project.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long borrow_id;

    private Long book_id;
    private Long member_id;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp issue_date;
    private Timestamp return_date;
    private float fine_amount;

    public Long getBook_id() {
        return book_id;
    }

    public void setBook_id(Long book_id) {
        this.book_id = book_id;
    }

    public Long getBorrow_id() {
        return borrow_id;
    }

    public void setBorrow_id(Long borrow_id) {
        this.borrow_id = borrow_id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public Timestamp getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(Timestamp issue_date) {
        this.issue_date = issue_date;
    }

    public Timestamp getReturn_date() {
        return return_date;
    }

    public void setReturn_date(Timestamp return_date) {
        this.return_date = return_date;
    }

    public float getFine_amount() {
        return fine_amount;
    }

    public void setFine_amount(float fine_amount) {
        this.fine_amount = fine_amount;
    }
}
