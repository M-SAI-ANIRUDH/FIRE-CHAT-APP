package com.msa.testingmsa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import com.msa.testingmsa.Model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextView username;
    private CircleImageView profilePicture;
    private String usernameString;
    private Uri GlobalUriExChange;
    private ProgressDialog pd;

    FirebaseUser fuser;
    DatabaseReference reference;

    CardView EditUsernameCard;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    RelativeLayout relativeLayout;
    private Task task;
    private StorageReference UserProfileImagesReferance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pd = new ProgressDialog(this);
        pd.setCancelable(true);
        pd.setMessage("Uploading...");

        username = findViewById(R.id.username);
        profilePicture = findViewById(R.id.profile_image);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        UserProfileImagesReferance = FirebaseStorage.getInstance().getReference().child("Profile Images");

        relativeLayout = findViewById(R.id.profileLayout);
        EditUsernameCard = findViewById(R.id.edit_username);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                usernameString = user.getUsername();

                if(user.getImageURL().equals("default")){
                    profilePicture.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        EditUsernameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
                    alertDialog.setTitle("New Username");
                    alertDialog.setMessage("Set Your New Username");

                    final EditText input = new EditText(ProfileActivity.this);
                    input.setSingleLine();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);

                    alertDialog.setPositiveButton("SAVE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String string = input.getText().toString().trim();
                                    if (!input.equals("")){
                                        if (string.length()<=18 && !string.equals("")) {
                                            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("Username", string);
                                            reference.updateChildren(map);
                                            Snackbar.make(relativeLayout, "Username Changed", BaseTransientBottomBar.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Snackbar.make(relativeLayout,"Username is too Long Or Empty", BaseTransientBottomBar.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Snackbar.make(relativeLayout,"No Username Inputted", BaseTransientBottomBar.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    alertDialog.setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                }

                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("Username", usernameString);
                reference.updateChildren(map);
            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent , IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getApplication().getContentResolver();
        return MimeTypeMap.getFileExtensionFromUrl(contentResolver.getType(uri));
    }

    private void uploadImage(){
        pd.show();

        if(GlobalUriExChange != null){
            final StorageReference fileReference = UserProfileImagesReferance.child(usernameString + ".jpg");

            uploadTask = fileReference.putFile(GlobalUriExChange);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot , Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        pd.dismiss();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);
                        Snackbar.make(relativeLayout,"Profile Picture Uploaded", BaseTransientBottomBar.LENGTH_SHORT).show();
                        pd.dismiss();
                    }else {
                        pd.dismiss();
                        Snackbar.make(relativeLayout,"Upload Failed", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else{
            Snackbar.make(relativeLayout,"No File Selected", BaseTransientBottomBar.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity

            CropImage.activity(imageUri)
                    .setAspectRatio(3, 4)
                    .setMinCropResultSize(300,300)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            GlobalUriExChange = result.getUri();

            /*
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                GlobalUriExChange = resultUri;

                StorageReference filePath = UserProfileImagesReferance.child(usernameString + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            Snackbar.make(profilePicture,"profile picture Uploaded", BaseTransientBottomBar.LENGTH_SHORT);
                            Toast.makeText(ProfileActivity.this, "Done!!!", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }else{
                            Snackbar.make(profilePicture,"Upload Failed", BaseTransientBottomBar.LENGTH_SHORT);
                            pd.dismiss();
                        }
                    }
                });
            }
            */
        }

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            }else{
                uploadImage();
            }


    }

}