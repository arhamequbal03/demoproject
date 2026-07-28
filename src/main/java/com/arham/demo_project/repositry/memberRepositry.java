package com.arham.demo_project.repositry;

import com.arham.demo_project.model.member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface memberRepositry extends JpaRepository<member, Long> {

    @Query("Select id from member m where m.username=:username")
    public Long getId(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("update member m set m.total_dues = m.total_dues + :value where m.id = :id")
    public void updateTotalDues(@Param("id") Long id, @Param("value") int value);
}
