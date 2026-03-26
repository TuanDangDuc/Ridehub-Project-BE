package com.tuan.ridehub.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChangePasswordDtoRequest {
    private UUID id;
    private String newPassword;
}
