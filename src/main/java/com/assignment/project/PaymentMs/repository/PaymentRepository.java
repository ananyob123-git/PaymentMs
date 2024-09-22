package com.assignment.project.PaymentMs.repository;

import com.assignment.project.PaymentMs.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    public boolean existsByBookingNumber(String bookingNumber);

    public Payment findByBookingNumber(String bookingNumber);
}
