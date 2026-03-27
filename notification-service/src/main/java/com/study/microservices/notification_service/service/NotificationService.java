package com.study.microservices.notification_service.service;

import com.study.microservices.order_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "order_placed")
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("Received Order Placed Event {}", orderPlacedEvent);

        // Send email to customer
        MimeMessagePreparator messagePreparator = mimeMessage -> {

          MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
          mimeMessageHelper.setFrom("testFrom@test.com");
          mimeMessageHelper.setTo(orderPlacedEvent.getEmail().toString());
          mimeMessageHelper.setSubject(String.format("Order Number %s Placed Successfully", orderPlacedEvent.getOrderNumber()));
          mimeMessageHelper.setText(String.format("""
                  Hi %s %s,
                  
                  Your order %s has been placed successfully.
                  
                  Best regards.
                  Loc Duong
                  """,
                  orderPlacedEvent.getFirstName()==null?"null":orderPlacedEvent.getFirstName().toString(),
                  orderPlacedEvent.getLastName()==null?"null":orderPlacedEvent.getLastName().toString(),
                  orderPlacedEvent.getOrderNumber()), true);

        };

        try {
            mailSender.send(messagePreparator);
            log.info("Sent Order Placed Event {}", orderPlacedEvent);
        } catch (MailException e) {
            log.error("Error while sending Order Placed Event {}", orderPlacedEvent, e);
            throw new RuntimeException("Exception while sending Order Placed Event " + orderPlacedEvent, e);
        }
    }
}
