package com.onlinemachenic.activities.user;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.microprogramers.library.PermissionAsker;
import com.onlinemachenic.R;
import com.onlinemachenic.activities.LoginActivity;
import com.onlinemachenic.helpers.StaticConfig;
import com.onlinemachenic.models.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.onlinemachenic.helpers.StaticConfig.myUID;

public class UserSignUpActivity extends AppCompatActivity implements Listener
{

    private EditText edt_full_name, edt_phone, edt_email, edt_password;
    private User user;
    private EasyWayLocation easyWayLocation;
    private ImageView user_profile;
    private byte[] imageData = null;
    private ProgressDialog progressDialog;
    private boolean imageInserted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        edt_full_name = findViewById(R.id.edt_full_name);
        edt_phone = findViewById(R.id.edt_phone);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        user_profile = findViewById(R.id.user_profile);
        Button btn_signup = findViewById(R.id.btn_signup);
        user = new User();



        easyWayLocation = new EasyWayLocation(this, false, false, this);
        String notificationToken = FirebaseInstanceId.getInstance().getToken();

        if (notificationToken != null && !notificationToken.isEmpty()) {
            user.setNotification_token(notificationToken);
        }

        imageInserted = false;
        user_profile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (PermissionAsker.hasPermissions(UserSignUpActivity.this, permissions))
                {
                    selectImage();
                }
                else
                {
                    PermissionAsker.getUserPermissions(UserSignUpActivity.this, permissions);
                }
            }
        });

    }


    public void signUp(View view)
    {

        String name = edt_full_name.getText().toString(), phone = edt_phone.getText().toString(), email = edt_email.getText().toString(), password = edt_password.getText().toString();

        if (imageInserted && !name.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !password.isEmpty())
        {
            user.setName(name);
            user.setPhone_number(phone);


            progressDialog = new ProgressDialog(UserSignUpActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Creating Account");
            progressDialog.show();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(edt_email.getText().toString(), edt_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {

                    if (task.isSuccessful())
                    {
                        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        user.setUser_id(myUID);
                        user.setEmail(email);
                        user.setPassword(password);

                        uploadImageAndData();
                    }
                    else
                    {
                        progressDialog.cancel();
                        Toast.makeText(UserSignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void selectImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @RequiresApi (api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            if (selectedImage != null)
            {
                CropImage.activity(selectedImage).setGuidelines(CropImageView.Guidelines.ON).start(UserSignUpActivity.this);
            }

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();


                Bitmap bitmap = null;
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                imageData = baos.toByteArray();
                imageInserted = true;

                user_profile.setImageBitmap(bitmap);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Toast.makeText(UserSignUpActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadImageAndData()
    {
        progressDialog.setMessage("Setting Data");
        progressDialog.setCancelable(false);

        FirebaseStorage.getInstance().getReference().child("users_profile_pics").child(myUID).putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {

                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {


                        user.setPic_url(task.getResult().toString());

                        StaticConfig.usersRef.child(myUID).child("info").setValue(user).addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                progressDialog.cancel();
                                Toast.makeText(UserSignUpActivity.this, "Account created Successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(UserSignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        });


                    }

                });
            }
        });
    }


    @Override
    public void locationOn()
    {

    }

    @Override
    public void currentLocation(Location location)
    {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        user.setLatitude(latitude);
        user.setLongitude(longitude);

        Log.d("TAG", "onLocationChanged: lat: " + latitude + ", lon: " + longitude);
    }

    @Override
    public void locationCancelled()
    {

    }


    @Override
    protected void onResume()
    {
        easyWayLocation.startLocation();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        easyWayLocation.endUpdates();
        super.onPause();
    }


}