package com.arham.demo_project.Repositry;

import com.arham.demo_project.Model.Book;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
