package com.reza.hatex.global;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

}
