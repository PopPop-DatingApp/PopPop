package com.example.poppop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.poppop.Adapters.ChatRecyclerAdapter;
import com.example.poppop.Model.ChatMessageModel;
import com.example.poppop.Model.ChatroomModel;
import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FCMSender;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    FCMSender fcmSender;
    UserModel otherUser;
    TextView otherUserName;
    EditText msgInput;
    ImageButton sendMsgBtn;
    ImageButton backBtn;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String uid = getIntent().getStringExtra("uid");
        fcmSender = new FCMSender(this);
        otherUserName = findViewById(R.id.other_username);
        msgInput = findViewById(R.id.chat_message_input);
        sendMsgBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.chat_recycler_view);

        FirestoreUserUtils.getUserModelByUid(uid)
                .addOnSuccessListener(userModel -> {
                    // This method is called when UserModel is successfully retrieved
                    otherUser = userModel;

                    if (otherUser != null) {
                        otherUserName.setText(otherUser.getName());
                        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(),otherUser.getUserId());
                        getOrCreateChatroomModel();
                        setupChatRecyclerView();
                    } else {
                        Log.e("ChatActivity", "otherUser is null");
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure case
                    Log.e("Firestore", "Error retrieving UserModel: " + e.getMessage());
                    finish();
                });

        backBtn.setOnClickListener(v -> onBackPressed());
        sendMsgBtn.setOnClickListener(v -> {
            String message = msgInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        });

    }

    void getOrCreateChatroomModel(){
        FirebaseUtils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtils.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    void setupChatRecyclerView(){
        Query query = FirebaseUtils.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        //scroll behaviour when there is a new message
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message){

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtils.currentUserId(),Timestamp.now());
        FirebaseUtils.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        msgInput.setText("");
//                            sendNotification(message);
                        fcmSender.sendPushToSingleInstance(this,otherUser.getFcmToken(),FirebaseUtils.currentUserName(),message);
                    }
                });
    }
}