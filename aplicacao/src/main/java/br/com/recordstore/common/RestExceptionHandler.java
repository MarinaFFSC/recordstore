package br.com.recordstore.common;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.context.request.WebRequest;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Map<String,Object>> handleBusiness(BusinessException ex, WebRequest req){
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
      .body(Map.of("timestamp", Instant.now().toString(), "error", ex.getMessage()));
  }
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,Object>> handleIllegal(IllegalArgumentException ex, WebRequest req){
    return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now().toString(), "error", ex.getMessage()));
  }
}
