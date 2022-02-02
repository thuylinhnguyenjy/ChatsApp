package com.example.chatsapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.chatsapp.R;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.chatsapp.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneNumberActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(PhoneNumberActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.edtxtPhoneNumber.requestFocus();

        //click event
        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneNumberActivity.this, OTPCheckActivity.class);
                intent.putExtra("phoneNumber", binding.edtxtPhoneNumber.getText().toString());
                startActivity(intent);
            }
        });

        //setSupportActionBar(binding.toolbar);

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_phone_number);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_phone_number);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}