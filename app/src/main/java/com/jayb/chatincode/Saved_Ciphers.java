package com.jayb.chatincode;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jayb.chatincode.ViewModels.CipherAdapter;
import com.jayb.chatincode.ViewModels.DbHelper;
import com.jayb.chatincode.ViewModels.PagerAdapter;
import com.jayb.chatincode.ViewModels.SavedCipher;

import java.util.LinkedList;
import java.util.Objects;

public class Saved_Ciphers extends AppCompatActivity {
    private final String TAG = "SAVED_CIPHERS";
    private ViewPager2 viewPager2;
    TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_ciphers);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.savedCipherTitle);


        //TAB LAYOUT AND VIEWPAGER2
        tabLayout = findViewById(R.id.tabLayout);
        // Set the text for each tab.
        tabLayout.addTab(tabLayout.newTab().setText(R.string.caesarShiftTitle));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.pigLatinTitle));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.subCipherTitle));
        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager2 = findViewById(R.id.viewPager2);
        //Create an adapter for the viewpager2
        final PagerAdapter adapter = new PagerAdapter(this, tabLayout.getTabCount());
        viewPager2.setAdapter(adapter);

        //Handle user events for viewPager2
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewPager2.setCurrentItem(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        //Set the titles of each tab
        new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.caesarShiftTitle);
                    break;
                case 1:
                    tab.setText(R.string.pigLatinTitle);
                    break;
                case 2:
                    tab.setText(R.string.subCipherTitle);
                    break;
            }
        })).attach();
    }
}