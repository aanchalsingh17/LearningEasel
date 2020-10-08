package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText name_reg,email_reg, password_reg,phone_reg;
    TextView loginBtn_reg;
    Button createBtn_reg;
    ProgressBar progressBar_reg;
    FirebaseAuth fAuth_reg;
    String userID;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    SharedPreferences sharedPreferences;
    //FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name_reg        = findViewById(R.id.name_reg);
        email_reg       = findViewById(R.id.email_reg);
        password_reg    = findViewById(R.id.password_reg);
        phone_reg       = findViewById(R.id.mobile_reg);
        createBtn_reg   = findViewById(R.id.register_reg);
        loginBtn_reg    = findViewById(R.id.login_reg);
        progressBar_reg = findViewById(R.id.progressBar_reg);
        fAuth_reg       = FirebaseAuth.getInstance();
        fUser           =   fAuth_reg.getCurrentUser();
        fStore          = FirebaseFirestore.getInstance();

        if(fUser!= null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        createBtn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_reg.getText().toString().trim();
                final String password = password_reg.getText().toString().trim();
                final String fullName = name_reg.getText().toString();
                final String phone    = phone_reg.getText().toString();

                if(TextUtils.isEmpty(fullName)){
                    name_reg.setError("Username is Required.");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    email_reg.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(phone)){
                    phone_reg.setError("Phone Number is Required.");
                    return;
                }

                if(phone.length() <10){
                    phone_reg.setError("Enter valid Phone Number");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    password_reg.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6){
                    password_reg.setError("Password Must be >= 6 Characters");
                    return;
                }
                SharedPreferences preferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("email_Id",email);
                editor.commit();
                progressBar_reg.setVisibility(View.VISIBLE);

                fAuth_reg.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //send email verification link
                            FirebaseUser fuser = fAuth_reg.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification email has been sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("tag","onFailure : Email not sent "+ e.getMessage());
                                }
                            });



                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();

                            //Storing data in firestore

                            userID = fAuth_reg.getCurrentUser().getUid();                                                           //user id stored

                            DocumentReference documentReference = fStore.collection("users").document(userID);          // firestore cloud database
                            Map<String, Object> user = new HashMap<>();                                                             //user data stored in HashMap
                            user.put("fName",fullName);
                            user.put("email",email);
                            user.put("phone",phone);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("tag", "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("tag", "onFailure: " + e.toString());
                                }
                            });
                            // after registration redirect to main class
//                                sharedPreferences=getSharedPreferences("MyPref", Context.MODE_PRIVATE);
//                                String folder = sharedPreferences.getString("email_Id", "");
//                                int j=folder.length()-4;
//                                final String username=folder.substring(0,j);
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser users = firebaseAuth.getCurrentUser();
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            int j=email.length()-4;
                            final String username=email.substring(0,j);
                            final DatabaseReference myRef=database.getReference().child(username);

                            myRef.child("Science").setValue("0");
                            myRef.child("Medication").setValue("0");
                            myRef.child("Computers").setValue("0");
                            myRef.child("Business").setValue("0");
                            myRef.child("Environment").setValue("0");
                            myRef.child("Arts").setValue("0");
                            myRef.child("Sports").setValue("0");
                            myRef.child("Economics").setValue("0");
                            myRef.child("Architecture").setValue("0");

                            startActivity(new Intent(getApplicationContext(),Profile.class));
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                            finish();


                        }else if(task.getException()!=null) {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar_reg.setVisibility(View.GONE);
                        }
                    }
                });
            }

        });

        //when login button is pressed

        loginBtn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                finish();

            }
        });




    }
}