package br.com.one.sentiment_analysis.handler;

import br.com.one.sentiment_analysis.exception.ResourceNotFoundException;
import br.com.one.sentiment_analysis.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //  Erro 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handlerNotFound(ResourceNotFoundException ex) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(),
                                    "Recurso não encontrado",
                                    Map.of("error", ex.getMessage()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    //  Erro 400

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handlerValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                fieldErrors
        );

        return ResponseEntity
                .badRequest()
                .body(apiError);
    }

    // Erro 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handlerGeneric(Exception ex) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                        "Erro interno no servidor",
                                    Map.of("error", "Erro inesperado no servidor"));
        log.error("Erro interno", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
