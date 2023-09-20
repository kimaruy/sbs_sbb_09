package com.sbs.sbb.user;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private com.sbs.sbb.user.UserService UserService;
    private PasswordEncoder passwordEncoder;
    private String correctPassword = "기존비밀번호"; // 실제 기존 비밀번호


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String mypage(Principal principal, Model model) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);

        return "mypage_form";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("mypage_modify")
    public String updateUserInfo(Principal principal, Model model) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);
        return "mypage_modify"; // 변경된 부분: 리다이렉트가 아니라 뷰 이름을 반환합니다.
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("mypage_modify") // @PathVariable 추가
    public String updateUserInfo(@Valid UserProfile userProfile, BindingResult bindingResult,
                                 Principal principal) {

        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.userService.modify(siteUser, userProfile.getName(), userProfile.getCellphoneNo(),
                userProfile.getEmail());
        return "redirect:/user/mypage";
    }

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
                    userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getNickname(),
                    userCreateForm.getCellphoneNo(), userCreateForm.getName(), userCreateForm.getBirth());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("mypage_withdrawal")
    public String userDelete(Principal principal, Model model) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);
        return "mypage_withdrawal"; // 변경된 부분: 리다이렉트가 아니라 뷰 이름을 반환합니다.
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/mypage_withdrawal")
    public String userDelete(HttpServletRequest request, @RequestParam("password") String password,
                             HttpServletResponse response, Principal principal, RedirectAttributes attributes) {
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if (userService.isCorrectPassword(siteUser.getUsername(), password)) {
            this.userService.delete(siteUser);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            return "redirect:/user/main";
        } else {
            // 비밀번호가 일치하지 않을 때 오류 메시지를 전달하고 회원 탈퇴 페이지로 리다이렉트합니다.
            attributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다. 다시 시도해주세요.");
            return "redirect:/user/mypage_withdrawal";
        }
    }



    @PreAuthorize("isAuthenticated()")
    @GetMapping("/password_modify")
    public String showChangePasswordForm(Principal principal, Model model) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        model.addAttribute("siteUser", siteUser);
        return "password_modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/password_modify")
    public String showChangePasswordForm(@RequestParam("Password") String Password,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model, Principal principal) {
        String username = principal.getName();

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "새로운 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            return "password_modify";
        }

        if (!userService.isCorrectPassword(username, Password)) {
            model.addAttribute("error", "기존 비밀번호가 올바르지 않습니다.");
            return "password_modify";
        }

        userService.updatePassword(username, newPassword);

        return "redirect:/user/mypage"; // 비밀번호 변경 성공 시 마이페이지로 리다이렉트
    }

    @GetMapping("/center_main")
    public String center_main() {

        return "center_main";
    }

    @GetMapping("/main")
    public String main() {

        return "main";
    }
}



