package com.sip.psm.dto;

public record RegisterRequest(String email, String password, String tel, String nom, String prenom) {
}