package com.example.learningeasle.chats;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ModelChat>chatList;
    String imageUrl;
    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_LEFT){
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new MyHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String message=chatList.get(position).getMessage();
        String timeStamp=chatList.get(position).getTimestamp();

        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timeStamp));

        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.messageTV.setText(message);
        holder.timeTV.setText(dateTime);

        try {

            Picasso.get().load(imageUrl).fit().centerCrop().into(holder.profileIV);
        } catch (Exception e) {
        }

        if(position==chatList.size()-1){
            if(chatList.get(position).isSeen().equals("1")) {
                holder.isSeenTV.setText("Seen");
            }
            else
                holder.isSeenTV.setText("Delivered");
        }
        else
        {
            holder.isSeenTV.setVisibility(View.GONE);
        }
    }



    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }

    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView profileIV;
        TextView messageTV,timeTV,isSeenTV;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profileIV=itemView.findViewById(R.id.profileIV);
            messageTV=itemView.findViewById(R.id.messageTV);
            isSeenTV=itemView.findViewById(R.id.isSeenTV);
            timeTV=itemView.findViewById(R.id.timeTV);
        }
    }

}
