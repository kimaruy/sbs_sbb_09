package com.sbs.sbb.user;

import com.sbs.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    public SiteUser create(String username, String email, String password, String name, String cellphoneNo,
                           String nickname, String birth) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setBirth(birth);
        user.setCellphoneNo(cellphoneNo);
        user.setName(name);
        user.setEmail(email);
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteUser not found");
        }
    }

    public void modify(SiteUser siteUser, String name, String email, String cellphoneNo) {
        siteUser.setName(name);
        siteUser.setEmail(email);
        siteUser.setCellphoneNo(cellphoneNo);
        this.userRepository.save(siteUser);
    }

    public void delete(SiteUser siteUser) {
        this.userRepository.delete(siteUser);
    }


    public boolean isCorrectPassword(String username, String password) {
        SiteUser user = getUser(username);
        String actualPassword = user.getPassword();
        return passwordEncoder.matches(password, actualPassword);
    }

    public void updatePassword(String username, String newPassword) {
        SiteUser user = getUser(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


}
