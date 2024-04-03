package amaraj.searchjob.application.web.advice;

import amaraj.searchjob.application.exception.NiptErrorMessage;
import amaraj.searchjob.application.exception.NiptNonValidException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ClassicModelsGlobalExceptionHanlder {

    @ExceptionHandler(NiptNonValidException.class)
    public ResponseEntity<NiptErrorMessage> handleNiptNonValidException(NiptNonValidException ex, HttpServletRequest request){
        var resp = NiptErrorMessage.builder()
                .message(ex.getMessage()).statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI()).build();
        return ResponseEntity.badRequest().body(resp);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<NiptErrorMessage> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request){
        var resp = NiptErrorMessage.builder()
                .message(ex.getMessage()).statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI()).build();
        return ResponseEntity.badRequest().body(resp);
    }
}
