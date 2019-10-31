package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class AuthenticateConsumer extends Activity {

    final Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticate_consumer);
        //final Intent intent = getIntent();

        Button yesButton = findViewById(R.id.yes_button);
        Button noButton = findViewById(R.id.no_button);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send message to Firebase Server
                final String SENDER_ID = "923983506811"; //Sender ID from Firebase Console
                final int messageId = random.nextInt(); // Increment for each
                // [START fcm_send_upstream]
                FirebaseMessaging.getInstance().subscribeToTopic("test");
                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                        .setMessageId(Integer.toString(messageId))
                        .addData("my_message", "Hello World Nandini Here")
                        .addData("my_action","SAY_HELLO")
                        .build());
                System.out.println("YES Clicked");
                Intent i = new Intent(AuthenticateConsumer.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send message to Firebase Server
                final String SENDER_ID = "923983506811"; //Sender ID from Firebase Console
                final int messageId = random.nextInt(); // Increment for each
                // [START fcm_send_upstream]
                FirebaseMessaging.getInstance().subscribeToTopic("test");
                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                        .setMessageId(Integer.toString(messageId))
                        .addData("my_message", "Hello World Nandini Here")
                        .addData("my_action","SAY_HELLO")
                        .build());
                System.out.println("NO Clicked");
                Intent i = new Intent(AuthenticateConsumer.this, MainActivity.class);
                startActivity(i);
                finish();


            }
        });

    }

}
