package com.assignment.project.PaymentMs.service;

import com.assignment.project.PaymentMs.exception.PaymentException;
import com.assignment.project.PaymentMs.model.Bus;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentSagaListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSagaListener.class);

    @Autowired
    private PaymentService paymentService;

    @KafkaListener(topics = "inventory-failure-topic", groupId = "payment-consumer-group")
    public void handleInventoryFailure(Bus bus) {
        logger.info("Inventory failure occurred and need to refund payment now");
        if(bus != null && !bus.getBookingNumber().isEmpty()){
            paymentService.refundPayment(bus.getBookingNumber());
        }else{
            throw new PaymentException("Bus details from Inventory is null");
        }

    }
}

