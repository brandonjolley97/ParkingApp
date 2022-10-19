package com.Parking_Mobile_App_Group3.phoneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {
    private final String USER_TYPE_KEY = "userType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView welcome = findViewById(R.id.welcome);
        Button customerButton = findViewById(R.id.customerButton);
        Button ownerButton = findViewById(R.id.ownerButton);
        Button attendantButton = findViewById(R.id.attendantButton);

        customerButton.setOnClickListener((view) -> {
            Intent intent = new Intent(this, LogInSignUpActivity.class);
            Bundle bundle = new Bundle();
            //send bundle specifying type of user to login activity
            bundle.putString(USER_TYPE_KEY, "customer");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });

        attendantButton.setOnClickListener((view) -> {
            Intent intent = new Intent(this, LogInSignUpActivity.class);
            Bundle bundle = new Bundle();
            //send bundle specifying type of user to login activity
            bundle.putString(USER_TYPE_KEY, "attendant");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });

        ownerButton.setOnClickListener((view) -> {
            Intent intent = new Intent(this, LogInSignUpActivity.class);
            Bundle bundle = new Bundle();
            //send bundle specifying type of user to login activity
            bundle.putString(USER_TYPE_KEY, "owner");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });


    }
}