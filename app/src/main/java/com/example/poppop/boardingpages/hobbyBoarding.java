//
//package com.example.poppop.boardingpages;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.poppop.MainActivity;
//import com.example.poppop.Model.UserModel;
//import com.example.poppop.R;
//import com.example.poppop.Utils.FirebaseUtils;
//import com.example.poppop.Utils.FirestoreUserUtils;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class hobbyBoarding extends AppCompatActivity {
//    private String userName, userGender, userHoro;
//    private Integer userAge;
//    private UserModel newUser;
//
//    private RecyclerView recyclerView;
//    private InterestAdapter interestAdapter;
//    private List<String> selectedInterests = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_hobby_boarding);
//
//        userName = getIntent().getStringExtra("userName");
//        userAge = getIntent().getIntExtra("userAge", 18);
//        userGender = getIntent().getStringExtra("userGender");
//        userHoro = getIntent().getStringExtra("userHoro");
//
//        Button save = findViewById(R.id.saveBtn);
//
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        interestAdapter = new InterestAdapter(new ArrayList<>(), interest -> {
//            // Handle interest click
//            if (selectedInterests.size() < 5) {
//                selectedInterests.add(interest);
//            }else {
//
//            }
//        }, selectedInterests);
//        recyclerView.setAdapter(interestAdapter);
//
//        save.setOnClickListener(v -> {
//            FirestoreUserUtils.getUserModelByUid(FirebaseUtils.currentUserId())
//                    .addOnSuccessListener(userModel -> {
//                        newUser = userModel;
//
//                        if (newUser != null) {
//                            newUser.setName(userName);
//                            newUser.setAge(userAge);
//                            newUser.setGender(userGender);
//                            newUser.setHoroscopeSign(userHoro);
//
//                            // Create a map to store selected interests
//                            Map<String, Boolean> selectedInterestsMap = new HashMap<>();
//                            for (String interest : selectedInterests) {
//                                selectedInterestsMap.put(interest, true);
//                            }
//
//                            // Set the selected interests map in the user object
//                            newUser.setInterests(selectedInterestsMap);
//
//                            FirestoreUserUtils.updateUserModel(newUser)
//                                    .addOnCompleteListener(task -> {
//                                        if (task.isSuccessful()) {
//                                            startActivity(new Intent(hobbyBoarding.this, MainActivity.class));
//                                        } else {
//                                            Exception exception = task.getException();
//                                            if (exception != null) {
//                                                exception.printStackTrace();
//                                            }
//                                        }
//                                    });
//                        } else {
//                            finish();
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        finish();
//                    });
//        });
//
//        CollectionReference inter = FirebaseUtils.getAllInterestsCollectionReference();
//        inter.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    String documentId = document.getString("name");
//                    Log.d("interest", documentId);
//                    interestAdapter.addData(documentId);
//                }
//            } else {
//                Exception e = task.getException();
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
////    public static class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestViewHolder> {
////        private List<String> interests;
////        private OnInterestClickListener onInterestClickListener;
////        private List<String> selectedInterests;
////
////        public interface OnInterestClickListener {
////            void onInterestClick(String interest);
////        }
////
////        public InterestAdapter(List<String> interests, OnInterestClickListener listener, List<String> selectedInterests) {
////            this.interests = interests;
////            this.onInterestClickListener = listener;
////            this.selectedInterests = selectedInterests;
////        }
////
////        @NonNull
////        @Override
////        public InterestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);
////            return new InterestViewHolder(view);
////        }
////
////        @Override
////        public void onBindViewHolder(@NonNull InterestViewHolder holder, int position) {
////            String interest = interests.get(position);
////            holder.bind(interest);
////
////            // Set click listener for item
////            holder.itemView.setOnClickListener(v -> {
////                onInterestClickListener.onInterestClick(interest);
////                notifyDataSetChanged();
////            });
////        }
////
////        @Override
////        public int getItemCount() {
////            return interests.size();
////        }
////
////        public void addData(String interest) {
////            interests.add(interest);
////            notifyDataSetChanged();
////        }
////
////        static class InterestViewHolder extends RecyclerView.ViewHolder {
////            TextView interestTextView;
////
////            public InterestViewHolder(@NonNull View itemView) {
////                super(itemView);
////                interestTextView = itemView.findViewById(R.id.interestTextView);
////            }
////
////            public void bind(String interest) {
////                interestTextView.setText(interest);
////            }
////        }
////    }
//public static class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestViewHolder> {
//    private List<String> interests;
//    private OnInterestClickListener onInterestClickListener;
//    private List<String> selectedInterests;
//
//    public interface OnInterestClickListener {
//        void onInterestClick(String interest);
//    }
//
//    public InterestAdapter(List<String> interests, OnInterestClickListener listener, List<String> selectedInterests) {
//        this.interests = interests;
//        this.onInterestClickListener = listener;
//        this.selectedInterests = selectedInterests;
//    }
//
//    @NonNull
//    @Override
//    public InterestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);
//        return new InterestViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull InterestViewHolder holder, int position) {
//        String interest = interests.get(position);
//        holder.bind(interest, selectedInterests.contains(interest));
//
//        // Set click listener for item
//        holder.itemView.setOnClickListener(v -> {
//                selectedInterests.add(interest);
//            holder.toggleSelection(selectedInterests.contains(interest));
//            onInterestClickListener.onInterestClick(interest);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return interests.size();
//    }
//
//    public void addData(String interest) {
//        interests.add(interest);
//        notifyDataSetChanged();
//    }
//
//    static class InterestViewHolder extends RecyclerView.ViewHolder {
//        TextView interestTextView;
//
//        public InterestViewHolder(@NonNull View itemView) {
//            super(itemView);
//            interestTextView = itemView.findViewById(R.id.interestTextView);
//        }
//
//        public void bind(String interest, boolean isSelected) {
//            interestTextView.setText(interest);
//            updateTextColor(isSelected);
//        }
//
//        public void toggleSelection(boolean isSelected) {
//            updateTextColor(isSelected);
//        }
//
//        private void updateTextColor(boolean isSelected) {
//            if (isSelected) {
//                interestTextView.setTextColor(Color.parseColor("#FF69B4"));
//            } else {
//                interestTextView.setTextColor(Color.BLACK);
//            }
//        }
//    }
//
//}
//
//
//}
package com.example.poppop.boardingpages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poppop.MainActivity;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class hobbyBoarding extends AppCompatActivity {
    private String userName, userGender, userHoro;
    private Integer userAge;
    private RecyclerView recyclerView;
    private InterestAdapter interestAdapter;
    private List<String> selectedInterests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobby_boarding);

        userName = getIntent().getStringExtra("userName");
        userAge = getIntent().getIntExtra("userAge", 18);
        userGender = getIntent().getStringExtra("userGender");
        userHoro = getIntent().getStringExtra("userHoro");

        Button save = findViewById(R.id.saveBtn);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        interestAdapter = new InterestAdapter(new ArrayList<>(), interest -> {
            // Handle interest click
            if (selectedInterests.size() < 5) {
                if (!selectedInterests.contains(interest)) {
                    selectedInterests.add(interest);
                } else {
                    selectedInterests.remove(interest);
                }
                interestAdapter.notifyDataSetChanged(); // Update the RecyclerView
            }else{
                if(selectedInterests.contains(interest)){
                    selectedInterests.remove(interest);
                }
            }
        }, selectedInterests);
        recyclerView.setAdapter(interestAdapter);

        save.setOnClickListener(v -> {
            FirestoreUserUtils.getUserModelByUid(FirebaseUtils.currentUserId())
                    .addOnSuccessListener(userModel -> {
                        if (userModel != null) {
                            userModel.setName(userName);
                            userModel.setAge(userAge);
                            userModel.setGender(userGender);
                            userModel.setHoroscopeSign(userHoro);

                            // Set the selected interests in the user object
                            userModel.setInterests(selectedInterests);

                            FirestoreUserUtils.updateUserModel(userModel)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(hobbyBoarding.this, MainActivity.class));
                                        } else {
                                            Exception exception = task.getException();
                                            if (exception != null) {
                                                exception.printStackTrace();
                                            }
                                        }
                                    });
                        } else {
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        finish();
                    });
        });

        CollectionReference inter = FirebaseUtils.getAllInterestsCollectionReference();
        inter.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getString("name");
                    Log.d("interest", documentId);
                    interestAdapter.addData(documentId);
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestViewHolder> {
        private List<String> interests;
        private OnInterestClickListener onInterestClickListener;
        private List<String> selectedInterests;

        public interface OnInterestClickListener {
            void onInterestClick(String interest);
        }

        public InterestAdapter(List<String> interests, OnInterestClickListener listener, List<String> selectedInterests) {
            this.interests = interests;
            this.onInterestClickListener = listener;
            this.selectedInterests = selectedInterests;
        }

        @NonNull
        @Override
        public InterestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);
            return new InterestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InterestViewHolder holder, int position) {
            String interest = interests.get(position);
            holder.bind(interest, selectedInterests.contains(interest));

            // Set click listener for item
            holder.itemView.setOnClickListener(v -> {
                if (selectedInterests.size() < 5) {
                    onInterestClickListener.onInterestClick(interest);
                    holder.toggleSelection(selectedInterests.contains(interest));
                } else{
                    onInterestClickListener.onInterestClick(interest);
                    holder.toggleSelection(false);
                }
            });
        }

        @Override
        public int getItemCount() {
            return interests.size();
        }

        public void addData(String interest) {
            interests.add(interest);
            notifyDataSetChanged();
        }

        static class InterestViewHolder extends RecyclerView.ViewHolder {
            TextView interestTextView;

            public InterestViewHolder(@NonNull View itemView) {
                super(itemView);
                interestTextView = itemView.findViewById(R.id.interestTextView);
            }

            public void bind(String interest, boolean isSelected) {
                interestTextView.setText(interest);
                updateTextColor(isSelected);
            }

            public void toggleSelection(boolean isSelected) {
                updateTextColor(isSelected);
            }

            private void updateTextColor(boolean isSelected) {
                if (isSelected) {
                    interestTextView.setTextColor(Color.parseColor("#FF69B4"));
                } else {
                    interestTextView.setTextColor(Color.BLACK);
                }
            }
        }
    }
}
