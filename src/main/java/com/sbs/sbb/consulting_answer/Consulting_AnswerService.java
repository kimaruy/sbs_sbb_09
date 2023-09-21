package com.sbs.sbb.consulting_answer;

import java.time.LocalDateTime;
import java.util.Optional;

import com.sbs.sbb.answer.Answer;
import com.sbs.sbb.answer.AnswerRepository;
import com.sbs.sbb.center.Center;
import com.sbs.sbb.consulting.Consulting;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.sbs.sbb.DataNotFoundException;
import com.sbs.sbb.center.Center;
import com.sbs.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class Consulting_AnswerService {

    private final Consulting_AnswerRepository consulting_answerRepository;


    public Consulting_Answer create(Consulting consulting, String content, SiteUser author) {
        Consulting_Answer consulting_answer = new Consulting_Answer();
        consulting_answer.setContent(content);
        consulting_answer.setCreateDate(LocalDateTime.now());
        consulting_answer.setConsulting(consulting);
        consulting_answer.setAuthor(author);
        this.consulting_answerRepository.save(consulting_answer);
        return consulting_answer;
    }

    public Consulting_Answer getConsulting_Answer(Integer id) {
        Optional<Consulting_Answer> consulting_answer = this.consulting_answerRepository.findById(id);
        if (consulting_answer.isPresent()) {
            return consulting_answer.get();
        } else {
            throw new DataNotFoundException("consulting_answer not found");
        }
    }

    public void modify(Consulting_Answer consulting_answer, String content) {
        consulting_answer.setContent(content);
        consulting_answer.setModifyDate(LocalDateTime.now());
        this.consulting_answerRepository.save(consulting_answer);
    }

    public void delete(Consulting_Answer consulting_answer) {
        this.consulting_answerRepository.delete(consulting_answer);
    }
    @Transactional
    public void deleteConsultingAnswersByAuthor(SiteUser author) {
        consulting_answerRepository.deleteByAuthor(author);
    }

}