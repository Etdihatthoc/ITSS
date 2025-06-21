package com.hust.ict.aims.subsystem.VNPaySubsystem;


import com.hust.ict.aims.exception.PaymentException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
@Getter
@Setter
@Component
//@ConfigurationProperties(prefix = "vnpay")
public class Config {

    private String tmnCode;
    private String hashSecret;
    private String version;
    private String paymentUrl;
    private String currCode;
    private String locale;
    private String orderType;
    private String paymentResult;
    private String refundUrl;
    private String transactionType;
    private String createBy;

    protected final Map<String, String> errorCodeMap = new HashMap<>();
    public Config() {
        errorCodeMap.put("00", "Giao dịch thành công.");
        errorCodeMap.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).");
        errorCodeMap.put("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
        errorCodeMap.put("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần.");
        errorCodeMap.put("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        errorCodeMap.put("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
        errorCodeMap.put("13", "Giao dịch không thành công do: Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.");
        errorCodeMap.put("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch.");
        errorCodeMap.put("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        errorCodeMap.put("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        errorCodeMap.put("75", "Ngân hàng thanh toán đang bảo trì.");
        errorCodeMap.put("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch.");
        errorCodeMap.put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê).");
    }

    public String getErrorMessage(String responseCode) {
        return errorCodeMap.getOrDefault(responseCode, "Lỗi giao dịch không xác định (" + responseCode + ")");
    }

    public void handleErrorCode(String responseCode, String message) throws PaymentException {
        if (!"00".equals(responseCode)) {
            throw new PaymentException(message);
        }
    }

    // HMAC SHA512 hash
    public String hmacSHA512(String secret, String data) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            byte[] hash = sha512_HMAC.doFinal(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

//    public static void main(String[] arg) {
//        Config config = new Config();
//
//        System.out.println(config.getErrorMessage("00"));
//
//        System.out.println(config.getErrorMessage("75"));
//    }

//    protected static String getRandomNumber(int len) {
//        if (len <= 0) {
//            throw new IllegalArgumentException("Length must be positive.");
//        }
//        Random rnd = new Random();
//        String chars = "0123456789";
//        StringBuilder sb = new StringBuilder(len);
//        for (int i = 0; i < len; i++) {
//            sb.append(chars.charAt(rnd.nextInt(chars.length())));
//        }
//        return sb.toString();
//    }
//
//    public static String getClientIpAddr(HttpServletRequest request) {
//        String[] headersToCheck = {
//                "X-Forwarded-For",
//                "Proxy-Client-IP",
//                "WL-Proxy-Client-IP",
//                "HTTP_X_FORWARDED_FOR",
//                "HTTP_X_FORWARDED",
//                "HTTP_X_CLUSTER_CLIENT_IP",
//                "HTTP_CLIENT_IP",
//                "HTTP_FORWARDED_FOR",
//                "HTTP_FORWARDED",
//                "HTTP_VIA",
//                "REMOTE_ADDR"
//        };
//
//        for (String header : headersToCheck) {
//            String ip = request.getHeader(header);
//            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
//                return ip.split(",")[0].trim();
//            }
//        }
//
//        return request.getRemoteAddr(); // fallback
//    }

//    @PostConstruct
//    public void testInjection() {
//        System.out.println("== VNPay Configuration Loaded ==");
//        System.out.println("tmnCode: " + tmnCode);
//        System.out.println("hashSecret: " + hashSecret);
//        System.out.println("version: " + version);
//        System.out.println("paymentUrl: " + paymentUrl);
//        System.out.println("currCode: " + currCode);
//        System.out.println("locale: " + locale);
//        System.out.println("orderType: " + orderType);
//        System.out.println("paymentResult: " + paymentResult);
//        System.out.println("refundUrl: " + refundUrl);
//        System.out.println("transactionType: " + transactionType);
//        System.out.println("createBy: " + createBy);
//    }
}
