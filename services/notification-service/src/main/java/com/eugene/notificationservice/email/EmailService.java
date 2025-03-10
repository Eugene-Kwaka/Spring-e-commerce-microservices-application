package com.eugene.notificationservice.email;

import com.eugene.notificationservice.kafka.order.ProductDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Comes from the thymeleaf dependency
     */
    private final SpringTemplateEngine templateEngine;

    /**
     * I want to make the methods async because when we receive a notification, I do not want to block the whole process until the email is sent.
     * */
    @Async
    public void sendPaymentSuccessEmail(String toEmail, String customerName, BigDecimal amount, String orderReference) throws MessagingException {

        // I need to create object of type MimeMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                "UTF-8");

        messageHelper.setFrom("kwakaeugene@gmail.com");

        final String templateName = EmailTemplate.PAYMENT_CONFIRMATION.getTemplate();

        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("amount", amount);
        variables.put("orderReference", orderReference);

        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(EmailTemplate.PAYMENT_CONFIRMATION.getSubject());

        try{
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);
            messageHelper.setTo(toEmail);

            mailSender.send(mimeMessage);

            log.info("Email sent successfully to {} with template {}", toEmail, templateName);


        } catch (MessagingException e) {
            log.warn("Cannot send mail to {}", toEmail);
            throw new RuntimeException(e);
        }

    }

    @Async
    public void sendOrderSuccessEmail(String toEmail, String customerName, BigDecimal amount, String orderReference, List<ProductDTO> productsDTO) throws MessagingException {

        // I need to create object of type MimeMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                "UTF-8");

        messageHelper.setFrom("kwakaeugene@gmail.com");

        final String templateName = EmailTemplate.ORDER_CONFIRMATION.getTemplate();

        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("totalAmount", amount);
        variables.put("orderReference", orderReference);
        variables.put("Products", productsDTO);

        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(EmailTemplate.ORDER_CONFIRMATION.getSubject());

        try{
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);
            messageHelper.setTo(toEmail);

            mailSender.send(mimeMessage);

            log.info("Email sent successfully to {} with template {}", toEmail, templateName);


        } catch (MessagingException e) {
            log.warn("Cannot send mail to {}", toEmail);
            throw new RuntimeException(e);
        }




    }
}
