package com.tuan.ridehub.repository;

import com.tuan.ridehub.enums.PaymentStatus;
import com.tuan.ridehub.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByUser_Id(UUID userId);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
}
