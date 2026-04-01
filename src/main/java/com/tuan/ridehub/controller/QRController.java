package com.tuan.ridehub.controller;

import com.tuan.ridehub.service.QRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qr/vehicle")
public class QRController {
    private final QRService qrService;

    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable UUID id
    ) {
        String url = "https://api.anhchuno.id.vn/api/vehicle/" + id;
        byte[] qr = qrService.generateQRCode(url);
        return ResponseEntity.ok(qr);
    }

}
