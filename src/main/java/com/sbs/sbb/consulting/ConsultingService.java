package com.sbs.sbb.consulting;

import com.sbs.sbb.DataNotFoundException;
import com.sbs.sbb.center.Center;
import com.sbs.sbb.consulting_answer.Consulting_Answer;
import com.sbs.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConsultingService {

    private final ConsultingRepository consultingRepository;

    private Specification<Consulting> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Consulting> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Consulting, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Consulting, Consulting_Answer> a = q.join("consulting_answerList", JoinType.LEFT);
                Join<Consulting_Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public Page<Consulting> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Consulting> spec = search(kw);
        return this.consultingRepository.findAll(spec, pageable);
    }

    public Consulting getConsulting(Integer id) {
        Optional<Consulting> consulting = this.consultingRepository.findById(id);
        if (consulting.isPresent()) {
            return consulting.get();
        } else {
            throw new DataNotFoundException("consulting not found");
        }
    }

    public void create(String subject, String content, SiteUser user) {
        Consulting q = new Consulting();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.consultingRepository.save(q);
    }

    public void modify(Consulting consulting, String subject, String content) {
        consulting.setSubject(subject);
        consulting.setContent(content);
        consulting.setModifyDate(LocalDateTime.now());
        this.consultingRepository.save(consulting);
    }

    public void delete(Consulting consulting) {
        this.consultingRepository.delete(consulting);
    }

    @Transactional
    public void deleteConsultingByAuthor(SiteUser author) {
        consultingRepository.deleteByAuthor(author);
    }

}