package com.ad.thereyet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

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
    Bitmap bitmap = null;



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
                if(data.getData()==null){
                    bitmap = (Bitmap) data.getExtras().get("data");
                    uri = getImageUri(bitmap);
                    ivImage.setImageBitmap(bitmap);

                }else{
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        ivImage.setImageBitmap(bitmap);
                        uri = getImageUri(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                uri = getImageUri(bitmap);

            }
        }
    }

    public Uri getImageUri(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
//            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = this.openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;

    }

    private void writeNewUser(String username, String email, String Fname, String Lname) {
        User user = new User(username, email, Fname, Lname);
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(uid).setValue(user);
        mDatabase.child("Username").child(username).setValue("");
        

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
