package com.example.apple.firebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddPostActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 1;
    private ImageView mImageView;
    private EditText mTitle, mCaption;
    private DatabaseReference mDatabase;
    private User user;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mImageView = (ImageView)findViewById(R.id.add_img);
        mTitle = (EditText)findViewById(R.id.add_title);
        mCaption = (EditText)findViewById(R.id.add_caption);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser userAuth = FirebaseAuth.getInstance().getCurrentUser();
        user = new User(userAuth.getUid());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    public void getPhoto(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                mImageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public void uploadImage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        Long tsLong = System.currentTimeMillis()/1000;
        // imagesRef now points to "images"
        StorageReference imagesRef = storageRef.child(tsLong.toString()+".jpg");

        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache();
        Bitmap bitmap = mImageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(AddPostActivity.this,"Upload image fail",Toast.LENGTH_SHORT).show();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                path = downloadUrl.toString();

                Post post = new Post(user.getUid(),mTitle.getText().toString(), mCaption.getText().toString(), path);
                mDatabase.child("posts").push().setValue(post);
                Toast.makeText(AddPostActivity.this, "Uploaded",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
