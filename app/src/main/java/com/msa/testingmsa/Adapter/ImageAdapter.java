package com.msa.testingmsa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.msa.testingmsa.Model.Chat;

import com.msa.testingmsa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;
    FirebaseUser fuser;

    public ImageAdapter(Context mContext , List<Chat> mChat){
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.img_rt , parent, false);
            return new ImageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.img_left , parent, false);
            return new ImageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        Glide.with(mContext).load(chat.getMessage()).into(holder.image);

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
        public ImageView image;
        public ImageView profile_image;
        public ImageView tick;
        public TextView txt_seen;
        public ImageView img_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_msg);
            profile_image = itemView.findViewById(R.id.profile_image);
            tick = itemView.findViewById(R.id.tick);
            txt_seen = itemView.findViewById(R.id.text_seen);
            img_seen = itemView.findViewById(R.id.seen_img);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
