package com.jayb.chatincode;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jayb.chatincode.ViewModels.DbHelper;

import java.util.Objects;

public class CaesarShift extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "CAESAR_SHIFT";
    private final String INPUT_KEY = "INPUT_KEY";
    private final String OUTPUT_KEY = "OUTPUT_KEY";
    private final String SHIFT_KEY = "SHIFT_KEY";
    private final String ENCRYPT_KEY = "ENCRYPT_KEY";
    private EditText inputTxtBox;
    private TextView outputTxtBox;
    private Spinner caesSpinner;
    private boolean encrypt;
    private String output = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caesar_shift);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.caesarBtn);

        //Insert values from string resource in to spinner
        caesSpinner = findViewById(R.id.caesar_shift_spinner);
        ArrayAdapter<CharSequence> arrAdapt = ArrayAdapter.createFromResource(this, R.array.caesar_shift_array, android.R.layout.simple_spinner_item);
        arrAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caesSpinner.setAdapter(arrAdapt);

        encrypt = getIntent().getExtras().getBoolean(ENCRYPT_KEY);
        inputTxtBox = findViewById(R.id.inputTxtBox);
        outputTxtBox = findViewById(R.id.outputTxtBox);
        TextView inputLbl = findViewById(R.id.inputLbl);
        TextView outputLbl = findViewById(R.id.outputLbl);

        if (savedInstanceState != null) {
            caesSpinner.setSelection(savedInstanceState.getInt(SHIFT_KEY));
            inputTxtBox.setText(savedInstanceState.getString(INPUT_KEY));
            output = savedInstanceState.getString(OUTPUT_KEY);
            outputTxtBox.setText(output);
            encrypt = savedInstanceState.getBoolean(ENCRYPT_KEY);
        }

        Button encryptDecryptBtn = findViewById(R.id.encrypt_decryptBtn);
        if (!encrypt) {
            encryptDecryptBtn.setText(R.string.decryptRadBtn);
            inputLbl.setText(R.string.yourCiphLbl);
            outputLbl.setText(R.string.yourMessageLbl);
            inputTxtBox.setHint(R.string.enterCiphHint);
        }
        Button saveBtn = findViewById(R.id.saveBtn);
        Button copyBtn = findViewById(R.id.copyBtn);
        Button shareBtn = findViewById(R.id.shareBtn);
        Button resetBtn = findViewById(R.id.deleteBtn);

        encryptDecryptBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        copyBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SHIFT_KEY, caesSpinner.getSelectedItemPosition());
        outState.putString(INPUT_KEY, inputTxtBox.getText().toString());
        outState.putString(OUTPUT_KEY, output);
        outState.putBoolean(ENCRYPT_KEY, encrypt);

        super.onSaveInstanceState(outState);
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

        if (id == R.id.encrypt_decryptBtn) {
            String input = inputTxtBox.getText().toString();
            int shiftVal = caesSpinner.getSelectedItemPosition() + 1;
            if (encrypt) {
                output = caesarCipherEncrypt(input, shiftVal);
            } else {
                output = caesarCipherDecrypt(input, shiftVal);
            }
            if (output != null) {
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                outputTxtBox.setText(output);
            }
        } else if (id == R.id.saveBtn) {
            if (!output.isEmpty()) {
                //Get the name to save it under
                final String[] savedName = {""};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Save As:");
                EditText inputBox = new EditText(this);
                inputBox.setInputType(InputType.TYPE_CLASS_TEXT);
                inputBox.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });
                inputBox.setHint("(Max. 12 characters)");
                builder.setView(inputBox);
                builder.setPositiveButton("Save", (dialog, which) -> {
                    savedName[0] = inputBox.getText().toString();
                    if (savedName[0].isEmpty()) {
                        Toast.makeText(CaesarShift.this, "Name can't be blank", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DbHelper.addCipherToDb(savedName[0], output, "Caesar Shift", CaesarShift.this);

                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();


            } else {
                Toast.makeText(this, "No output to save", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: Attempt to save non-existent output");
            }
        } else if (id == R.id.copyBtn) {
            //Check to make sure there is something to copy
            if (!output.isEmpty()) {
                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //Create clip of output text box contents
                ClipData clip = ClipData.newPlainText("output", output);
                //Set it to the clipboard
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No output to copy", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: Attempt to copy non-existent output");
            }
        } else if (id == R.id.shareBtn) {
            //Check to make sure there is something to share
            if (!output.isEmpty()) {
                //Create the intent that will hold the output
                Intent senderIntent = new Intent();
                senderIntent.setAction(Intent.ACTION_SEND);
                senderIntent.putExtra(Intent.EXTRA_TEXT, output);
                senderIntent.setType("text/plain");
                //Attach the previous intent to an intent that opens the Sharesheet
                Intent shareIntent = Intent.createChooser(senderIntent, null);
                //Open Sharesheet options
                startActivity(shareIntent);
            } else {
                Toast.makeText(this, "No output to share", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: Attempt to share non-existent output");
            }
        } else if (id == R.id.deleteBtn) {
            inputTxtBox.setText("");
            outputTxtBox.setText("");
            output = "";
        }
        //Edge case error
        else {
            Log.e(TAG, "Error: id in onClick not recognized. ID: " + id);
        }
    }


    private String caesarCipherEncrypt(String plainMessage, int shiftVal) {
        if (plainMessage.isEmpty()) {
            Toast.makeText(this, "Please enter a message.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error: Plaintext message has no content.");
            return null;
        } else if (shiftVal < 0 || shiftVal > 25) {
            Toast.makeText(this, "Shift value must be between 1-25.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error: Shift value out of range. shiftVal: " + shiftVal);
            return null;
        }
        //Keep formatting consistent
        char[] plainMessArr = plainMessage.toUpperCase().trim().toCharArray();

        //Create cipher
        int counter = 0;
        for (char c : plainMessArr) {
            //Keep all punctuation, symbols, and numbers the same
            if ((int) c > 64 && (int) c < 91) {
                //Shift the letter to the desired amount
                plainMessArr[counter] += shiftVal;
                //Handle overflow
                if (plainMessArr[counter] >= 91) {
                    plainMessArr[counter] -= 26;
                }
            }
            counter++;
        }
        return new String(plainMessArr);
    }

    private String caesarCipherDecrypt(String cipher, int shiftVal) {
        if (cipher.isEmpty()) {
            Log.e(TAG, "Error: Cipher message has no content.");
            return null;
        } else if (shiftVal < 0 || shiftVal > 25) {
            Log.e(TAG, "Error: Shift value out of range. shiftVal: " + shiftVal);
            return null;
        }

        //Handle manual input by user (Make sure the formatting is consistent)
        char[] cipherArr = cipher.toUpperCase().trim().toCharArray();

        //Decrypt cipher
        int counter = 0;
        for (char c : cipherArr) {
            //Keep all punctuation, symbols, and numbers the same
            if ((int) c > 64 && (int) c < 91) {
                //Shift the letter to the desired amount
                cipherArr[counter] -= shiftVal;
                //Handle underflow
                if (cipherArr[counter] <= 64) {
                    cipherArr[counter] += 26;
                }
            }
            counter++;
        }
        return new String(cipherArr);
    }
}

//Link for more info on cipher (Child version) - https://simple.wikipedia.org/wiki/Caesar_cipher
//Link for more info on cipher (Adult version) - https://en.wikipedia.org/wiki/Caesar_cipher