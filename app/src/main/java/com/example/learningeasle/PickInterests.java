package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.color.holo_red_dark;

public class PickInterests extends AppCompatActivity {
    Button btn_science, btn_medication, btn_computers, btn_business, btn_environment, btn_arts, btn_sports, btn_economics, btn_arch;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_interests);

        SharedPreferences sharedPreferences=getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String folder = sharedPreferences.getString("email_Id", "");
        int j = folder.length() - 4;
        final String username = folder.substring(0, j);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef= database.getReference().child(username);

        btn_science = findViewById(R.id.science);
        btn_medication = findViewById(R.id.medication);
        btn_computers = findViewById(R.id.computers);
        btn_business = findViewById(R.id.business);
        btn_environment = findViewById(R.id.environment);
        btn_arts = findViewById(R.id.arts);
        btn_sports = findViewById(R.id.sports);
        btn_economics = findViewById(R.id.economics);
        btn_arch = findViewById(R.id.architecture);

        setColors();



        btn_science.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Science").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data.equals("0")) {
                            myRef.child("Science").setValue("1");
                            btn_science.setBackgroundResource(R.drawable.button_shaper_red);
                            btn_science.setText("Unfollow");
                            btn_science.setTextColor(getApplication().getResources().getColor(R.color.text));
                        } else {
                            myRef.child("Science").setValue("0");
                            btn_science.setBackgroundResource(R.drawable.button_shaper_aqua);
                            btn_science.setText("Follow Us");
                            btn_science.setTextColor(getApplication().getResources().getColor(R.color.textDark));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Medication").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data.equals("0")) {
                            myRef.child("Medication").setValue("1");
                            btn_medication.setBackgroundResource(R.drawable.button_shaper_red);
                            btn_medication.setText("Unfollow");
                            btn_medication.setTextColor(getApplication().getResources().getColor(R.color.text));
                        } else {
                            myRef.child("Medication").setValue("0");
                            btn_medication.setBackgroundResource(R.drawable.button_shaper_aqua);
                            btn_medication.setText("Follow Us");
                            btn_medication.setTextColor(getApplication().getResources().getColor(R.color.textDark));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_computers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Computers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data.equals("0")) {
                            myRef.child("Computers").setValue("1");
                            btn_computers.setBackgroundResource(R.drawable.button_shaper_red);
                            btn_computers.setText("Unfollow");
                            btn_computers.setTextColor(getApplication().getResources().getColor(R.color.text));
                        } else {
                            myRef.child("Computers").setValue("0");
                            btn_computers.setBackgroundResource(R.drawable.button_shaper_aqua);
                            btn_computers.setText("Follow Us");
                            btn_computers.setTextColor(getApplication().getResources().getColor(R.color.textDark));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Business").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data.equals("0")) {
                            myRef.child("Business").setValue("1");
                            btn_business.setBackgroundResource(R.drawable.button_shaper_red);
                            btn_business.setText("Unfollow");
                            btn_business.setTextColor(getApplication().getResources().getColor(R.color.text));
                        } else {
                            myRef.child("Business").setValue("0");
                            btn_business.setBackgroundResource(R.drawable.button_shaper_aqua);
                            btn_business.setText("Follow Us");
                            btn_business.setTextColor(getApplication().getResources().getColor(R.color.textDark));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_environment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Environment").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data.equals("0")) {
                            myRef.child("Environment").setValue("1");
                            btn_environment.setBackgroundResource(R.drawable.button_shaper_red);
                            btn_environment.setText("Unfollow");
                            btn_environment.setTextColor(getApplication().getResources().getColor(R.color.text));
                        } else {
                            myRef.child("Environment").setValue("0");
                            btn_environment.setBackgroundResource(R.drawable.button_shaper_aqua);
                            btn_environment.setText("Follow Us");
                            btn_environment.setTextColor(getApplication().getResources().getColor(R.color.textDark));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_arts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Arts").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data.equals("0")) {
                            myRef.child("Arts").setValue("1");
                            btn_arts.setBackgroundResource(R.drawable.button_shaper_red);
                            btn_arts.setText("Unfollow");
                            btn_arts.setTextColor(getApplication().getResources().getColor(R.color.text));
                        } else {
                            myRef.child("Arts").setValue("0");
                            btn_arts.setBackgroundResource(R.drawable.button_shaper_aqua);
                            btn_arts.setText("Follow Us");
                            btn_arts.setTextColor(getApplication().getResources().getColor(R.color.textDark));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Sports").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data.equals("0")) {
                            myRef.child("Sports").setValue("1");
                            btn_sports.setBackgroundResource(R.drawable.button_shaper_red);
                            btn_sports.setText("Unfollow");
                            btn_sports.setTextColor(getApplication().getResources().getColor(R.color.text));
                        } else {
                            myRef.child("Sports").setValue("0");
                            btn_sports.setBackgroundResource(R.drawable.button_shaper_aqua);
                            btn_sports.setText("Follow Us");
                            btn_sports.setTextColor(getApplication().getResources().getColor(R.color.textDark));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_economics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Economics").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data.equals("0")) {
                            myRef.child("Economics").setValue("1");
                            btn_economics.setBackgroundResource(R.drawable.button_shaper_red);
                            btn_economics.setText("Unfollow");
                            btn_economics.setTextColor(getApplication().getResources().getColor(R.color.text));
                        } else {
                            myRef.child("Economics").setValue("0");
                            btn_economics.setBackgroundResource(R.drawable.button_shaper_aqua);
                            btn_economics.setText("Follow Us");
                            btn_economics.setTextColor(getApplication().getResources().getColor(R.color.textDark));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_arch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("Architecture").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data.equals("0")) {
                            myRef.child("Architecture").setValue("1");
                            btn_arch.setBackgroundResource(R.drawable.button_shaper_red);
                            btn_arch.setText("Unfollow");
                            btn_arch.setTextColor(getApplication().getResources().getColor(R.color.text));
                        } else {
                            myRef.child("Architecture").setValue("0");
                            btn_arch.setBackgroundResource(R.drawable.button_shaper_aqua);
                            btn_arch.setText("Follow Us");
                            btn_arch.setTextColor(getApplication().getResources().getColor(R.color.textDark));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    public void setColors(){
        myRef.child("Science").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue().toString();
                if (data.equals("1")) {
                    btn_science.setBackgroundResource(R.drawable.button_shaper_red);
                    btn_science.setText("Unfollow");
                    btn_science.setTextColor(getApplication().getResources().getColor(R.color.text));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRef.child("Medication").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue().toString();
                if (data.equals("1")) {
                    btn_medication.setBackgroundResource(R.drawable.button_shaper_red);
                    btn_medication.setText("Unfollow");
                    btn_medication.setTextColor(getApplication().getResources().getColor(R.color.text));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRef.child("Computers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue().toString();
                if (data.equals("1")) {
                    btn_computers.setBackgroundResource(R.drawable.button_shaper_red);
                    btn_computers.setText("Unfollow");
                    btn_computers.setTextColor(getApplication().getResources().getColor(R.color.text));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child("Business").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue().toString();
                if (data.equals("1")) {
                    btn_business.setBackgroundResource(R.drawable.button_shaper_red);
                    btn_business.setText("Unfollow");
                    btn_business.setTextColor(getApplication().getResources().getColor(R.color.text));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRef.child("Environment").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue().toString();
                if (data.equals("1")) {
                    btn_environment.setBackgroundResource(R.drawable.button_shaper_red);
                    btn_environment.setText("Unfollow");
                    btn_environment.setTextColor(getApplication().getResources().getColor(R.color.text));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRef.child("Arts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue().toString();
                if (data.equals("1")) {
                    btn_arts.setBackgroundResource(R.drawable.button_shaper_red);
                    btn_arts.setText("Unfollow");
                    btn_arts.setTextColor(getApplication().getResources().getColor(R.color.text));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRef.child("Sports").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue().toString();
                if (data.equals("1")) {
                    btn_sports.setBackgroundResource(R.drawable.button_shaper_red);
                    btn_sports.setText("Unfollow");
                    btn_sports.setTextColor(getApplication().getResources().getColor(R.color.text));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRef.child("Economics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue().toString();
                if (data.equals("1")) {
                    btn_economics.setBackgroundResource(R.drawable.button_shaper_red);
                    btn_economics.setText("Unfollow");
                    btn_economics.setTextColor(getApplication().getResources().getColor(R.color.text));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRef.child("Architecture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = snapshot.getValue().toString();
                if (data.equals("1")) {
                    btn_arch.setBackgroundResource(R.drawable.button_shaper_red);
                    btn_arch.setText("Unfollow");
                    btn_arch.setTextColor(getApplication().getResources().getColor(R.color.text));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

