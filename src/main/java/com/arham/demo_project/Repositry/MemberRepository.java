package com.arham.demo_project.Repositry;

import com.arham.demo_project.Model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("Select id from Member m where m.username=:username")
    public Long getId(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("update Member m set m.total_dues = m.total_dues + :value where m.id = :id")
    public void updateTotalDues(@Param("id") Long id, @Param("value") int value);
}
