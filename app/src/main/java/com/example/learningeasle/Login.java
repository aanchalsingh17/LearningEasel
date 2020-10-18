package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    EditText email_login, password_login;
    Button loginBtn_login;
    TextView createBtn_login, forgot_password;
    ProgressBar progressBar_login;
    FirebaseAuth fAuth_login;
    SignInButton signin;
    // FirebaseFirestore fStore;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    GoogleSignInClient mgooglesignin;
    String url;
    private int RC_SIGN_IN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //   fStore = FirebaseFirestore.getInstance();
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        progressBar_login = findViewById(R.id.progressBar_login);
        fAuth_login = FirebaseAuth.getInstance();
        loginBtn_login = findViewById(R.id.login_login);
        createBtn_login = findViewById(R.id.create_login);
        forgot_password = findViewById(R.id.forgot_password);
        fUser = fAuth_login.getCurrentUser();
        signin = findViewById(R.id.googlesignin);
        fStore = FirebaseFirestore.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).
                requestEmail().build();

        mgooglesignin = GoogleSignIn.getClient(this, gso);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        if (fUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        loginBtn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String email = email_login.getText().toString().trim();
                final String password = password_login.getText().toString().trim();


                //Check for error in entered values
                if (TextUtils.isEmpty(email)) {
                    email_login.setError("Email is Required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    password_login.setError("Password is Required!");
                    return;
                }

                if (password.length() < 6) {
                    password_login.setError("Password must be >= 6 characters");
                    return;
                }

                loginUser(email, password);
            }

        });

        //if signup/register button is pressed
        createBtn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

            }
        });


        //forgot password

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(new ContextThemeWrapper(v.getContext(), R.style.AlertDialogCustom));
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter email to receive reset link ");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract email and set reset link

                        String mail = resetMail.getText().toString();
                        fAuth_login.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Reset link sent to email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error! Reset link not sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });
//                passwordResetDialog.create().show();
                AlertDialog alert = passwordResetDialog.create();
                alert.show();
                //    Customising buttons for dialog
                Button p = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                p.setBackgroundColor(Color.parseColor("#222831"));
                p.setTextColor(Color.parseColor("#D90091EA"));
                Button n = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                n.setBackgroundColor(Color.parseColor("#222831"));
                n.setTextColor(Color.parseColor("#DEFFFFFF"));
            }
        });


    }

    //GoogleSignIn
    private void signIn() {
        Intent signinintent = mgooglesignin.getSignInIntent();
        startActivityForResult(signinintent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(Login.this, "Successfully Signed in", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
        } catch (ApiException ae) {
            Toast.makeText(Login.this, "Failure", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fAuth_login.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //task is completed
                FirebaseUser user = fAuth_login.getCurrentUser();
                addUserInfo(user);
            }
        });
    }

    private void addUserInfo(final FirebaseUser user) {
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            SharedPreferences preferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email_Id", account.getEmail());
            editor.commit();
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
            final boolean[] start = new boolean[1];
            start[0]=true;
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChild(user.getUid())){
                        start[0] =false;
                        final HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("Name",account.getDisplayName());
                        hashMap.put("Id",user.getUid());
                        hashMap.put("Url", "empty");
                        hashMap.put("email",account.getEmail());
                        hashMap.put("phone","");
                        hashMap.put("status","");
                        reference.child(user.getUid()).setValue(hashMap);
                        String email = account.getEmail();
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
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

                        System.out.println("pick");

                        startActivity(new Intent(Login.this,PickInterests.class));
                        finish();

                    }
                    else if(start[0] == true){
                        System.out.println("main");
                        startActivity(new Intent(Login.this,MainActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }
    }
    // login function

    private void loginUser(String email, String password) {
        SharedPreferences preferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email_Id", email);
        editor.commit();
        progressBar_login.setVisibility(View.VISIBLE);
        hideKeyboard(Login.this);

        // authenticate user
        fAuth_login.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Welcome User!!", Toast.LENGTH_SHORT).show();
                    //Uploading profile pic n name n uid in realtimedatabase  to show all the users in the users fragment;

                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    finish();

                } else if (task.getException() != null) {
                    Toast.makeText(Login.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }


                progressBar_login.setVisibility(View.GONE);
            }
        });

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}

