package com.ad.thereyet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.ad.thereyet.Fragments.MainFragment;
import com.ad.thereyet.Fragments.MapFragment;
import com.ad.thereyet.Fragments.ProfileFragment;
import com.ad.thereyet.Fragments.SettingsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private DatabaseReference mDatabase;
    TextView tvNav;
    private FirebaseAuth mAuth;

    MainFragment mainFragment;
    MapFragment mapFragment;
    ProfileFragment profileFragment;
    SettingsFragment settingsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mainFragment = new MainFragment();
        mapFragment = new MapFragment();
        settingsFragment = new SettingsFragment();
        profileFragment = new ProfileFragment();

        Fragment fragment = mainFragment;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.FragmentPlace, fragment);

        ft.commit();


        tvNav = (TextView) findViewById(R.id.navHeader);
        String uid = mAuth.getCurrentUser().getUid();
//        tvNav.setText("Hi " + mDatabase.child("Users").child(uid));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openString, R.string.closeString);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // method invoked only when the actionBar is not null.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)){
            System.out.println(item.getItemId());

//            MenuItem items = (MenuItem) item.getSubMenu();
//
//            int id = items.getItemId();
//
//            //noinspection SimplifiableIfStatement
//            if (id == R.id.nav_profile) {
//                System.out.println("prooooofile??");
//            } else if (id == R.id.nav_settings) {
//                System.out.println("seeeetting??");
//            } else if (id == R.id.nav_map) {
//                System.out.println("maaappp??");
//            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
