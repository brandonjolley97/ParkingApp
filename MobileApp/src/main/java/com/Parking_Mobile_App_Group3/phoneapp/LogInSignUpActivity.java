package com.Parking_Mobile_App_Group3.phoneapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.Parking_Mobile_App_Group3.api.HideKeyboard;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.Parking_Mobile_App_Group3.api.viewmodels.UserViewModel;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


public class LogInSignUpActivity extends ActivityWithUser {
    DatabaseReference database;
    private final String USER_TYPE_KEY = "userType";
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_sign_up);
        this.database = FirebaseDatabase.getInstance().getReference();

        //Get bundle from WelcomeActivity to determine what type of user is trying to log in.
        userType = getIntent().getExtras().getString(USER_TYPE_KEY);
        Log.d("USER TYPE", userType);

        //Initially sign out since Firebase likes to store authorization tokens for some reason
        userViewModel.signOut();

        TextView errorMsg = findViewById(R.id.logInSignUpErrorMsg);
        EditText username = findViewById(R.id.inputUsername);
        EditText password = findViewById(R.id.inputPassword);
        Button logIn = findViewById(R.id.buttonLogIn);
        Button signUp = findViewById(R.id.buttonSignUp);

        findViewById(R.id.backToWelcome).setOnClickListener(view ->{
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        logIn.setOnClickListener(view ->{
            try {
                userViewModel.signIn(
                        username.getText().toString(),
                        password.getText().toString()
                );
            }
            catch (IllegalArgumentException ex){
                ex.printStackTrace();
                //Send user error message
                genErrorMsg(username, password, errorMsg, false);
            }
        });

        signUp.setOnClickListener(view ->{
            try {
                userViewModel.signUp(
                        username.getText().toString(),
                        password.getText().toString(),
                        userType
                );
            }
            catch (IllegalArgumentException ex){
                ex.printStackTrace();
                genErrorMsg(username, password, errorMsg, true);
            }
        });
    }

    public void changeActivity(String type){
        if(type.equals("customer")){
            Intent intent = new Intent(this, CustomerMainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(type.equals("owner")){
            Intent intent = new Intent(this, OwnerMainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(type.equals("attendant")){
            Intent intent = new Intent(this, AttendantMainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Log.d("CRITICAL ERROR",
                    "User Type was invalid. UserType = " + type);
            System.exit(1001);
        }
    }

    private void genErrorMsg(EditText email, EditText pass, TextView msgTextView, boolean signUp){
        //either field blank
        if(email.getText().toString().equals("") || pass.getText().toString().equals("")){
            //No email or password
            if(email.getText().toString().equals("") || pass.getText().toString().equals("")){
                msgTextView.setText("Please enter email and password");
            }
            //No email
            if(email.getText().toString().equals("")){
                msgTextView.setText("Please enter email");
            }
            //No password
            else{
                msgTextView.setText("Please enter password");
            }
        }
        else{
            if(signUp){
                msgTextView.setText("Email in use or invalid email");
            }
            else{
                msgTextView.setText("Invalid password");
            }
        }

        //reset password field
        pass.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Observe changes in user
        userViewModel.getUser().observe(this, (user) ->{
            if(user != null){
                Log.d("USER ID", user.id);
                // Listen for data to be put in firebase, then retrieve user data and switch to
                // New activity based on the user type.
                database.child("accounts").child(user.id).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
//                            String uType = snapshot.child("userType").getValue().toString();
                            String uType = snapshot.getValue().toString();
                            Log.d("SNAPSHOT", uType);
                            changeActivity(uType);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        // Observe changes to error messages in UserViewModel
        userViewModel.getAuthError().observe(this, (authErrorMsg) ->{
            if(authErrorMsg != null) {
                Log.d("TESTING MSG", authErrorMsg);

                EditText pass = findViewById(R.id.inputPassword);
                TextView text = findViewById(R.id.logInSignUpErrorMsg);
                text.setText(authErrorMsg);
                pass.setText("");
            }

        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        HideKeyboard.run(this);
        return super.dispatchTouchEvent(ev);
    }
}