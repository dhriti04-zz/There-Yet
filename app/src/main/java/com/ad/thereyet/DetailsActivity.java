package com.ad.thereyet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ad.thereyet.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DetailsActivity extends AppCompatActivity {

    EditText etUserName;
    EditText etFname;
    EditText etLname;
    Button btnImage;
    Button btnSubmit;
    ImageView ivImage;
    StorageReference filePath;
    Uri uri;
    private static final int CAPTURE_REQUEST_CODE = 1;
    private StorageReference stoRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        stoRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        etUserName = findViewById(R.id.etUsername);
        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLname);
        btnImage = findViewById(R.id.BtnProfImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivImage = findViewById(R.id.ivDP);


        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        CAPTURE_REQUEST_CODE);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUserName.getText().toString();
                String FirstName = etFname.getText().toString();
                String LastName = etLname.getText().toString();
                String emails = mAuth.getCurrentUser().getEmail();
                writeNewUser(username, emails, FirstName, LastName);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

//                uri = data.getData();
                Bitmap bitmap;
                if(data.getData()==null){
                    bitmap = (Bitmap)data.getExtras().get("data");
                    ivImage.setImageBitmap(bitmap);

                    uri = getImageUri(this,bitmap);
                }else{
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        ivImage.setImageBitmap(bitmap);
                        uri = getImageUri(this,bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void writeNewUser(String username, String email, String Fname, String Lname) {
        User user = new User(username, email, Fname, Lname);
        mDatabase.child("users").child(username).setValue(user);

        filePath = stoRef.child(username).child("Photo");
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(DetailsActivity.this, "Uploading file...", Toast.LENGTH_LONG);
                startActivity(new Intent(DetailsActivity.this, MainActivity.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Failed to upload...", Toast.LENGTH_LONG);

            }
        });

    }

}
