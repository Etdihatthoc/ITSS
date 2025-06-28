package com.hust.ict.aims.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SwaggerLogger {
    Logger logger;
    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerUrl() {
        logger = LoggerFactory.getLogger(SwaggerLogger.class);
        logger.info("Swagger is ready at: http://localhost:8080/swagger-ui.html");
    }
}
