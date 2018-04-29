package com.example.akarinzahotmailcom.projectandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity{
    // initialize state.
    private Button registerBtn;
    private EditText Email;
    private EditText userName;
    private EditText Password;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private TextView loginTextview;

    /*Set the initial variable or action of the activity.
Also intent to other activity addition with
authentication of the user in database */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginTextview = (TextView)findViewById(R.id.loginTxtView);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        Email = (EditText)findViewById(R.id.emailField);
        userName = (EditText)findViewById(R.id.usernameField);
        Password = (EditText)findViewById(R.id.passwordField);

        //get authentication instance from Firebase.
        auth = FirebaseAuth.getInstance();
        //Get data Reference from database  table name "Users" in Firebase.
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        // Go to Login Page.
        loginTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        // Register Activity
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "Loading...", Toast.LENGTH_LONG).show();
                // Get input from EditText.
                final String username = userName.getText().toString().trim();
                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();
                //Check if iput is not Empty.
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                    // Create user in Firebase Authentication.
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            String user_id = auth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = database.child(user_id);
                            //Set username
                            current_user_db.child("Username").setValue(username);
                            //Set image as Default
                            current_user_db.child("Image").setValue("Default");
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            //Go to Profile Activity.
                            Intent registerIntent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(registerIntent);
                        }
                    });
                }
                else {// If user didn't complete the input fielf
                    Toast.makeText(RegisterActivity.this, "Complete all field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
