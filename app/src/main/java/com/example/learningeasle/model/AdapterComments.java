package com.example.learningeasle.model;

import android.content.Context;
import android.icu.number.CompactNotation;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    public void onBindViewHolder(@NonNull final holder holder, final int position) {
        String uid=commentList.get(position).getUid();
        String name=commentList.get(position).getuName();
        String image=commentList.get(position).getuDp();
        String cid=commentList.get(position).getcId();
        String comment=commentList.get(position).getComment();
        final String timestamp=commentList.get(position).getTimestamp();
        final String postId = commentList.get(position).getPostId();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        //If the comment is posted by current user than make delete text visible
        final String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(uid.equals(currentuser)){
            holder.delete.setVisibility(View.VISIBLE);
        }
        // if the current user is admin then also he can delete some irrelevant comment
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("Id");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(currentuser))
                    holder.delete.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //Set the details of the comment i.e comment time of comment and user
        holder.nameTV.setText(name);
        holder.commentTV.setText(comment);
        holder.timeTV.setText(pTime);

        //Set the userdp
        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_default).into(holder.avatarIV);
        }

        catch(Exception e){
            Picasso.get().load(R.drawable.ic_default).into(holder.avatarIV);
        }

        //Delete the comment
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments").child(timestamp);
                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        updateCommentCount(postId);
                        Toast.makeText(context,"Comment Deleted",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    boolean mProcessComment = false;
    //When someone commented update the commnt count
    private void updateCommentCount(String postId) {
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessComment) {
                    String comments = (String) snapshot.child("pComments").getValue();
                    if (comments == null)
                        comments = "0";
                    int newCommentVal = Integer.parseInt(comments) - 1;
                    ref.child("pComments").setValue("" + (newCommentVal));
                    mProcessComment = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class holder extends RecyclerView.ViewHolder{

        ImageView avatarIV;
        TextView nameTV,commentTV,timeTV;
        TextView delete;
        public holder(@NonNull View itemView) {
            super(itemView);
            avatarIV=itemView.findViewById(R.id.avtar1);
            nameTV=itemView.findViewById(R.id.name1);
            commentTV=itemView.findViewById(R.id.comment1);
            timeTV=itemView.findViewById(R.id.time1);
            delete = itemView.findViewById(R.id.delete);
        }

    }

}
