package GymApp.dto;

import jakarta.validation.constraints.Email;

// this dto represents the request body sent to delete coach, or client accounts using email / phone_number
public record DeleteAccountDto(@Email String email, String phoneNumber) {
}
