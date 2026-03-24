package com.tuan.ridehub.dto.request;

import lombok.Data;

@Data
public class RegisterDtoRequest {
    String username;
    String password;
    String email;
}
