package com.tuan.ridehub.service;

import com.tuan.ridehub.dto.request.PaymentDtoRequest;
import com.tuan.ridehub.dto.response.PaymentDtoResponse;
import com.tuan.ridehub.enums.PaymentStatus;
import com.tuan.ridehub.enums.TripStatus;
import com.tuan.ridehub.model.Payment;
import com.tuan.ridehub.model.Trip;
import com.tuan.ridehub.model.Users;
import com.tuan.ridehub.repository.PaymentRepository;
import com.tuan.ridehub.repository.TripRepository;
import com.tuan.ridehub.repository.UserRepository;
import com.tuan.ridehub.service.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    public PaymentDtoResponse createPayment(PaymentDtoRequest request) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (trip.getTripStatus() != TripStatus.COMPLETED) {
            throw new RuntimeException("Trip must be completed before creating payment");
        }

        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double amount = trip.getTotalCost() != null ? trip.getTotalCost() : 0.0;

        // Tạo Payment
        Payment payment = paymentMapper.toPayment(request.getPaymentMethod(), amount, user);
        Payment savedPayment = paymentRepository.save(payment);

        // Gán payment cho trip (Trip có @ManyToOne Payment)
        trip.setPayment(savedPayment);
        tripRepository.save(trip);

        return paymentMapper.toPaymentResponse(savedPayment);
    }

    public PaymentDtoResponse processPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Only pending payments can be processed");
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    public PaymentDtoResponse failPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Only pending payments can be marked as failed");
        }

        payment.setPaymentStatus(PaymentStatus.FAILED);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    public PaymentDtoResponse getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return paymentMapper.toPaymentResponse(payment);
    }

    public List<PaymentDtoResponse> getPaymentsByUserId(UUID userId) {
        return paymentRepository.findByUser_Id(userId).stream()
                .map(paymentMapper::toPaymentResponse)
                .toList();
    }

    public List<PaymentDtoResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toPaymentResponse)
                .toList();
    }

    public void deletePayment(UUID id) {
        paymentRepository.deleteById(id);
    }
}
