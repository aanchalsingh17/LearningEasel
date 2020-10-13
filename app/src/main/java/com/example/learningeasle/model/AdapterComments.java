package com.example.learningeasle.model;

import android.content.Context;
import android.icu.number.CompactNotation;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.holder>{

    Context context;
    List<ModelComment> commentList;
    public AdapterComments(Context context, List<ModelComment> commentList) {
        this.context = context;
        this.commentList=commentList;
    }


    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        String uid=commentList.get(position).getUid();
        String name=commentList.get(position).getuName();
        String image=commentList.get(position).getuDp();
        String cid=commentList.get(position).getcId();
        String comment=commentList.get(position).getComment();
        String timestamp=commentList.get(position).getTimestamp();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));

        final String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.nameTV.setText(name);
        holder.commentTV.setText(comment);
        holder.timeTV.setText(pTime);

        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_default).into(holder.avatarIV);
        }

        catch(Exception e){
            Picasso.get().load(R.drawable.ic_default).into(holder.avatarIV);
        }



    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class holder extends RecyclerView.ViewHolder{

        ImageView avatarIV;
        TextView nameTV,commentTV,timeTV;

        public holder(@NonNull View itemView) {
            super(itemView);
            avatarIV=itemView.findViewById(R.id.avtar1);
            nameTV=itemView.findViewById(R.id.name1);
            commentTV=itemView.findViewById(R.id.comment1);
            timeTV=itemView.findViewById(R.id.time1);

        }

    }

}
