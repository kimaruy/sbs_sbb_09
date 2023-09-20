package com.sbs.sbb.consulting_answer;

import com.sbs.sbb.consulting.Consulting;
import com.sbs.sbb.consulting.ConsultingService;
import com.sbs.sbb.user.SiteUser;
import com.sbs.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/consulting_answer")
@RequiredArgsConstructor
@Controller
public class Consulting_AnswerController {

    private final ConsultingService consultingService;
    private final Consulting_AnswerService consulting_answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id,
                               @Valid Consulting_AnswerForm consulting_answerForm, BindingResult bindingResult, Principal principal) {
        Consulting consulting = this.consultingService.getConsulting(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("Consulting", consulting);
            return "consulting_detail";
        }
        Consulting_Answer consulting_answer = this.consulting_answerService.create(consulting,
                consulting_answerForm.getContent(), siteUser);
        return String.format("redirect:/consulting/detail/%s#consulting_answer_%s",
                consulting_answer.getConsulting().getId(), consulting_answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String consulting_answerModify(Consulting_AnswerForm consulting_answerForm, @PathVariable("id") Integer id, Principal principal) {
        Consulting_Answer consulting_answer = this.consulting_answerService.getConsulting_Answer(id);
        if (!consulting_answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        consulting_answerForm.setContent(consulting_answer.getContent());
        return "consulting_answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String consulting_answerModify(@Valid Consulting_AnswerForm consulting_answerForm, BindingResult bindingResult,
                                          @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "consulting_answer_form";
        }
        Consulting_Answer consulting_answer = this.consulting_answerService.getConsulting_Answer(id);
        if (!consulting_answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.consulting_answerService.modify(consulting_answer, consulting_answerForm.getContent());
        return String.format("redirect:/consulting/detail/%s#answer_%s",
                consulting_answer.getConsulting().getId(), consulting_answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String consulting_answerDelete(Principal principal, @PathVariable("id") Integer id) {
        Consulting_Answer consulting_answer = this.consulting_answerService.getConsulting_Answer(id);
        if (!consulting_answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.consulting_answerService.delete(consulting_answer);
        return String.format("redirect:/consulting/detail/%s", consulting_answer.getConsulting().getId());
    }

}