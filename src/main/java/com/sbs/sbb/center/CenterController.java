package com.sbs.sbb.center;

import com.sbs.sbb.answer.AnswerForm;
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

@RequestMapping("/center")
@RequiredArgsConstructor
@Controller
public class CenterController {

    private final CenterService centerService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Center> paging = this.centerService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "center_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Center center = this.centerService.getCenter(id);
        model.addAttribute("center", center);
        return "center_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String centerCreate(CenterForm centerForm) {
        return "center_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String centerCreate(@Valid CenterForm centerForm,
                                 BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "center_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.centerService.create(centerForm.getSubject(), centerForm.getContent(), siteUser);
        return "redirect:/center/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String centerModify(CenterForm centerForm, @PathVariable("id") Integer id, Principal principal) {
        Center center = this.centerService.getCenter(id);
        if(!center.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        centerForm.setSubject(center.getSubject());
        centerForm.setContent(center.getContent());
        return "center_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String centerModify(@Valid CenterForm centerForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "center_form";
        }
        Center center = this.centerService.getCenter(id);
        if (!center.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.centerService.modify(center, centerForm.getSubject(), centerForm.getContent());
        return String.format("redirect:/center/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String centerDelete(Principal principal, @PathVariable("id") Integer id) {
        Center center = this.centerService.getCenter(id);
        if (!center.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.centerService.delete(center);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Center center = this.centerService.getCenter(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.centerService.vote(center, siteUser);
        return String.format("redirect:/center/detail/%s", id);
    }

}