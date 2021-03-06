package com.example.egarden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.egarden.databinding.ActivityMainBinding;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ActivityMainBinding binding;
    Home homeFragment;
    Toolbar toolbarr;
    TextView toolbarTitle;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private GoogleSignInClient mGoogleSignInClient;
    Snackbar snackbar;

    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreference = SharedPreference.getPreferences(MainActivity.this);


        toolbarr = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarr);

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.app_name);


        if (sharedPreference.getPump().equals("off")) {
            binding.waterPump.setChecked(false);
        } else if (sharedPreference.getPump().equals("on")) {
            binding.waterPump.setChecked(true);
        }


        if (sharedPreference.getAutoWatering().equals("off")) {
            binding.autoWatering.setChecked(false);
        } else if (sharedPreference.getAutoWatering().equals("on")) {
            binding.autoWatering.setChecked(true);
        }

        setSupportActionBar(binding.include.toolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                binding.drawerLayout, binding.include.toolbar, R.string.open, R.string.close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getColor(R.color.white));


        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.help.setOnClickListener((View.OnClickListener) this);
        initFragmentHome();

        Glide.with(MainActivity.this)
                .load(sharedPreference.getImage())
                .into(binding.profileImage);

        binding.name.setText(sharedPreference.getName());
        binding.email.setText(sharedPreference.getEmail());

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreference.setName("none");
                sharedPreference.setEmail("none");
                sharedPreference.setImage("none");
                signOut();
                revokeAccess();
                deleteAccessToken();
                Toast.makeText(MainActivity.this, "Successfully Logout", Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(MainActivity.this, Splash.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();


            }
        });


        /*binding.autoWatering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    binding.waterPump.setClickable(false);
                    binding.waterPump.setChecked(false);
                    Tools.snackInfo(MainActivity.this, "Water pump is automatically On/OFF");

                } else {
                    binding.waterPump.setClickable(true);
                }
            }
        });*/

        binding.autoWatering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {


                    Tools.snackInfo(MainActivity.this, "Water pump is automatically On/OFF");
                    binding.waterPump.setChecked(false);
                    sharedPreference.setAutoWatering("on");

                } else {

                    sharedPreference.setAutoWatering("off");
                }
            }
        });


        binding.waterPump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (binding.autoWatering.isChecked()) {

                    binding.waterPump.setChecked(false);
                    Tools.snackErrInfo(MainActivity.this, "Turn off Automatic watering !", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                } else {

                    if (isChecked) {
                        sharedPreference.setPump("on");
                        snackbar = Snackbar.make(
                                findViewById(android.R.id.content),
                                "Water pump is running !",
                                Snackbar.LENGTH_INDEFINITE)
                                .setActionTextColor(Color.WHITE);

                        snackbar.getView().setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                        snackbar.show();
                    } else {
                        sharedPreference.setPump("off");
                        snackbar.dismiss();
                    }

                }

            }
        });

    }


    private void initFragmentHome() {
        homeFragment = new Home();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(binding.include.contentMain.fragmentContainer.getId(), homeFragment, "HomeFragment").commit();

    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.help:


        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


    //google logout
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


    //facebook logout
    private void deleteAccessToken() {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null) {
                    //User logged out
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }
}