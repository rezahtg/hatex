package com.reza.hatex.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAttempts {

    private int attempts;
    private long timestamp;

    public void increment() {
        this.attempts++;
    }

    public void resetAttempts() {
        this.attempts = 0;
        this.timestamp = System.currentTimeMillis();
    }

}
