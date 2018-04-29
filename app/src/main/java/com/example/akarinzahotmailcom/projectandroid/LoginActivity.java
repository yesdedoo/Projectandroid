package com.example.akarinzahotmailcom.projectandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Set the initial variable or action of the activity.
//Also intent to other activity
public class LoginActivity extends AppCompatActivity {
    // Initialize state
    private EditText loginEmail;
    private EditText loginPassword;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private Button loginButton;
    private Button signUpButton;
    private Button forgotButton;


    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button)findViewById(R.id.loginBtn);
        loginEmail = (EditText)findViewById(R.id.login_email);
        loginPassword = (EditText)findViewById(R.id.login_password);
        signUpButton = (Button)findViewById(R.id.SignupBtn);
        forgotButton = (Button)findViewById(R.id.ResetBtn);

        // Get authentication instance from Firebase
        auth = FirebaseAuth.getInstance();

        //Get data Reference from database  table name "Users" in Firebase.
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        //Go to SingUp page
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        //Go to for got password Activity
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        //Login Activity
        //Check the user input
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "PROCESSING...", Toast.LENGTH_LONG).show(); //Make Popup "Processing..."
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                //Check if email & password label is not empty
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    //Send input to Firebase.
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //If User exist in the Firebase system
                            if(task.isSuccessful()){
                                checkUserExistence();
                            }
                            //If not exist
                            else {
                                Toast.makeText(LoginActivity.this, "Could't login, User not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                // If user didn't not complete input.
                else {
                    Toast.makeText(LoginActivity.this, "Complete all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//check user existence.
//Check user in the database while logging in
    public void checkUserExistence(){
        final String user_id = auth.getCurrentUser().getUid();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)){ //check user exist from "user_id"
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else {
                    Toast.makeText(LoginActivity.this,"User not registered!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
