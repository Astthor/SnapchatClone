package com.example.snapchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set mAuth:
        mAuth = FirebaseAuth.getInstance();

        // Set the email and password:
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // If we've already signed in to the app, go to the next activity
        if(mAuth.getCurrentUser() != null){
            login();
        }
    }

    // Move to the next activity / SnapsActivity registered as teh user just signed up or in
    private void login(){
        Intent intent = new Intent(MainActivity.this, SnapsActivity.class);
        startActivity(intent);
    }

    // Sign in or Sign the user up if the user doesn't exist in the database
    public void loginBtnClicked(View view){
        // Check fields to see if they're empty
        if(emailEditText.getText().toString().length() > 0 && passwordEditText.getText().toString().length() > 0){
            // Check if we can log in the user / Registered?
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            login();
                        } else {
                            signUp();
                        }
                    });
        } else {
            Toast.makeText(MainActivity.this, "Please fill in the fields",
                    Toast.LENGTH_SHORT).show();
        }
        // Sign up the user
    }
    private void signUp(){
        System.out.println("Starting the signup");
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(MainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Map<String , String> map = new HashMap<>();
                        map.put("email", emailEditText.getText().toString());
                        db.collection("users").document(mAuth.getCurrentUser().getUid()).set(map);
                        login();
                    } else {
                        // Check if password is longer than 6 characters - Display appropriate Toast
                        if(passwordEditText.getText().toString().length() < 6){
                            Toast.makeText(MainActivity.this, "SignUp Failed - Password to short.\n6 char minimum",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            /*
                            Causes that I've found so far that do an error here are if there is a matched email but password is wrong
                            And if you make an invalid email.
                            TODO:
                            1. Create method to check if user is registered and tell them if it's a wrong password
                            2. Check if it's a valid email address.
                            3. Create appropriate Toasts or a custom error msg appearing below button.
                            */
                            Toast.makeText(MainActivity.this, "SignUp Failed - Unknown cause\nIs your email correct?",
                                    Toast.LENGTH_SHORT).show();
                        }
                        System.out.println("Email: " + emailEditText.getText().toString() + " password: " + passwordEditText.getText().toString());
                    }
                });
    }
}