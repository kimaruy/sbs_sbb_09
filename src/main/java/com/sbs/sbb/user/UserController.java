package com.sbs.sbb.user;

import com.sbs.sbb.question.Question;
import com.sbs.sbb.question.QuestionForm;
import com.sbs.sbb.user.UserCreateForm;
import com.sbs.sbb.user.UserService;
import com.sbs.sbb.user.SiteUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Optional;
import com.sbs.sbb.DataNotFoundException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private com.sbs.sbb.user.UserService UserService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String mypage(Principal principal, Model model) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);

        return "mypage_form";
    }


//    @PreAuthorize("isAuthenticated()")
//    @PostMapping("/modify/{id}")
//    public String mypageModify(@Valid UserCreateForm userCreateForm, BindingResult bindingResult,
//                                 Principal principal, @PathVariable("id") Integer id) {
//        if (bindingResult.hasErrors()) {
//            return "mypage_form";
//        }
//        SiteUser siteUser = this.UserService.getUser(id);
//        if (!SiteUser.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
//        }
//        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
//        return String.format("redirect:/question/detail/%s", id);
//    }
//
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/delete/{id}")
//    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
//        Question question = this.questionService.getQuestion(id);
//        if (!question.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
//        }
//        this.questionService.delete(question);
//        return "redirect:/";
//    }


    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1(),userCreateForm.getNickname(),
                    userCreateForm.getCellphoneNo(),userCreateForm.getName(),userCreateForm.getBirth());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

}