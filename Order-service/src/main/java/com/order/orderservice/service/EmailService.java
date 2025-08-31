package com.order.orderservice.service;

import com.order.orderservice.entity.OrderItem;
import com.order.orderservice.entity.Orders;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;



    public void sendOrderEmail(Orders details) {
        try {
            SimpleMailMessage message = createOrderMessage(details, true);
            mailSender.send(message);
            logger.info("Order confirmation email sent to {}", details.getEmailId());
        } catch (Exception e) {
            logger.error("Failed to send Order confirmation email to {}: {}", details.getEmailId(), e.getMessage());
        }
    }

    public void sendCancelEmail(Orders details) {
        try {
            SimpleMailMessage message = createOrderMessage(details, false);
            mailSender.send(message);
            logger.info("Cancellation email sent to {}", details.getEmailId());
        } catch (Exception e) {
            logger.error("Failed to send cancellation email to {}: {}", details.getEmailId(), e.getMessage());
        }
    }


        public SimpleMailMessage createOrderMessage(Orders details, boolean isConfirmed) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(details.getEmailId());
            message.setSubject(isConfirmed ? "Order Confirmation - " + details.getOrderId() : "Order Details - " + details.getOrderId());

            StringBuilder messageText = new StringBuilder();
            messageText.append("Dear Customer,\n\n");
            messageText.append(isConfirmed ? "Thank you for your order! Here are the details:\n\n" : "Here your canceled order details:\n\n");

            messageText.append("Order ID: ").append(details.getOrderId()).append("\n");
            messageText.append("Order Date: ").append(details.getOrderDate()).append("\n");
            messageText.append("Payment Mode: ").append(details.getModeOfPayment()).append("\n");
            messageText.append("Order Status: ").append(details.getOrderStatus()).append("\n\n");

            messageText.append("Items:\n");
            for (OrderItem item : details.getItems()) {
                messageText.append(item.getProductName()).append(" - Qty: ").append(item.getQuantity())
                        .append(" @ ").append(item.getPrice()).append(" each\n");
            }

            messageText.append("\nTotal Amount Paid: ").append(details.getAmountPaid()).append("\n\n");

            if (details.getAddress() != null) {
                messageText.append("Shipping Address:\n");
                messageText.append(details.getAddress().getFullAddress()).append("\n\n");
            }

            messageText.append("Thank you for shopping with us!\nBest Regards,\nYour Store Team");

            message.setText(messageText.toString());
            return message;
        }


}

