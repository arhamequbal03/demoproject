package com.arham.demo_project.Repositry;

import com.arham.demo_project.Model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberValidation extends JpaRepository<Member, Long> {
    @Query("select case when count(m) > 0 then true else false end " +
            "from Member m where m.username = :username")
    Boolean isMember(@Param("username") String username);

    @Query("select case when count(m) > 0 then true else false end " +
            "from Member m where m.username = :username and m.password = :password")
    Boolean passwordchecker(@Param("username") String username,
                            @Param("password") String password);

    @Query("select case when count(m) > 0 then true else false end " +
            "from Member m where m.username = :username and m.role = 'ADMIN'")
    Boolean setrole(@Param("username") String username);
}
