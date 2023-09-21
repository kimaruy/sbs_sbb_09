package com.sbs.sbb.consulting_answer;

import com.sbs.sbb.consulting_answer.Consulting_Answer;
import com.sbs.sbb.user.SiteUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Consulting_AnswerRepository extends JpaRepository<Consulting_Answer, Integer> {
    @Transactional
    void deleteByAuthor(SiteUser author);
}