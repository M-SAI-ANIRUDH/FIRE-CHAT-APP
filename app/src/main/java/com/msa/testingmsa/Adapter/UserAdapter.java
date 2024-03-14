package com.msa.testingmsa.Adapter;

import com.msa.testingmsa.Model.User;

import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.msa.testingmsa.R;
import com.msa.testingmsa.MessagingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msa.testingmsa.Model.Chat;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUser;
    private Boolean isOnline;

    String theLastMessage;


    public UserAdapter(Context mContext , List<User> mUser , boolean isOnline){
        this.mContext = mContext;
        this.mUser = mUser;
        this.isOnline = isOnline;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item , parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUser.get(position);

        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")){

           holder.profile_image.setImageResource(R.drawable.prof_pic_def);
        }else{
           // Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
            Uri uri;
            uri = Uri.parse(user.getImageURL());
            Picasso.get()
                    .load(uri)
                    .into(holder.profile_image);
        }

        if (isOnline){
            lastMessage(user.getId(), holder.msg);
        }else{
            holder.msg.setVisibility(View.GONE);
        }



        if (isOnline){
            if (user.getStatus().equals("online")) {
                holder.imgOn.setVisibility(View.VISIBLE);
                holder.imgOff.setVisibility(View.GONE);
            }else{
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.VISIBLE);
        }
        }else{
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.GONE);
        }

        if(user.getSearch().equals("msa 18#")){
            holder.tick.setImageResource(R.drawable.tick);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , MessagingActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });

        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView image = new ImageView(mContext);


                image.setImageResource(R.drawable.prof_pic_def);
                if (!user.getImageURL().equals("default")) {
                    // Copying the Image To put in Dialog Box
                    {
                        BitmapDrawable drawable = (BitmapDrawable) holder.profile_image.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();


                        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        image.setImageBitmap(bmp);
                    }

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(mContext).
                                    setMessage(""+user.getUsername()+"").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).
                                    setView(image);
                    builder.create().show();
                }else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(mContext).
                                    setMessage("" + user.getUsername() + " (No DP)").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).
                                    setView(image);
                    builder.create().show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public  TextView username;
        public  ImageView profile_image;
        public  ImageView tick;
        private ImageView imgOff;
        private ImageView imgOn;
        private TextView msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username      = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            tick          = itemView.findViewById(R.id.tick);
            imgOff        = itemView.findViewById(R.id.offline);
            imgOn         = itemView.findViewById(R.id.online);
            msg           = itemView.findViewById(R.id.message);
        }
    }

    private  void lastMessage(final String userid, final TextView msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)
                    || chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }
                switch (theLastMessage){
                    case "default":
                        msg.setText("null");
                        break;

                    default:
                        msg.setText(theLastMessage);
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}