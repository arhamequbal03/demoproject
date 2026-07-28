package com.arham.demo_project.repositry;

import com.arham.demo_project.model.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface bookrRepositry extends JpaRepository<book, Long> {
    @Query("select case when count(b) > 0 then true else false end " +
            "from book b where b.id = :id")
    boolean validate(@Param("id") Long id);

    @Query("select case when count(b) > 0 then true else false end " +
            "from book b where b.id = :id and b.total_quantity > b.issue_quantity")
    boolean isavailable(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update book b set b.issue_quantity = b.issue_quantity + 1 where b.id = :id")
    void increaseissuequatity(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update book b set b.issue_quantity = b.issue_quantity - 1 where b.id = :id")
    void decreaseissuequatity(@Param("id") Long id);

}
