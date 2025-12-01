package br.com.truta.models;

import java.util.Map;

public record AuthResponse(
    String status,
    Map<String, Boolean> data 
) {
}