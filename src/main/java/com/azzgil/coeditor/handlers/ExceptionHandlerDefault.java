package com.azzgil.coeditor.handlers;

import com.azzgil.coeditor.utils.logging.ColoredLogger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerDefault extends ResponseEntityExceptionHandler {

    private ColoredLogger logger = new ColoredLogger(ExceptionHandlerDefault.class.getCanonicalName());

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleExceptions(Exception ex, WebRequest request) {

        // show client nothing; log error to console.
        logger.error(ex.getMessage());
        ex.printStackTrace();
        String body = "We are sorry, some server error happened.";
        return handleExceptionInternal(ex, body, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
