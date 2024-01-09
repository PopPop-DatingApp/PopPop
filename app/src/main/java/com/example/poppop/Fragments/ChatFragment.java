package com.example.poppop.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.poppop.Adapters.RecentChatRecyclerAdapter;
import com.example.poppop.Model.ChatroomModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    public ChatFragment() {
    // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyler_view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Your code to be executed when the fragment is resumed
        setupRecyclerView();
    }

    void setupRecyclerView(){

        Query query = FirebaseUtils.getAllChatroomCollectionReference()
                .whereArrayContains("userIds",FirebaseUtils.currentUserId())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query,ChatroomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}