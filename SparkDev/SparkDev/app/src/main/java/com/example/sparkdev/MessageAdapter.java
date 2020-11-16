package com.example.sparkdev;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth nAuth;
    private DatabaseReference usersRef;


    public MessageAdapter (List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;



        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
        }
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent, false);
        //parent from ViewGroup parent from generating method. -kev50

        nAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
    //public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder...
    //ia  hia line. I don't know if final is necessary or not. -kev50
        String messageSenderId = nAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType(); //Text or file. For now, just text -kev50

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("image"))
                {
                    String receiverImage = snapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image)
                            .into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (fromMessageType.equals("text"))
        {
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);
            holder.senderMessageText.setVisibility(View.INVISIBLE);

            if (fromUserID.equals(messageSenderId))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setTextColor(Color.BLACK);
                //Color is from android.graphics, but there is an option com.google.type I did
                //not check. The default color without this line of code is black, but is left
                //for customization. -kev50
                holder.senderMessageText.setText(messages.getMessage());
            }
            else
            {
              //  holder.senderMessageText.setVisibility(View.INVISIBLE);

                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(messages.getMessage());
            }
        }

        else if(fromMessageType.equals("image")){
            if(fromUserID.equals(messageSenderId)){
                    holder.messageSenderPicture.setVisibility(View.VISIBLE);
                    holder.senderMessageText.setVisibility(View.INVISIBLE);
                     holder.receiverMessageText.setVisibility(View.INVISIBLE);


                    Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);



            }
            else{

                holder.receiverProfileImage.setVisibility(View.VISIBLE);
               holder.messageReceiverPicture.setVisibility(View.VISIBLE);
               holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.receiverMessageText.setVisibility(View.INVISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);
            }
        }
        else{
            if(fromUserID.equals(messageSenderId)){

                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setBackgroundResource(R.drawable.send_files);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

            }
            else{

                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                holder.messageReceiverPicture.setBackgroundResource(R.drawable.send_files);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }

    }




    @Override
    public int getItemCount() {
        return userMessagesList.size();
        //how many messages are available -kev50
    }







}
