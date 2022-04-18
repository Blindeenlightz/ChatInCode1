package com.jayb.chatincode;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jayb.chatincode.ViewModels.CipherAdapter;
import com.jayb.chatincode.ViewModels.DbHelper;
import com.jayb.chatincode.ViewModels.SavedCipher;

import java.util.LinkedList;

public class SavedPigLatinFragment extends Fragment {
    private RecyclerView recyclerView;
    private CipherAdapter cipherAdapter;
    private LinkedList<SavedCipher> pigLatinCiphers = new LinkedList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_pig_latin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        assert context != null;
        //Get the ciphers from the db
        pigLatinCiphers = DbHelper.getSavedMessages("PigLatin");
        //Create the recycler view
        recyclerView = view.findViewById(R.id.recyclerViewPig);
        //Give the RecyclerView a default layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //Add lines between items
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        //Insert the ciphers
        cipherAdapter = new CipherAdapter(context, pigLatinCiphers);
        //Set the adapter
        recyclerView.setAdapter(cipherAdapter);
    }
}