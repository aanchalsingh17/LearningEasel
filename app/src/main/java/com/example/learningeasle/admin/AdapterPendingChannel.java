package com.example.learningeasle.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.R;
import com.example.learningeasle.model.modelpost;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterPendingChannel extends RecyclerView.Adapter<AdapterPendingChannel.PendingChannel> {
    Context context;
    List<ModelPendingChannel> modelPendingChannels;
    View view;

    public AdapterPendingChannel(Context context, List<ModelPendingChannel> modelPendingChannels) {
        this.context = context;
        this.modelPendingChannels = modelPendingChannels;
    }


    @NonNull
    @Override
    public PendingChannel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pending_channel, parent, false);
        return new PendingChannel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingChannel holder, int position) {
        final String title = modelPendingChannels.get(position).getChannelName();
        final String des = modelPendingChannels.get(position).getDes();
        final String url = modelPendingChannels.get(position).getImageurl();


        Picasso.get().load(url).into(holder.coverImage);

        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
        //Initialise shimmer Drawable
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        holder.channelname.setText(title);
        holder.Description.setText(des);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<Object, Object> channels = new HashMap<>();
                channels.put("cName", title);
                channels.put("cDes", des);
                channels.put("cUrl", url);
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Applying Changes...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("channel");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(title)) {
                            Toast.makeText(context, "Channel Name Already Exists", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            reference.child(title).setValue(channels).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "New Channel Added", Toast.LENGTH_SHORT).show();
                                    //Whenever Admin Add new Channel  add the channel into the users profile
                                    final DatabaseReference user_profile = FirebaseDatabase.getInstance().getReference("Users");
                                    user_profile.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot user : snapshot.getChildren()) {
                                                String user_email = (String) user.child("email").getValue();
                                                String Id = (String) user.child("Id").getValue();
                                                int j = user_email.length() - 4;
                                                final String username = user_email.substring(0, j);
                                                user_profile.child(Id).child(username).child(title).setValue("0");
                                            }
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
                                        }
                                    });
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("pendingchannel");
                                    reference.child(title).removeValue();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });

            }
        });
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("pendingchannel").child(title);
                StorageReference coverRe = FirebaseStorage.getInstance().getReference().child("Channel/" + title);
                coverRe.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        reference.removeValue();
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return modelPendingChannels.size();
    }

    public static class PendingChannel extends RecyclerView.ViewHolder {
        TextView channelname, Description;
        Button add, cancel;
        ImageView coverImage;

        public PendingChannel(@NonNull View itemView) {
            super(itemView);
            channelname = itemView.findViewById(R.id.name);
            Description = itemView.findViewById(R.id.desc);
            add = itemView.findViewById(R.id.publish);
            cancel = itemView.findViewById(R.id.cancel);
            coverImage = itemView.findViewById(R.id.coverImage);
        }
    }
}
