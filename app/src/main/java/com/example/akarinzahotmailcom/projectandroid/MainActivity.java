package com.example.akarinzahotmailcom.projectandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    //innitialize state
    private RecyclerView recyclerView;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ImageView addItem;
    private ImageView signoutButton;
    private ImageView profileButton;

    @Override
    /*Set the initial variable or action of the activity.
Also intent to other activity addition with
authentication of the user in database */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize recyclerview and FIrebase objects
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //get reference of data from database name "Reviewer"
        database = FirebaseDatabase.getInstance().getReference().child("Reviewer");

        //get authentication instance from firebase.
        auth = FirebaseAuth.getInstance();
        addItem = (ImageButton)findViewById(R.id.additem);
        signoutButton = (ImageButton)findViewById(R.id.signoutBtn);
        profileButton = (ImageButton)findViewById(R.id.profileBtn);

        //Listener call when there when their is a change in Authentication state.
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override

            //This method gets invoked in the UI thread on changes in the authentication state
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null){
                    Intent loginIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
        //add post activity
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });
        //sign out activity
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
            }
        });
        //Go to profile page
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

    }
    /*Call for the Post's object from the database
and set the View of the layout and intent*/
    protected void onStart(){
        super.onStart();
        //show all feed when it change user.
        auth.addAuthStateListener(authStateListener);
        //set recycler
        FirebaseRecyclerAdapter<Reviewer,ReviewerViewHolder> FBRA = new FirebaseRecyclerAdapter<Reviewer, ReviewerViewHolder>(Reviewer.class,R.layout.card_items,ReviewerViewHolder.class,database) {
            @Override
            protected void populateViewHolder(ReviewerViewHolder viewHolder, Reviewer model, int position) {
                final String post_key = getRef(position).getKey().toString();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImageUrl(getApplicationContext(), model.getImageUrl());
                viewHolder.setUserName(model.getUsername());
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleActivity = new Intent(MainActivity.this, SinglePostActivity.class);
                        singleActivity.putExtra("PostID", post_key);
                        startActivity(singleActivity);
                    }
                });
            }
        };
        //Adapter for a recycler view.
        recyclerView.setAdapter(FBRA);
    }
    //Set view holder at each post.
    //Set the View in the layout
    public static class ReviewerViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ReviewerViewHolder(View itemView){
            super(itemView);
            view = itemView;
        }
        public void setTitle(String title){
            TextView post_title = view.findViewById(R.id.post_title_txtview);
            post_title.setText(title);
        }
        public void setDesc(String desc){
            TextView post_desc = view.findViewById(R.id.post_desc_txtview);
            post_desc.setText(desc);
        }
        public void setImageUrl(Context ctx, String imageUrl){
            ImageView post_image = view.findViewById(R.id.post_image);
            Picasso.with(ctx).load(imageUrl).into(post_image);
        }
        public void setUserName(String userName){
            TextView postUserName = view.findViewById(R.id.post_user);
            postUserName.setText(userName);
        }
    }

}
