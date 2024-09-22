package com.assignment.project.PaymentMs.controller;

import com.assignment.project.PaymentMs.model.Payment;
import com.assignment.project.PaymentMs.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paymentsApi")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/{paymentNumber}")
    public ResponseEntity<Payment> getPayment(@PathVariable String paymentNumber) {
        Payment payment = paymentService.getPayment(paymentNumber);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }
}
