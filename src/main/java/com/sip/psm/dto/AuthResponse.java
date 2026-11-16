package com.sip.psm.dto;

import java.util.List;

public record AuthResponse(String token, String email, String nom, String prenom, List<String> roles) {
}
