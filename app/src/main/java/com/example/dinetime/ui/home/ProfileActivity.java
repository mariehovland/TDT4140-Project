package com.example.dinetime.ui.home;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.dinetime.MainActivity;
import com.example.dinetime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = user.getUid();
    private static final String TAG = "ProfileActivity";

    public ImageButton back;
    public TextView profileEmail;
    public TextView profileName;
    public TextView profileAddress;
    public TextView profileAllergies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("UserData");

        back = findViewById(R.id.backButtonProfile);
        profileEmail = findViewById(R.id.profileEmail);
        profileName = findViewById(R.id.profileName);
        profileAddress = findViewById(R.id.profileAddress);
        profileAllergies = findViewById(R.id.profileAllergies);

        try {
            if (user != null) {
                // User is signed in
                updateUI();
                System.out.println("Logged in");
            } else {
                // No user is signed in
                System.out.println("No user is signed in");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("NullPointerException");
        }

         back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

    }

    private void updateUI() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String nameString = snapshot.child(userID).child("firstName").getValue(String.class)
                        + " " + snapshot.child(userID).child("lastName").getValue(String.class);
                String addressString = snapshot.child(userID).child("address").getValue(String.class);
                String allergyString = "";

                profileName.setText(nameString);
                profileAddress.setText(addressString);
                profileEmail.setText(user.getEmail());

                for (DataSnapshot ds : snapshot.child("allergies").getChildren()) {
                    if (!allergyString.isEmpty()) {
                        allergyString += ", " + snapshot.child(userID).child("allergies").getValue();
                    } else {
                        allergyString += snapshot.child(userID).child("allergies").getValue();
                    }
                }
                profileAllergies.setText(allergyString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

}