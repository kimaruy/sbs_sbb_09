package com.sbs.sbb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sbs.sbb.center.CenterService;

@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private CenterService centerService;

    @Test
    void testJpa() {
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            this.centerService.create(subject, content, null);
        }
    }
}