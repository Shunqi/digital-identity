package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AuthenticateConsumer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticate_consumer);

        Button yesButton = findViewById(R.id.yes_button);
        Button noButton = findViewById(R.id.no_button);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send message to Firebase Server
                System.out.println("YES Clicked");
                Intent i = new Intent(AuthenticateConsumer.this, MainActivity.class);
                startActivity(i);

            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send message to Firebase Server
                System.out.println("NO Clicked");
                Intent i = new Intent(AuthenticateConsumer.this, MainActivity.class);
                startActivity(i);

            }
        });

    }
}
