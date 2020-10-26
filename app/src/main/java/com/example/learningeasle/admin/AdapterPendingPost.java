package com.example.learningeasle.admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.PushNotifications.APIService;
import com.example.learningeasle.PushNotifications.Client;
import com.example.learningeasle.PushNotifications.Data;
import com.example.learningeasle.PushNotifications.NotificationSender;
import com.example.learningeasle.PushNotifications.MyResponse;
import com.example.learningeasle.R;
import com.example.learningeasle.ViewAttachement;
import com.example.learningeasle.ViewImage;
import com.example.learningeasle.model.modelpost;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterPendingPost extends RecyclerView.Adapter<AdapterPendingPost.PendingHolder> {

    Context context;
    List<modelpost> pendingpostList;
    View view;
    boolean notify;
    String userId;

    private APIService apiService;
    public AdapterPendingPost(Context context, List<modelpost> pendingpostList) {
        this.context = context;
        this.pendingpostList = pendingpostList;
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

    }

    @NonNull
    @Override
    public PendingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pending_post, parent, false);
        return new PendingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PendingHolder holder, int position) {
        final String uName = pendingpostList.get(position).getpName();
        final String url = pendingpostList.get(position).getuImage();
         final String pTitle = pendingpostList.get(position).getpTitle();
        final String pDescription = pendingpostList.get(position).getpDesc();
        final String pImage = pendingpostList.get(position).getpImage();
       final String  pTimeStamp = pendingpostList.get(position).getpTime();
        final String pId = pendingpostList.get(position).getpId();
        final String pType = pendingpostList.get(position).getpType();
        final String videourl = pendingpostList.get(position).getVideourl();
        final String pdfurl = pendingpostList.get(position).getPdfurl();
        final String audiourl = pendingpostList.get(position).getAudiourl();
        //Initialise Shimmer
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




        //Setting al the data at the specified position of holder;
        holder.uName.setText(uName);
        holder.pDescpType.setText(pType);
        holder.pDes.setText(pDescription);
        holder.pTitle.setText(pTitle);
        //Converting the timestamp from the database in readable format;
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));

        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.pTime.setText(pTime);
        if(pImage.equals("noImage")){
            holder.pImage.setVisibility(View.GONE);
        }else{
            holder.pImage.setVisibility(View.VISIBLE);
            Picasso.get().load(pImage).placeholder(shimmerDrawable).fit().centerCrop().into(holder.pImage);
        }
        if (url.equals("empty"))
            holder.uDp.setImageResource(R.drawable.ic_action_account);
        else
            Picasso.get().load(url).placeholder(shimmerDrawable).into(holder.uDp);
        //If Admin Publishes the post then change the reference of the post in the database
        holder.pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewImage.class);
                intent.putExtra("image", pImage);
                context.startActivity(intent);
            }
        });
        holder.publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the post from the pending post reference
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admin").child("pendingpost");//.child(pTimeStamp);
                ref.child(pTimeStamp).removeValue();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                HashMap<Object,Object> hashMap = new HashMap<>();
                hashMap.put("pId",pId);
                hashMap.put("pTitle",pTitle);
                hashMap.put("pDesc",pDescription);
                hashMap.put("pImage",pImage);
                hashMap.put("pLikes","0");
                hashMap.put("pTime",pTimeStamp);
                hashMap.put("type",pType);
                hashMap.put("url",url);
                hashMap.put("pName",uName);
                hashMap.put("pComments","0");
                hashMap.put("videourl",videourl);
                hashMap.put("pdfurl",pdfurl);
                hashMap.put("audiourl",audiourl);
                hashMap.put("order",-Long.parseLong(pTimeStamp));
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Views");
                databaseReference.child(pTimeStamp).setValue("0");
                reference.child(pTimeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Post Published !!",Toast.LENGTH_SHORT).show();
                    }
                });

                FirebaseDatabase.getInstance().getReference().child("Tokens").child(pId).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String usertoken=dataSnapshot.getValue(String.class);
                        sendNotifications(usertoken, "Admin published your post - "+pTitle,pDescription);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        //If admin cancel the post then delete the post from the pending post list and dont publish it
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //If admin Cancel the post then simply delete the post from the pending post and nothing else to do
                //Since at this time post doesnt contain any nested data thats why simply removing the value work here

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admin").child("pendingpost");//.child(pTimeStamp);
                ref.child(pTimeStamp).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Post Not Published!!",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Unable to complete the action!!",Toast.LENGTH_SHORT).show();
                    }
                });

                FirebaseDatabase.getInstance().getReference().child("Tokens").child(pId).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String usertoken=dataSnapshot.getValue(String.class);
                        sendNotifications(usertoken, "Admin disapproved your post - "+pTitle,pDescription);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        if(!videourl.equals("empty")||!(audiourl.equals("empty"))||!(pdfurl.equals("empty"))){
            holder.attachement.setVisibility(View.VISIBLE);
        }
        holder.attachement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!videourl.equals("empty")){
                    holder.video_btn.setVisibility(View.VISIBLE);

                }
                if(!pdfurl.equals("empty")){
                    holder.pdf_btn.setVisibility(View.VISIBLE);
                }
                if(!audiourl.equals("empty")){
                    holder.audio_btn.setVisibility(View.VISIBLE);
                }
            }
        });
        //Pass the url of the attached file which user want to view
        holder.video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewAttachement.class);
                intent.putExtra("videourl",videourl);
                intent.putExtra("audiourl","empty");
                intent.putExtra("pdfurl","empty");
                context.startActivity(intent);
            }
        });
        holder.audio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewAttachement.class);
                intent.putExtra("videourl","empty");
                intent.putExtra("audiourl",audiourl);
                intent.putExtra("pdfurl","empty");
                context.startActivity(intent);
            }
        });
        holder.pdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewAttachement.class);
                intent.putExtra("videourl","empty");
                intent.putExtra("audiourl","empty");
                intent.putExtra("pdfurl",pdfurl);
                context.startActivity(intent);
            }
        });

    }


    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccessful != 1) {
                        Toast.makeText(context, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingpostList.size();
    }

    public static class PendingHolder extends RecyclerView.ViewHolder{

        ImageView uDp,pImage;
        TextView uName, pTime, pTitle, pDescpType,pDes;
        Button publish,cancel;
        FloatingActionButton attachement,video_btn,audio_btn,pdf_btn;
        public PendingHolder(@NonNull View itemView) {
            super(itemView);
            uDp = itemView.findViewById(R.id.uDp);
            pImage = itemView.findViewById(R.id.pImage);
            uName = itemView.findViewById(R.id.uname);
            pTime = itemView.findViewById(R.id.time);
            pTitle = itemView.findViewById(R.id.ptitle);
            pDescpType = itemView.findViewById(R.id.pType);
            pDes = itemView.findViewById(R.id.pdesc);
            publish = itemView.findViewById(R.id.publish);
            cancel = itemView.findViewById(R.id.cancel);
            attachement = itemView.findViewById(R.id.view_attached);
            audio_btn = itemView.findViewById(R.id.audio_upload);
            pdf_btn = itemView.findViewById(R.id.pdf_upload);
            video_btn = itemView.findViewById(R.id.video_upload);
        }
    }
}
