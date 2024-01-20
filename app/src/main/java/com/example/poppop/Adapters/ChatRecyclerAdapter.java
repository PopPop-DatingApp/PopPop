package com.example.poppop.Adapters;


import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poppop.Model.ChatMessageModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    private OnMessageLongClickListener onMessageLongClickListener;
    private int highlightedPosition = RecyclerView.NO_POSITION;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    public void setOnMessageLongClickListener(OnMessageLongClickListener listener) {
        this.onMessageLongClickListener = listener;
    }

    public void clearLongPressState() {
        if (highlightedPosition != RecyclerView.NO_POSITION) {
            // Reset the background color of the previously highlighted item
            notifyItemChanged(highlightedPosition);
            highlightedPosition = RecyclerView.NO_POSITION;
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        int adapterPosition = holder.getAdapterPosition();
        if(model.getSenderId().equals(FirebaseUtils.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
        } else {
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
            holder.leftChatLayout.setOnLongClickListener(v -> {
                clearLongPressState();

                // Highlight the currently long-pressed item
                highlightedPosition = adapterPosition;
                notifyItemChanged(highlightedPosition);

                if (onMessageLongClickListener != null) {
                    onMessageLongClickListener.onMessageLongClick(model);
                }
                return true; // Consume the long click event
            });

            int backgroundColor = adapterPosition == highlightedPosition
                    ? ContextCompat.getColor(context, R.color.chat_color_select)
                    : ContextCompat.getColor(context, R.color.chat_color_sender);

            holder.setBackgroundTint(backgroundColor);
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    public interface OnMessageLongClickListener {
        void onMessageLongClick(ChatMessageModel message);
    }


    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
        }
        public void setBackgroundTint(int color) {
            leftChatLayout.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }
}
