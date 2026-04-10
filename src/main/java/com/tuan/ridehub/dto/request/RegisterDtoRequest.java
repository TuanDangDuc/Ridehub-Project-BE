package com.tuan.ridehub.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDtoRequest {
    String username;
    String password;
    String email;
}
