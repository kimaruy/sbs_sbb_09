package com.sbs.sbb.user;

import com.sbs.sbb.DataNotFoundException;
import com.sbs.sbb.answer.Answer;
import com.sbs.sbb.answer.AnswerRepository;
import com.sbs.sbb.center.Center;
import com.sbs.sbb.center.CenterRepository;
import com.sbs.sbb.consulting.ConsultingService;
import com.sbs.sbb.consulting_answer.Consulting_AnswerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Consulting_AnswerService consulting_AnswerService;
    private  final ConsultingService consultingService;
    private final CenterRepository centerRepository;
    private final AnswerRepository answerRepository;


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
    @Transactional
    public void deleteUserAndRelatedData(String username) {
        // 사용자 조회
        SiteUser siteUser = userRepository.findByusername(username).orElse(null);

        if (siteUser != null) {
            // 사용자가 작성한 Center 가져오기
            List<Center> userCenters = centerRepository.findByAuthor(siteUser);
            List<Answer> userAnswers = answerRepository.findByAuthor(siteUser);

            // Center에 연결된 Answer 삭제
            for (Center center : userCenters) {
                List<Answer> centerAnswers = center.getAnswerList();
                answerRepository.deleteAll(centerAnswers); // Answer 데이터 삭제
            }

            // 사용자가 작성한 Center 삭제
            centerRepository.deleteAll(userCenters);
            answerRepository.deleteAll(userAnswers);

            // 사용자 삭제
            userRepository.delete(siteUser);
        }
    }
}
