package com.sbs.sbb.consulting;

import com.sbs.sbb.consulting.Consulting;
import com.sbs.sbb.user.SiteUser;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConsultingRepository extends JpaRepository<Consulting, Integer> {
    Consulting findBySubject(String subject);
    Consulting findBySubjectAndContent(String subject, String content);
    List<Consulting> findBySubjectLike(String subject);
    Page<Consulting> findAll(Pageable pageable);
    Page<Consulting> findAll(Specification<Consulting> spec, Pageable pageable);

    @Query("select "
            + "distinct q "
            + "from Consulting q "
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Consulting_Answer a on a.consulting=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% ")
    Page<Consulting> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

    @Transactional
    void deleteByAuthor(SiteUser author);
}
