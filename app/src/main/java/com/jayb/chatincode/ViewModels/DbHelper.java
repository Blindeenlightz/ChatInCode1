package com.jayb.chatincode.ViewModels;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jayb.chatincode.MainActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class DbHelper extends MainActivity {
    private static final String TAG = "DB_HELPER";


    public static void addCipherToDb(String saveName, String cipher, String encryptionType, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        //First make sure the user exists in db
//        userEntry.put("saved", saveName);
//        if(!addToDb("users", userEntry, userId)) { return false;}

        //Now save the cipher
        Map<String, Object> cipherEntry = new HashMap<>();
        Date now = new Date();
        Timestamp timeNow = new Timestamp(now);
        cipherEntry.put("SaveName", saveName);
        cipherEntry.put("Cipher", cipher);
        cipherEntry.put("DateCreated", timeNow);
        cipherEntry.put("EncryptMethod", encryptionType);
        cipherEntry.put("User", user.getUid());
        addToDbWithSub(userId, saveName, cipherEntry, context);

    }

    private static void addToDbWithSub(String firstLevelDoc, String secondLevelDoc, Map<String, Object> entry, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Attempt to add document to firestore
        db.collection("users")
                .document(firstLevelDoc)
                .collection("saved")
                .document(secondLevelDoc)
                .set(entry)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    Toast.makeText(context, "Saved!!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(context, "Error saving. Try again.", Toast.LENGTH_SHORT).show();
                });
    }


    public static void logOutCurrUser() {
        FirebaseAuth.getInstance().signOut();
    }


    public static LinkedList<SavedCipher> getSavedMessagesUpdateAdapter(String encryptMethod, CipherAdapter adapter) {
        LinkedList<SavedCipher> returnedItems = new LinkedList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        String firstLevelColl = "users";
        String secondLevelColl = "saved";

        db.collection(firstLevelColl)
                .document(userId)
                .collection(secondLevelColl)
                .whereEqualTo("EncryptMethod", encryptMethod)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                            returnedItems.add(doc.toObject(SavedCipher.class));
                        }
                        if (!returnedItems.isEmpty()) {
                            adapter.setCiphers(returnedItems);
                            for (SavedCipher item : returnedItems) {
                                Log.d(TAG, "FromFirestore: " + item.getSaveName());
                            }
                        } else {
                            Log.d(TAG, "No saved ciphers found in db... ");
                        }
                    } else {
                        Log.e(TAG, "Error retrieving saved ciphers. Exception: ", task.getException());
                    }
                });
        return returnedItems;
    }

    public static void deleteSavedMessage(String savedName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        String firstLevelColl = "users";
        String secondLevelColl = "saved";

        db.collection(firstLevelColl)
                .document(userId)
                .collection(secondLevelColl)
                .document(savedName)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted! savedName: " + savedName))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting document: " + e + " SavedName: " + savedName));
    }
}
