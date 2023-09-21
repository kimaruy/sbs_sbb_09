package com.sbs.sbb.center;

import com.sbs.sbb.DataNotFoundException;
import com.sbs.sbb.answer.Answer;
import com.sbs.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CenterService {

    private final CenterRepository centerRepository;

    private Specification<Center> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Center> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Center, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Center, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public Page<Center> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Center> spec = search(kw);
        return this.centerRepository.findAll(spec, pageable);
//        return this.questionRepository.findAllByKeyword(kw, pageable);
    }

    public Center getCenter(Integer id) {
        Optional<Center> center = this.centerRepository.findById(id);
        if (center.isPresent()) {
            return center.get();
        } else {
            throw new DataNotFoundException("center not found");
        }
    }

    public void create(String subject, String content, SiteUser user) {
        Center q = new Center();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.centerRepository.save(q);
    }

    public void modify(Center center, String subject, String content) {
        center.setSubject(subject);
        center.setContent(content);
        center.setModifyDate(LocalDateTime.now());
        this.centerRepository.save(center);
    }

    public void delete(Center center) {
        this.centerRepository.delete(center);
    }

    public void vote(Center center, SiteUser siteUser) {
        center.getVoter().add(siteUser);
        this.centerRepository.save(center);
    }
}