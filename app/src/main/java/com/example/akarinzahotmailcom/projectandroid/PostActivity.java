package com.example.akarinzahotmailcom.projectandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {
    //initialize state
    private ImageButton imageButton;
    private static final int GALLERRY_REQUEST_CODE = 2;
    private Uri uri = null;
    private EditText textTitle;
    private EditText description;
    private Button postButton;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private DatabaseReference databaseUsers;
    private FirebaseUser currentUser;

    /*Set the initial variable or action of the activity.
Also intent to other activity addition with
authentication of the user in database */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postButton = (Button)findViewById(R.id.postBtn);
        description = (EditText)findViewById(R.id.textDesc);
        textTitle = (EditText)findViewById(R.id.textTitle);
        storageReference = FirebaseStorage.getInstance().getReference();

        //get reference of data from database name "Reviewer"
        databaseReference = database.getInstance().getReference().child("Reviewer");

        //get authentication instance from firebase.
        auth = FirebaseAuth.getInstance();

        //get data of current user.
        currentUser = auth.getCurrentUser();

        //Get User id from data Reference in database  table name "Users" in Firebase.
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        imageButton = (ImageButton)findViewById(R.id.imageBtn);

        //Intent to the gallery to access to the photo
        //upload image from gallery.
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERRY_REQUEST_CODE);
            }
        });

        //Post Activity
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PostActivity.this, "POSTING...", Toast.LENGTH_LONG).show();
                final String PostTiTle = textTitle.getText().toString().trim();
                final String PostDescription = description.getText().toString().trim();

                //Check if title and Description is not Empty.
                if(!TextUtils.isEmpty(PostDescription) && !TextUtils.isEmpty(PostTiTle)){

                    //save image in Firebase Storage folder name "post_image"
                    StorageReference filepath = storageReference.child("post_image").child(uri.getLastPathSegment());
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        //Upload the picture to Firebase storage.
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(getApplicationContext(), "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                            final DatabaseReference newPost = databaseReference.push();

                            // add data to the database of current user.
                            databaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newPost.child("title").setValue(PostTiTle);
                                    newPost.child("decription").setValue(PostDescription);
                                    newPost.child("imageUrl").setValue(downloadUrl.toString());
                                    newPost.child("uid").setValue(currentUser.getUid());
                                    newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        //When post is complete then go to Main page.
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }
        });
    }
    //Image from galley result
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == GALLERRY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            imageButton.setImageURI(uri);
        }
    }
}
