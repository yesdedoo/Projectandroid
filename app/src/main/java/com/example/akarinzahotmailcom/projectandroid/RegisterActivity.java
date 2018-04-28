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
    private Button registerBtn;
    private EditText Email;
    private EditText userName;
    private EditText Password;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private TextView loginTextview;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginTextview = (TextView)findViewById(R.id.loginTxtView);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        Email = (EditText)findViewById(R.id.emailField);
        userName = (EditText)findViewById(R.id.usernameField);
        Password = (EditText)findViewById(R.id.passwordField);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        loginTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                /** Fading Transition Effect */
                RegisterActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "Loading...", Toast.LENGTH_LONG).show();
                final String username = userName.getText().toString().trim();
                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String user_id = auth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = database.child(user_id);
                            current_user_db.child("Username").setValue(username);
                            current_user_db.child("Image").setValue("Default");
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent registerIntent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(registerIntent);

                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Complete all field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
