package com.example.clonechat.AdapterStuff;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.clonechat.ChatDetailActivity;
import com.example.clonechat.Models.User;
import com.example.clonechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class UserRecylerView extends Adapter<UserRecylerView.ViewHolder> {
    private final ArrayList<User>list;
    private final Context context;

    public UserRecylerView(ArrayList<User>list,Context context){
        Log.d("tag", "UserRecylerView: invoked");
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public UserRecylerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = View.inflate(parent.getContext(),R.layout.todo_chat_row,null);
        Log.d("tag", "UserRecylerView: invoked-viewholder");
        View view = LayoutInflater.from(context).inflate(R.layout.todo_chat_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecylerView.ViewHolder holder, int position) {
        //binding the data to the views.
        User user = list.get(position);

        if(user.getFemaleBoolean()){
            Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.woman_img)
                    .into(holder.profileImg);
        }else{
            Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.man_img)
                    .into(holder.profileImg);
        }

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(FirebaseAuth.getInstance().getUid() + user.getId())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                String template = snapshot1.child("sms_message").getValue(String.class);
                                if(Objects.requireNonNull(template).length()>=15){
                                    holder.lastMessage.setText(template.substring(0,30)+"....");
                                }
                                else{
                                    holder.lastMessage.setText(template.trim());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //fetching the usernames
        holder.userNameTv.setText(user.getName().trim());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ChatDetailActivity.class);
                intent.putExtra("male_gender",user.getMaleBoolean());
                intent.putExtra("female_gender",user.getFemaleBoolean());
                intent.putExtra("userName",user.getName());
                intent.putExtra("chatProfile",user.getProfilePic());
                intent.putExtra("userChatId",user.getId());
                Log.d("tag", "onClick from the user recycler view : "+user.getName());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
//        int n;
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImg;
        private TextView userNameTv;
        private TextView lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image);
            userNameTv = itemView.findViewById(R.id.usernameTv);
            lastMessage = itemView.findViewById(R.id.last_messageTv);
        }
    }
}
