package com.hamidi.ecommerce.kafka;

import com.hamidi.ecommerce.email.EmailService;
import com.hamidi.ecommerce.kafka.order.OrderConfirmation;
import com.hamidi.ecommerce.kafka.payment.PaymentConfirmation;
import com.hamidi.ecommerce.notification.Notification;
import com.hamidi.ecommerce.notification.NotificationRepository;
import com.hamidi.ecommerce.notification.NotificationType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.hamidi.ecommerce.notification.NotificationType.PAYMENT_CONFIRMATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(PaymentConfirmation paymentConfirmation) throws MessagingException {
        log.info("Consuming the message from payment-topic Topic:: {}", paymentConfirmation);
        repository.save(
                Notification.builder()
                        .type(PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );

        // send email
        var customerName = paymentConfirmation.customerFirstname() + " " + paymentConfirmation.customerLastname();
        emailService.sendPaymentSuccessEmail(
                paymentConfirmation.customerEmail(),
                customerName,
                paymentConfirmation.amount(),
                paymentConfirmation.orderReference()
        );
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotification(OrderConfirmation orderConfirmation) throws MessagingException {
        log.info("Consuming the message from order-topic Topic:: {}", orderConfirmation);
        repository.save(
                Notification.builder()
                        .type(NotificationType.ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build());

        // send email
        var customerName = orderConfirmation.customer().firstname() + " " + orderConfirmation.customer().lastname();
        emailService.sendPaymentSuccessEmail(
                orderConfirmation.customer().email(),
                customerName,
                orderConfirmation.amount(),
                orderConfirmation.orderReference()
        );
    }
}
