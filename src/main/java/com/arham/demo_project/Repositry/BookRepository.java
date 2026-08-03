package com.arham.demo_project.Repositry;

import com.arham.demo_project.Model.Book;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("select case when count(b) > 0 then true else false end " +
            "from Book b where b.id = :id")
    boolean validate(@Param("id") Long id);

    // Locks the book row (SELECT ... FOR UPDATE) so concurrent issue/return
    // requests serialize on it — the second one waits until the first commits.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") Long id);

    @Query("select case when count(b) > 0 then true else false end " +
            "from Book b where b.id = :id and b.total_quantity > b.issue_quantity")
    boolean isavailable(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update Book b set b.issue_quantity = b.issue_quantity + 1 where b.id = :id")
    void increaseIssueQuantity(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update Book b set b.issue_quantity = b.issue_quantity - 1 where b.id = :id")
    void decreaseIssueQuantity(@Param("id") Long id);

}
