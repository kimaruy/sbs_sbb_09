package com.sbs.sbb.consulting;

import com.sbs.sbb.center.Center;
import com.sbs.sbb.consulting_answer.Consulting_AnswerForm;
import com.sbs.sbb.user.SiteUser;
import com.sbs.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/consulting")
@RequiredArgsConstructor
@Controller
public class ConsultingController {

    private final ConsultingService consultingService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Consulting> paging = this.consultingService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "consulting_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Consulting consulting = this.consultingService.getConsulting(id);

        // Consulting_AnswerForm 폼 객체를 생성하고 모델에 추가
        Consulting_AnswerForm consulting_answerForm = new Consulting_AnswerForm();
        model.addAttribute("consulting_answerForm", consulting_answerForm);

        model.addAttribute("consulting", consulting);
        return "consulting_detail";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String consultingCreate(ConsultingForm consultingForm) {
        return "consulting_form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String consultingCreate(@Valid ConsultingForm consultingForm,
                                   BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "consulting_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.consultingService.create(consultingForm.getSubject(), consultingForm.getContent(), siteUser);
        return "redirect:/consulting/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String consultingModify(ConsultingForm consultingForm, @PathVariable("id") Integer id, Principal principal) {
        Consulting consulting = this.consultingService.getConsulting(id);
        if(!consulting.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        consultingForm.setSubject(consulting.getSubject());
        consultingForm.setContent(consulting.getContent());
        return "consulting_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String consultingModify(@Valid ConsultingForm consultingForm, BindingResult bindingResult,
                               Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "consulting_form";
        }
        Consulting consulting = this.consultingService.getConsulting(id);
        if (!consulting.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.consultingService.modify(consulting, consultingForm.getSubject(), consultingForm.getContent());
        return String.format("redirect:/consulting/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String consultingDelete(Principal principal, @PathVariable("id") Integer id) {
        Consulting consulting = this.consultingService.getConsulting(id);
        if (!consulting.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.consultingService.delete(consulting);
        return "redirect:/";
    }

}