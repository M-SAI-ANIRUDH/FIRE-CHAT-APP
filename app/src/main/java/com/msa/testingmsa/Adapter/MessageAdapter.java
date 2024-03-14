package com.msa.testingmsa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.msa.testingmsa.Model.Chat;

import com.msa.testingmsa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public int loso = 18;
    public String httpLink;
    private Context mContext;
    private List<Chat> mChat;
    FirebaseUser fuser;

    public MessageAdapter(Context mContext , List<Chat> mChat){
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_msg_layout , parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);

        holder.rigtImj.setVisibility(View.GONE);
        holder.rigtMsg.setVisibility(View.GONE);
        holder.leftImj.setVisibility(View.GONE);
        holder.leftMsg.setVisibility(View.GONE);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (chat.getSender().equals(fuser.getUid())){
            if (chat.getType().equals("image")){
                holder.rigtImj.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(chat.getMessage()).into(holder.image);
            }else{
                holder.rigtMsg.setVisibility(View.VISIBLE);
                holder.show_message.setText(chat.getMessage());
            }
        }else {
            if (chat.getType().equals("image")){
                holder.leftImj.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(chat.getMessage()).into(holder.imagei);
                httpLink = chat.getMessage();

            }else{
                holder.leftMsg.setVisibility(View.VISIBLE);
                holder.show_messagee.setText(chat.getMessage());
            }

        }


        if (position == mChat.size()-1){
            if (chat.isIsseen().equals("true")){
                holder.img_seen.setImageResource(R.drawable.seen);
                holder.txt_seen.setText("");
            }else{
                holder.txt_seen.setText("delivered");
                holder.img_seen.setImageResource(R.drawable.ayseii);
            }
        }else{
            holder.txt_seen.setVisibility(View.GONE);
            holder.img_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image,imagei;
        public TextView show_message,show_messagee;
        public ImageView profile_image;
        public ImageView tick;
        public TextView txt_seen,txt_seene,txt_seeni,txt_seenii;
        public ImageView img_seen,img_seene,img_seeni,img_seenii;
        public RelativeLayout rigtImj , leftImj, rigtMsg, leftMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_msgo);
            imagei = itemView.findViewById(R.id.image_msgii);
            show_message = itemView.findViewById(R.id.show_message);
            show_messagee = itemView.findViewById(R.id.show_messagee);
            {
                profile_image = itemView.findViewById(R.id.profile_image);
                tick = itemView.findViewById(R.id.tick);
            }
            txt_seen = itemView.findViewById(R.id.text_seen);
            txt_seeni = itemView.findViewById(R.id.text_seeni);

            img_seen = itemView.findViewById(R.id.seen_img);
            img_seeni = itemView.findViewById(R.id.seen_imgi);

            rigtImj = itemView.findViewById(R.id.right_imageos);
            leftImj = itemView.findViewById(R.id.left_imgeos);
            rigtMsg = itemView.findViewById(R.id.right_messageos);
            leftMsg = itemView.findViewById(R.id.left_messegeos);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            if (mChat.get(position).getType() == "image"){
                loso = 88;
            }
                    return MSG_TYPE_RIGHT;
        }else{
            if (mChat.get(position).getType() == "image"){
                loso = 88;
            }
                return MSG_TYPE_LEFT;

        }
    }
}
