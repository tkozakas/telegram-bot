package org.churk.telegrambot.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public final ResponseEntity<ErrorResponse> handleFeignException(FeignException ex, WebRequest request) {
        ErrorResponse errorResponse = getErrorInfo(ex, request);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        errorResponse.setDetails(request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse getErrorInfo(FeignException ex, WebRequest request) {
        ErrorResponse errorResponse;
        switch (ex) {
            case FeignException.BadGateway badGateway ->
                    errorResponse = new ErrorResponse(badGateway.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            case FeignException.InternalServerError internalServerError ->
                    errorResponse = new ErrorResponse(internalServerError.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            case FeignException.GatewayTimeout gatewayTimeout ->
                    errorResponse = new ErrorResponse(gatewayTimeout.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
            case FeignException.ServiceUnavailable serviceUnavailable ->
                    errorResponse = new ErrorResponse(serviceUnavailable.getMessage(), HttpStatus.NOT_FOUND);
            case FeignException.NotFound notFound ->
                    errorResponse = new ErrorResponse(notFound.getMessage(), HttpStatus.NOT_FOUND);
            case FeignException.UnprocessableEntity unprocessableEntity ->
                    errorResponse = new ErrorResponse(unprocessableEntity.getMessage(), HttpStatus.NOT_FOUND);
            case null, default ->
                    errorResponse = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        errorResponse.setDetails(request.getDescription(false));
        return errorResponse;
    }
}
