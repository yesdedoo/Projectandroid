package com.example.akarinzahotmailcom.projectandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SinglePostActivity extends AppCompatActivity{
    private ImageView singleImage;
    private TextView singleTitle;
    private TextView singleDescription;
    String post_key = null;
    private DatabaseReference database;
    private Button deleteButton;
    private FirebaseAuth auth;

    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_single_post);

        singleImage = (ImageView)findViewById(R.id.singleImageview);
        singleTitle = (TextView)findViewById(R.id.singleTitle);
        singleDescription = (TextView)findViewById(R.id.singleDesc);
        database = FirebaseDatabase.getInstance().getReference().child("Reviewer");
        post_key = getIntent().getExtras().getString("PostID");
        deleteButton = (Button)findViewById(R.id.deleteBtn);
        auth = FirebaseAuth.getInstance();
        deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child(post_key).removeValue();
                Intent mainintent = new Intent(SinglePostActivity.this, MainActivity.class);
                startActivity(mainintent);
            }
        });

        database.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String)dataSnapshot.child("title").getValue();
                String post_description = (String)dataSnapshot.child("description").getValue();
                String post_image = (String)dataSnapshot.child("imageUrl").getValue();
                String post_uid = (String )dataSnapshot.child("uid").getValue();

                singleTitle.setText(post_title);
                singleDescription.setText(post_description);
                Picasso.with(SinglePostActivity.this).load(post_image).into(singleImage);
                if(auth.getCurrentUser().getUid().equals(post_uid)){
                    deleteButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
