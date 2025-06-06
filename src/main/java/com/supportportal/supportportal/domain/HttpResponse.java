package com.supportportal.supportportal.domain;

import org.springframework.http.HttpStatus;

public class HttpResponse {

    private int httpStatusCode; // 200,201, 400, 500
    private HttpStatus httpStatus;
    private String reason;
    private String message;

    /*  The response is going to look like this:
     *  {
     *         code: 200,
     *         httpStatus: "OK",
     *         reason: "ok",
     *         message: "Your request was successful"
     *     }
     */

    public HttpResponse(){}

    public HttpResponse(String message, String reason, HttpStatus httpStatus, int httpStatusCode) {
        this.message = message;
        this.reason = reason;
        this.httpStatus = httpStatus;
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
