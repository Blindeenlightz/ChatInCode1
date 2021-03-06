package com.jayb.chatincode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.jayb.chatincode.ViewModels.DbHelper;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {
    Button pigLatBtn, caesShiftBtn, subCiphBtn, logOutBtn, savedBtn, instructBtn;
    RadioButton encryptRad, decryptRad;
    boolean encryption = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        pigLatBtn = findViewById(R.id.pigLatinBtn);
        caesShiftBtn = findViewById(R.id.caesShiftBtn);
        subCiphBtn = findViewById(R.id.subCiphBtn);
        logOutBtn = findViewById(R.id.logoutBtn);
        savedBtn = findViewById(R.id.savedBtn);
        encryptRad = findViewById(R.id.encryptRadBtn);
        decryptRad = findViewById(R.id.decryptRadBtn);
        instructBtn = findViewById(R.id.howItWorksBtn);

        pigLatBtn.setOnClickListener(this);
        caesShiftBtn.setOnClickListener(this);
        subCiphBtn.setOnClickListener(this);
        logOutBtn.setOnClickListener(this);
        savedBtn.setOnClickListener(this);
        encryptRad.setOnClickListener(this);
        decryptRad.setOnClickListener(this);
        instructBtn.setOnClickListener(this);
    }

    /***
     *  Allows the keyboard to close when user touches out of useful zone.
     *
     * @param ev Motion Event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        String ENCRYPT_KEY = "ENCRYPT_KEY";
        if (id == R.id.pigLatinBtn) {
            Intent pigIntent = new Intent(HomePageActivity.this, PigLatinActivity.class);
            pigIntent.putExtra(ENCRYPT_KEY, encryption);
            startActivity(pigIntent);
        } else if (id == R.id.caesShiftBtn) {
            Intent caesIntent = new Intent(HomePageActivity.this, CaesarShift.class);
            caesIntent.putExtra(ENCRYPT_KEY, encryption);
            startActivity(caesIntent);
        } else if (id == R.id.subCiphBtn) {
            Intent subIntent = new Intent(HomePageActivity.this, SubstitutionCipher.class);
            subIntent.putExtra(ENCRYPT_KEY, encryption);
            startActivity(subIntent);
        } else if (id == R.id.logoutBtn) {
            DbHelper.logOutCurrUser();
            Intent i = new Intent(HomePageActivity.this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.savedBtn) {
            Intent savedIntent = new Intent(HomePageActivity.this, Saved_Ciphers.class);
            startActivity(savedIntent);
        } else if (id == R.id.encryptRadBtn) {
            encryption = true;
        } else if (id == R.id.decryptRadBtn) {
            encryption = false;
        } else if (id == R.id.howItWorksBtn) {
            Intent instructIntent = new Intent(HomePageActivity.this, Tutorial.class);
            startActivity(instructIntent);
        }
        //Edge case error
        else {
            String TAG = "HOME_PAGE";
            Log.e(TAG, "Error: id in onClick not recognized. ID: " + id);
        }
    }
}