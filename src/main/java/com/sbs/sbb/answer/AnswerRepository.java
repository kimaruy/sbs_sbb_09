package com.sbs.sbb.answer;

import com.sbs.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    List<Answer> findByAuthor(SiteUser siteUser);
}