package com.example.chatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.chatsapp.databinding.ActivitySetupProfileBinding;
import com.example.chatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SetupProfileActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivitySetupProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog loadingSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        loadingSetup = new ProgressDialog(this);
        loadingSetup.setMessage("Waiting...");
        loadingSetup.setCancelable(false);

        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });

        binding.btnSetupProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.edtxtName.getText().toString();
                if (name.isEmpty()) {
                    binding.edtxtName.setError("Please type your name");
                    return;
                }

                loadingSetup.show();

                if (selectedImage != null) {
                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        String uid = auth.getUid();
                                        String phoneNumber = auth.getCurrentUser().getPhoneNumber();
                                        String name = binding.edtxtName.getText().toString();

                                        User user = new User(uid, name, phoneNumber, imageUrl);

                                        database.getReference()
                                                .child("User")
                                                .child(uid)
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                        loadingSetup.dismiss();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    String uid = auth.getUid();
                    String phoneNumber = auth.getCurrentUser().getPhoneNumber();

                    User user = new User(uid, name, phoneNumber, "No image");

                    database.getReference()
                            .child("User")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                    loadingSetup.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (data.getData() != null){
                binding.avatar.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }
}