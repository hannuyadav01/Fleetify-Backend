package com.fleetify.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void sendPushNotification(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("Cannot send push notification: FCM token is null or empty");
            return;
        }

        log.info("Sending push notification to FCM token [{}]: Title: \"{}\", Body: \"{}\"", 
                fcmToken, title, body);
        
        // In a real-world implementation, this would invoke the Firebase Admin SDK:
        // Message message = Message.builder()
        //         .putData("title", title)
        //         .putData("body", body)
        //         .setToken(fcmToken)
        //         .build();
        // FirebaseMessaging.getInstance().send(message);
    }
}
