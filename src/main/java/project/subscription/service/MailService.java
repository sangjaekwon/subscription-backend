package project.subscription.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import project.subscription.aop.annotation.Retry;
import project.subscription.exception.ex.MailSendException;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Retry(5)
    public void sendMail(String to, String html, String subject) {
        MimeMessage message = getMimeMessage(to, html, subject);

        mailSender.send(message);
    }

    @Retry(5)
    public void sendVerifyMail(String to, String html) {
        MimeMessage message = getMimeMessage(to, html, "구독관리서비스.site 이메일 인증 안내");

        mailSender.send(message);

    }

    private MimeMessage getMimeMessage(String to, String html, String subject) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML
        } catch (Exception e) {
            throw new MailSendException();
        }
        return message;
    }
}
