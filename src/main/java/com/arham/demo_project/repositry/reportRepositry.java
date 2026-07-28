package com.arham.demo_project.repositry;

import com.arham.demo_project.model.report;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;


@Repository
public interface reportRepositry extends JpaRepository<report, Long> {
    @Query("select case when count(r) > 0 then true else false end " +
            "from report r where r.member_id = :member_id")
    public boolean finduserbyid(@Param("member_id") Long memeber_id);

    @Query("select r from report r where r.member_id = :member_id")
    public List<report> getbooklistofuser(@Param("member_id") Long memeber_id);

    @Query("select count(r)<=2 from report r where r.member_id = :member_id and r.return_date is null")
    public boolean howManybooks(@Param("member_id") Long memeber_id);

    @Query("select case when count(r) > 0 then true else false end " +
            "from report r where r.book_id = :book_id")
    public boolean findbookbyid(@Param("book_id") Long book_id);

    @Query("select r from report r where r.book_id = :book_id")
    public List<report> getbooksinfo(@Param("book_id") Long book_id);

    @Query("select case when count(r) > 0 then true else false end from report r " +
            "where r.book_id = :book_id and r.member_id = :userid and r.return_date is null")
    public boolean doesbothexistsandhavenull(@Param("userid") Long userid, @Param("book_id") Long book_id);

    @Modifying
    @Transactional
    @Query("update report r set r.fine_amount = :value where r.id = :id")
    public int updatefine(@Param("id") Long id, @Param("value") int value);

    @Query("select r.issue_date from report r " +
            "where r.book_id = :book_id and r.member_id = :userid and r.return_date is null")
    public Timestamp getissuedate(@Param("userid") Long userid, @Param("book_id") Long book_id);

    @Modifying
    @Transactional
    @Query("update report r set r.return_date = CURRENT_TIMESTAMP " +
            "where r.member_id = :userid and r.book_id = :book_id")
    public void updateReturnDate(@Param("userid") Long userid, @Param("book_id") Long book_id);
}
