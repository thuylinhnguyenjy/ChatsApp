package com.example.chatsapp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.chatsapp.R;
import com.example.chatsapp.adapter.StoryAdapter;
import com.example.chatsapp.adapter.UserAdapter;
import com.example.chatsapp.model.Story;
import com.example.chatsapp.model.User;
import com.example.chatsapp.model.UserStory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    FirebaseDatabase database;
    User user;
    ArrayList<User> userList;
    UserAdapter adapter;
    StoryAdapter storyAdapter;
    ArrayList<Story> storiesList;
    ArrayList<UserStory> userStoriesList;
    ProgressDialog storyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storyDialog = new ProgressDialog(MainActivity.this);
        storyDialog.setMessage("Uploading...");
        storyDialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();

        userList = new ArrayList<>();
        adapter = new UserAdapter(this, userList);
        binding.rvRowConversation.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRowConversation.setAdapter(adapter);

        userStoriesList = new ArrayList<>();
        storyAdapter = new StoryAdapter(this, userStoriesList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvStory.setLayoutManager(layoutManager);
        binding.rvStory.setAdapter(storyAdapter);

        database.getReference().child("User").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ss : snapshot.getChildren()){
                    User user = ss.getValue(User.class);
                    userList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("Story").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userStoriesList.clear();
                    for (DataSnapshot ss : snapshot.getChildren()){
                        UserStory userStory = new UserStory();
                        userStory.setName(ss.child("name").getValue(String.class));
                        userStory.setProfileName(ss.child("profileImage").getValue(String.class));
                        userStory.setLastUpdated(ss.child("lastUpdated").getValue(Long.class));

                        ArrayList<Story> arr = new ArrayList<>();

                        for (DataSnapshot ds : ss.child("status").getChildren()){
                            Story story = ds.getValue(Story.class);
                            arr.add(story);
                        }
                        userStory.setUserStoryList(arr);
                        userStoriesList.add(userStory);
                    }
                    storyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("User").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.story:
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, 75);
                        break;
                }
                return false;
            }
        });

        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, PhoneNumberActivity.class);
                        startActivity(intent);
                        finishAffinity();
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (data.getData() != null){
                storyDialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("stories").child(date.getTime()+"");
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String randomKey = database.getReference().push().getKey();
                                    UserStory story = new UserStory();
                                    story.setName(user.getName());
                                    story.setProfileName(user.getProfileImage());
                                    story.setLastUpdated(date.getTime());
                                    storyDialog.dismiss();

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", story.getName());
                                    obj.put("profileImage", story.getProfileName());
                                    obj.put("lastUpdated", story.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Story st = new Story(imageUrl, story.getLastUpdated());

                                    database.getReference()
                                            .child("Story")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference()
                                            .child("Story")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("status")
                                            .child(randomKey)
                                            .setValue(st);

                                }
                            });
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}