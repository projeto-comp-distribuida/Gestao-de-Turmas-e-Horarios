package com.distrischool.schedule.exception;

/**
 * Exceção para recursos não encontrados
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s não encontrado(a) com ID: %d", resource, id));
    }
}
