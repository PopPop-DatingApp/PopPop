package com.example.poppop.Activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poppop.Adapters.ChatRecyclerAdapter;
import com.example.poppop.Model.ChatMessageModel;
import com.example.poppop.Model.ChatroomModel;
import com.example.poppop.Model.ReportCaseModel;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FCMSender;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.ReportCaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity implements ChatRecyclerAdapter.OnMessageLongClickListener {
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    FCMSender fcmSender;
    UserModel currentUser, otherUser;
    TextView otherUserName;
    EditText msgInput;
    ImageButton sendMsgBtn;
    ImageButton backBtn;
    RecyclerView recyclerView;
    Button unmatchBtn, reportBtn;
    private GestureDetector gestureDetector;

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
        unmatchBtn = findViewById(R.id.unmatchBtn);
        reportBtn = findViewById(R.id.reportBtn);

        FirestoreUserUtils.getUserModelByUid(FirebaseUtils.currentUserId())
                .addOnSuccessListener(userModel -> {
                    // This method is called when UserModel is successfully retrieved
                    currentUser = userModel;
                })
                .addOnFailureListener(e -> {
                    // Handle the failure case
                    Log.e("Firestore", "Error retrieving UserModel: " + e.getMessage());
                    finish();
                });

        FirestoreUserUtils.getUserModelByUid(uid)
                .addOnSuccessListener(userModel -> {
                    // This method is called when UserModel is successfully retrieved
                    otherUser = userModel;

                    if (otherUser != null) {
                        otherUserName.setText(otherUser.getName());
                        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(), otherUser.getUserId());
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
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        });
        unmatchBtn.setOnClickListener(v -> {
            unmatchUser();
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                // Handle the tap event here
                // You can implement your logic to respond to the tap
                reportBtn.setVisibility(View.GONE);
                unmatchBtn.setVisibility(View.VISIBLE);
                adapter.clearLongPressState();
                return true;
            }
        });

        recyclerView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

    }

    @Override
    public void onMessageLongClick(ChatMessageModel message) {
        // Handle the long click event here
        reportBtn.setVisibility(View.VISIBLE);
        unmatchBtn.setVisibility(View.GONE);
        reportBtn.setOnClickListener(v -> {
            showReportDialog(message);
        });
    }

    private void showReportDialog(ChatMessageModel message) {
        // Create a custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_report_message, null);

        // Find the EditText in the custom layout
        EditText contextDetailEditText = dialogView.findViewById(R.id.editTextContextDetail);
        TextView msgTextView = dialogView.findViewById(R.id.msg);
        TextView offenderNameTextView = dialogView.findViewById(R.id.offenderName);

        msgTextView.setText(message.getMessage());
        offenderNameTextView.setText(otherUser.getName());

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("REPORT MESSAGE");

        // Set positive button action
        builder.setPositiveButton("Report", null); // We'll set the listener after showing the dialog

        builder.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            positiveButton.setEnabled(false);

            positiveButton.setOnClickListener(view -> {
                String contextDetail = contextDetailEditText.getText().toString().trim();
                sendReport(dialog, message, contextDetail);

            });

            // Add text change listener to EditText
            contextDetailEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Not used in this example
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Not used in this example
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    boolean isEditTextEmpty = editable.toString().trim().isEmpty();
                    // Update positive button state after text changes
                    positiveButton.setEnabled(!isEditTextEmpty);
                    if (isEditTextEmpty) {
                        positiveButton.setTextColor(ContextCompat.getColor(ChatActivity.this, R.color.dark_gray));
                    } else {
                        // Set the text color to the default color when the text is not empty
                        positiveButton.setTextColor(ContextCompat.getColor(ChatActivity.this, R.color.purple_200));
                    }
                }
            });
        });

        // Show the dialog
        dialog.show();
    }

    void sendReport(AlertDialog dialog, ChatMessageModel messageModel, String contextDetail) {
        // Create a ReportCaseModel with the provided details
        ReportCaseModel reportCaseModel = new ReportCaseModel();
        reportCaseModel.setReporterId(currentUser.getUserId());
        reportCaseModel.setOffenderId(messageModel.getSenderId());
        reportCaseModel.setReportedMsg(messageModel.getMessage());
        reportCaseModel.setReportedMsgTime(messageModel.getTimestamp());
        reportCaseModel.setContextDetail(contextDetail);
        reportCaseModel.setReportTime(Timestamp.now());

        // Add the report case
        ReportCaseUtils.addReportCase(reportCaseModel, task -> {
            if (task.isSuccessful()) {
                // Handle success
                Toast.makeText(this, "You have reported this message", Toast.LENGTH_SHORT).show();
            } else {
                // Handle failure
                Toast.makeText(this, "There is an error, try again", Toast.LENGTH_SHORT).show();
            }
            reportBtn.setVisibility(View.GONE);
            unmatchBtn.setVisibility(View.VISIBLE);
            adapter.clearLongPressState();
            dialog.dismiss(); // Dismiss the dialog after reporting
        });
    }

    void getOrCreateChatroomModel() {
        FirebaseUtils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtils.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtils.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        adapter.setOnMessageLongClickListener(this);
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


    void sendMessageToUser(String message) {

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtils.currentUserId(), Timestamp.now());
        FirebaseUtils.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        msgInput.setText("");
//                            sendNotification(message);
                        fcmSender.sendPushToSingleInstance(this, otherUser.getFcmToken(), currentUser.getName(), message);
                    }
                });
    }

    void unmatchUser() {
        // Show a confirmation dialog before unmatching
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unmatch")
                .setMessage("Are you sure you want to unmatch with " + otherUser.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User clicked Yes, proceed with the unmatch
                    performUnmatch();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User clicked No, do nothing or handle accordingly
                    dialog.dismiss();
                })
                .show();
    }

    private void performUnmatch() {
        // Delete the chatroom
        FirebaseUtils.deleteChatroom(
                chatroomId,
                task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Unmatched successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        // Handle the failure case
                        Toast.makeText(ChatActivity.this, "Failed to unmatch", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });
    }
}