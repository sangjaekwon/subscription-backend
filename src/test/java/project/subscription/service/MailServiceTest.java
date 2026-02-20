package project.subscription.service;


import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MailServiceTest {

    @Autowired
    MailService mailService;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @Test
    public void 메일_전송_정상() throws Exception {
        //given
        mailService.sendMail("test@local.com", "mail test1", "test");
        mailService.sendMail("test@local.com", "mail test2", "test");
        mailService.sendMail("test@local.com", "mail test3", "test");

        //when
        MimeMessage[] messages = greenMail.getReceivedMessages();

        //then
        assertThat(messages.length).isEqualTo(3);
    }
}