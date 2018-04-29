package com.example.akarinzahotmailcom.projectandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileActivity extends AppCompatActivity{
    //initial state.
    private TextView user;
    private ImageButton imageButton;
    private final static int GALLERY_REQ =1;
    private Button doneButton;
    private Button homeButton;
    private Uri ImageUri = null;
    private FirebaseAuth auth;
    private DatabaseReference databaseUsers;
    private StorageReference storageReference;


    /*Set the initial variable or action of the activity.
Also intent to other activity addition with
authentication of the user in database */
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_profile);
        //Get instance from Firebase Authentication
        auth = FirebaseAuth.getInstance();
        //Get data Reference from database  table name "Users" in Firebase.
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        //Get data reference from storage floder name "profile_image"
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_image");

        user = (TextView)findViewById(R.id.Username);
        imageButton = (ImageButton)findViewById(R.id.imagebutton);
        doneButton = (Button)findViewById(R.id.doneBtn);
        homeButton = (Button)findViewById(R.id.homeBtn);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = (String)dataSnapshot.child(auth.getCurrentUser().getUid()).child("Username").getValue();
                user.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Upload Profile picture
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQ);
            }
        });
        // change data done
        //Check the user input text,image
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID= auth.getCurrentUser().getUid();
                //check if input is not empty
                if( ImageUri != null){
                    //upload image to data storage name "profile_image"
                    StorageReference filepath = storageReference.child("profile_image").child(ImageUri.getLastPathSegment());
                    filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                            databaseUsers.child(userID).child("image").setValue(downloadUrl);

                            Toast.makeText(ProfileActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
    //Crop and get image result.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQ && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(requestCode == RESULT_OK){
                ImageUri = result.getUri();
                imageButton.setImageURI(ImageUri);
            }
            else {
                if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception err = result.getError();
                }
            }
        }

    }
}
