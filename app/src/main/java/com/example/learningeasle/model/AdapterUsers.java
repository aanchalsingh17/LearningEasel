package com.example.learningeasle.model;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.UserHolder>{
   Context context;
   List<ModelUsers> userList;

    public AdapterUsers(Context context, List<ModelUsers> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
          final String userName = userList.get(position).getName();
          final String email = userList.get(position).getEmail();
          final String url = userList.get(position).getUrl();
          final String Uid = userList.get(position).getId();
          holder.name.setText(userName);
          holder.email.setText(email);
          if(url==null)
            holder.profile.setImageResource(R.drawable.ic_action_account);
          else
            Picasso.get().load(url).into(holder.profile);
          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Toast.makeText(context,"Account Clicked" + userName,Toast.LENGTH_SHORT).show();
              }
          });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {


        ImageView profile;
        TextView name,email;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
        }
    }
}
