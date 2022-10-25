package com.example.clonechat.AdapterStuff;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Freezable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clonechat.Models.MessageModel;
import com.example.clonechat.R;
import com.example.clonechat.Utils.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter {
    ArrayList<MessageModel>messageModels;
    Context context;
    String receiverUid;

    private final String PATTERN = "dd-M-yyyy hh:mm:ss";

    final int SENDER_VIEW_TYPE = 1;
    final int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel>messageModels,Context context,String receiverUid){
        this.context = context;
        this.messageModels = messageModels;
        this.receiverUid = receiverUid;

        Log.d("tag-mess", "ChatAdapter: "+messageModels);
    }

    public ChatAdapter(ArrayList<MessageModel>messageModels,Context context){
        this.context = context;
        this.messageModels = messageModels;

        Log.d("tag-mess", "ChatAdapter: "+messageModels);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==SENDER_VIEW_TYPE){
           View Sender_view = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewModel(Sender_view);
        }else{
           View  Receiver_view = LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return new ReceiverViewModel(Receiver_view);
        }

    }


    @Override
    public int getItemViewType(int position) {
        //here writing the code to identify who is sender and receiver.
        Log.d("sender-tag", "invoking the getItemViewtype --->  "+FirebaseAuth.getInstance().getUid());
        Log.d("receiver-tag", "invoking the chatadapter constructor --->  "+messageModels.get(position).getUnique_Uid());
        Log.d("model-list", "getItemViewType: "+messageModels.size());

        if (messageModels.get(position).getUnique_Uid().equals(FirebaseAuth.getInstance().getUid())) {
            Log.d("tag", "getItemViewType: "+FirebaseAuth.getInstance().getUid());
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //setting the text to the sender and receiver.


        MessageModel messageModel = messageModels.get(position);
               // messageModels.get(position);
        Log.d("model", "onBindViewHolder: "+messageModel.getSms_message());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                //setting the alert dialog box.

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you surer you want to delete ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + receiverUid;
                                db.getReference().child("chats")
                                        .child(senderRoom)
                                        .child(messageModel.getMessage_id())
                                        .setValue(null);

                                Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

                return true;
            }
        });


        if(holder.getClass() == SenderViewModel.class){
            try{
                ((SenderViewModel)holder).senderText.setText(messageModel.getSms_message());
                SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN);
                Date current_date = Utility.MillisToDateFormat(messageModels.get(position).getTime());
                String curr_Time =  dateFormat.format(current_date);
                ((SenderViewModel) holder).sender_time.setText(curr_Time);
                Log.d("model-tag", "onBindViewHolder: "+((SenderViewModel) holder).senderText.getText().toString());
            }catch (Exception e){
                Log.e("model", "onBindViewHolder-exception: "+e);
            }
        }else{
            try{
                ((ReceiverViewModel)holder).receiver_tv.setText(messageModels.get(position).getSms_message());
                SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN);
                Date current_date = Utility.MillisToDateFormat(messageModels.get(position).getTime());
                String curr_Time =  dateFormat.format(current_date);
                ((ReceiverViewModel) holder).receiver_time.setText(curr_Time);
                Log.d("model-tag", "onBindViewHolder: "+((SenderViewModel) holder).senderText.getText().toString());
            }catch (Exception e){
                Log.e("model", "onBindViewHolder-exception: "+e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }


    public class ReceiverViewModel extends RecyclerView.ViewHolder{
        TextView receiver_tv;
        TextView receiver_time;
                public ReceiverViewModel(@NonNull View itemView) {
                    super(itemView);
                    receiver_tv = itemView.findViewById(R.id.receiver_tv);
                    receiver_time = itemView.findViewById(R.id.receiver_time);
                }
    }

    public class SenderViewModel extends RecyclerView.ViewHolder{
        TextView senderText,sender_time;
        public SenderViewModel(@NonNull View itemView) {
            super(itemView);
            Log.d("finding-Id", "invoked SenderViewModel : "+itemView.findViewById(R.id.senderText));
            senderText = itemView.findViewById(R.id.senderText);
            sender_time = itemView.findViewById(R.id.sender_time);
        }
    }

}
