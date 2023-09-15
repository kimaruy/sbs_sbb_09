package com.sbs.sbb.center;

import com.sbs.sbb.center.Center;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CenterRepository extends JpaRepository<Center, Integer> {
    Center findBySubject(String subject);
    Center findBySubjectAndContent(String subject, String content);
    List<Center> findBySubjectLike(String subject);
    Page<Center> findAll(Pageable pageable);
    Page<Center> findAll(Specification<Center> spec, Pageable pageable);

    @Query("select "
            + "distinct q "
            + "from Center q "
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Answer a on a.center=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% ")
    Page<Center> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}