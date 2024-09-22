package com.assignment.project.PaymentMs.service;

import com.assignment.project.PaymentMs.exception.PaymentException;
import com.assignment.project.PaymentMs.model.Bus;
import com.assignment.project.PaymentMs.model.Payment;
import com.assignment.project.PaymentMs.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, Bus> kafkaTemplateInventory;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplatePaymentFail;

    private static final String INVENTORY_TOPIC = "payment_inventory_topic";

    private static final String PAYMENT_FAILURE = "payment-failure-topic";

    private boolean paymentStatus = false;

    @KafkaListener(topics = "booking_payment_topic", groupId = "payment-consumer-group")
    @Transactional
    public void processPayment(Bus bus) {
        if(bus == null || bus.getBookingNumber().isEmpty()){
            throw new PaymentException("Bus details from Booking service obtained from kafka is invalid");
        }
        if (paymentRepository.existsByBookingNumber(bus.getBookingNumber())) {
            logger.info("Payment already processed for booking number: {}", bus.getBookingNumber());
            return;  // Idempotent operation: do nothing if payment already exists
        }
        try{
            Payment payment = new Payment();
            payment.setBookingNumber(bus.getBookingNumber());
            payment.setDateOfPayment(new Date());
            paymentRepository.save(payment);
            paymentStatus = true;
        } catch (Exception e) {
            throw new PaymentException("Exception occurred while updating payment"+e.getMessage());
        }finally {
            if(paymentStatus){
                logger.info("Payment is successfully completed. Please update inventory");
                kafkaTemplateInventory.send(INVENTORY_TOPIC, bus);//Send to Inventory service for further booking updates
            }else{
                logger.info("Failure in payment. Please update Booking service");
                kafkaTemplatePaymentFail.send(PAYMENT_FAILURE, bus.getBookingNumber());//Send to Booking service to cancel booking
            }
        }

    }

    public Payment getPayment(String paymentNumber) {
        return paymentRepository.findById(paymentNumber).get();
    }

    public void refundPayment(String bookingNumber) {
        Payment payment = paymentRepository.findByBookingNumber(bookingNumber);
        if (payment != null) {
            logger.info("Refund processed for booking: {}",bookingNumber);
            paymentRepository.delete(payment);
            kafkaTemplatePaymentFail.send(PAYMENT_FAILURE, bookingNumber);// Sending request to booking service to cancel the booking
        } else {
            logger.info("No payment record found for booking: " + bookingNumber);
            throw new PaymentException("No payment record found for booking: "+ bookingNumber);
        }
    }
}

