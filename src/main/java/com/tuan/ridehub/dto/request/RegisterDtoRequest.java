package com.tuan.ridehub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RegisterDtoRequest {
    String username;
    String password;
    String email;
}
