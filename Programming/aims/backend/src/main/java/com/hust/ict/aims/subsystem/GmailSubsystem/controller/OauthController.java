package com.hust.ict.aims.subsystem.GmailSubsystem.controller;

import com.hust.ict.aims.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hust.ict.aims.subsystem.GmailSubsystem.service.MailService;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;

@Controller
@RequiredArgsConstructor
public class OauthController {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Value("${google.token-uri}")
    private String tokenUri;

    @Value("${google.user-info-uri}")
    private String userInfoUri;

    private Logger logger = LoggerFactory.getLogger(OauthController.class);

    private final MailService emailService;
    @GetMapping("/gmail/login")
    public void login(HttpServletResponse response) throws IOException {
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "scope=" + URLEncoder.encode("https://www.googleapis.com/auth/userinfo.email", StandardCharsets.US_ASCII.toString()) +
                "&access_type=offline&include_granted_scopes=true&response_type=code&prompt=consent" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.US_ASCII.toString()) +
                "&client_id=" + clientId;
        logger.info(authUrl);
        response.sendRedirect(authUrl);
    }

    @GetMapping("/oauth2/callback")
    public String oauthCallback(@RequestParam("code") String code, Model model, HttpServletRequest request) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUri, params, Map.class);
        String accessToken = (String) tokenResponse.getBody().get("access_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, Map.class);
        HttpSession session = request.getSession();
        Transaction trans = (Transaction) session.getAttribute("transaction_info");
        System.out.println("Transaction info: " + trans.toString());
        String email = (String) userInfoResponse.getBody().get("email");
        String username = (String) userInfoResponse.getBody().get("username");
        emailService.sendEmail(email, "Thông tin đơn hàng VNPay", "Cảm ơn bạn đã thanh toán. Đơn hàng của bạn đang được xử lý.\nĐường dẫn");
        model.addAttribute("email", email);
        model.addAttribute("username", username);
        return "order_to_gmail";
    }

    /**
     * Send order confirmation email
     * @param toGmail recipient email address
     * @param orderInfo order details
     * @return response with status
     */
    @GetMapping("/send-gmail")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendOrderInfo(
            @RequestParam("toGmail") String toGmail,
            @RequestParam(value = "body", required = false, defaultValue = "") String orderInfo
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            // Validate email
            if (toGmail == null || toGmail.isEmpty() || !toGmail.contains("@")) {
                logger.warn("Invalid email address provided: {}", toGmail);
                response.put("status", "error");
                response.put("message", "Invalid email address");
                return ResponseEntity.badRequest().body(response);
            }

            // Use provided order info or default message
            String messageBody = orderInfo.isEmpty()
                    ? "Cảm ơn bạn đã thanh toán. Đơn hàng của bạn đang được xử lý."
                    : orderInfo;

            emailService.sendEmail(toGmail, "Thông tin đơn hàng AIMS", messageBody);

            logger.info("Order confirmation email sent to {}", toGmail);
            response.put("status", "success");
            response.put("message", "Email sent successfully");
            return ResponseEntity.ok(response);

        } catch (MessagingException e) {
            logger.error("Failed to send email: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Failed to send email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}