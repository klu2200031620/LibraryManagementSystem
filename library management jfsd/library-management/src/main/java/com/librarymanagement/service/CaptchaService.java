package com.librarymanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {
    private static final String SECRET_KEY = "YOUR_SECRET_KEY";
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyCaptcha(String response) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = VERIFY_URL + "?secret=" + SECRET_KEY + "&response=" + response;
        CaptchaResponse captchaResponse = restTemplate.postForObject(requestUrl, null, CaptchaResponse.class);
        return captchaResponse != null && captchaResponse.isSuccess();
    }
}

class CaptchaResponse {
    private boolean success;
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
