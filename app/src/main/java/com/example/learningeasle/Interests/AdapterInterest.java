package com.example.learningeasle.Interests;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterInterest extends RecyclerView.Adapter<AdapterInterest.InterestHolder> {
    public Context context;
    List<ModelInterest> modelInterests;
    View view;
    String follow;
    String userId,username,email;
    public AdapterInterest(Context context, List<ModelInterest> modelInterests) {
        this.context = context;
        this.modelInterests = modelInterests;
    }
    @NonNull
    @Override
    public InterestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.row_interest, parent, false);
        return new InterestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InterestHolder holder, int position) {
        final String name = modelInterests.get(position).getChannelName();
        follow = modelInterests.get(position).getValue();

        //Get the model name and its value i.e user follow this channel or not
        holder.name.setText(name);
        setfollow(follow,holder);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            userId = user.getUid();
        }

       //Follow button is clicked if user follow this page unfollow it and vie-versa
        holder.followButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("email");
               reference.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       email = (String) snapshot.getValue();
                       username=email.substring(0,email.length()-4);
                       final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(userId).child(username);
                       reference1.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               String data = snapshot.getValue().toString();
                               if (data.equals("0")) {
                                   reference1.child(name).setValue("1");
                                   holder.followButton.setBackgroundResource(R.drawable.button_shaper_red);
                                   holder.followButton.setText("Unfollow");
                                   holder.followButton.setTextColor(context.getResources().getColor(R.color.textDark));
                               } else {
                                   reference1.child(name).setValue("0");
                                   holder.followButton.setBackgroundResource(R.drawable.button_alltabs);
                                   holder.followButton.setText("Follow Us");
                                   holder.followButton.setTextColor(context.getResources().getColor(R.color.text));
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });


           }
       });
        //When an item view is clicked Go to detail view of that channel
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view_interest = new Intent(context, ViewChannel.class);
                view_interest.putExtra("channel",name);
                context.startActivity(view_interest);
            }
        });

    }

    //Checking if user follow this field or not and se the text and colour accordingly
    private void setfollow(String type,InterestHolder holder) {
        if(type.equals("0")){
            holder.followButton.setBackgroundResource(R.drawable.button_alltabs);
            holder.followButton.setText("Follow Us");
            holder.followButton.setTextColor(context.getResources().getColor(R.color.text));
        }else{
            holder.followButton.setBackgroundResource(R.drawable.button_shaper_red);
            holder.followButton.setText("Unfollow");
            holder.followButton.setTextColor(context.getResources().getColor(R.color.textDark));
        }
    }


    @Override
    public int getItemCount() {
        return modelInterests.size();
    }

    public static class InterestHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button followButton;
        public InterestHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.channel);
            followButton = itemView.findViewById(R.id.follow_btm);

        }
    }
}
