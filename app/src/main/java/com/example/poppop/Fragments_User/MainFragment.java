package com.example.poppop.Fragments_User;

import static com.example.poppop.Utils.FirestoreUserUtils.addUserToDisLikedList;
import static com.example.poppop.Utils.FirestoreUserUtils.addUserToLikedList;
import static com.example.poppop.Utils.FirestoreUserUtils.addUserToSwipedList;
import static com.example.poppop.Utils.FirestoreUserUtils.removeUserFromDislikedList;
import static com.example.poppop.Utils.FirestoreUserUtils.removeUserFromLikedList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import com.example.poppop.Activities.EditProfileActivity;
import com.example.poppop.Adapters.CardStackAdapter;
import com.example.poppop.Adapters.UserModelDiffCallback;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FCMSender;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.NotificationUtils;
import com.example.poppop.ViewModel.UserListViewModel;
import com.example.poppop.ViewModel.UserListViewModelFactory;
import com.example.poppop.cardstackview.CardStackLayoutManager;
import com.example.poppop.cardstackview.CardStackListener;
import com.example.poppop.cardstackview.CardStackView;
import com.example.poppop.cardstackview.Direction;
import com.example.poppop.cardstackview.Duration;
import com.example.poppop.cardstackview.RewindAnimationSetting;
import com.example.poppop.cardstackview.SwipeAnimationSetting;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements CardStackListener {

    private UserListViewModel userListViewModel;
    private CardStackView cardStackView;
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private UserModel userModel;
    View rewind;
    FCMSender fcmSender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initializeFragmentView(view);
        setupCardStackView(view);
        setupButton(view);
        fcmSender = new FCMSender(requireContext());
        return view;
    }

    private void initializeFragmentView(View view) {
        if (view != null) {
            DocumentReference userDocRef = FirebaseUtils.getUserReference(FirebaseUtils.currentUserId());
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        userModel = documentSnapshot.toObject(UserModel.class);
                        if (userModel != null && userModel.getGenderPref() != null) {
                            //set up UI
                            if(!userModel.getPremium())
                                rewind.setVisibility(View.INVISIBLE);
                            else rewind.setVisibility(View.VISIBLE);
                            // Initialize the ViewModel using ViewModelProvider with AndroidViewModelFactory
//                            userListViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
//                                    .get(UserListViewModel.class);
                            userListViewModel = new ViewModelProvider(requireActivity(), new UserListViewModelFactory(new FirebaseUtils()))
                                    .get(UserListViewModel.class);

                            // Observe the LiveData in the ViewModel
                            userListViewModel.getUserListWithPref(FirebaseUtils.currentUserId(), userModel.getCurrentLocation(), userModel.getGenderPref(), userModel.getMaxDistPref(), userModel.getAgeRangePref()).observe(getViewLifecycleOwner(), userList -> {
                                if (userList != null) {
                                    // Update UI or adapter with the new user list
                                    adapter.setPremium(userModel.getPremium());
                                    adapter.setCurrentUserId(userModel.getUserId());
                                    adapter.setUserModels(userList);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                            startActivity(intent);
                            // Handle profile exit
                            // requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                }
            });
        } else {
            // Handle the case where the Fragment's view is null
            Log.e("MainFragment", "Fragment view is null in onActivityCreated");
        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // Remove the observer to avoid "Can't access the Fragment View's LifecycleOwner" error
//        if (userModel != null) {
//            usersViewModel.getUserList(
//                    FirebaseUtils.currentUserId(),
//                    userModel.getCurrentLocation(),
//                    userModel.getGenderPref(),
//                    userModel.getMaxDistPref(),
//                    userModel.getAgeRangePref()
//            ).removeObservers(getViewLifecycleOwner());
//        }
//    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {
        Log.d("CardStackView", "onCardDragging: d = " + direction.name() + ", r = " + ratio);
    }

    @Override
    public void onCardSwiped(Direction direction) {
        int swipedPosition = manager.getTopPosition();
        UserModel swipedUserModel = adapter.getUserModels().get(swipedPosition - 1); // Adjust for zero-based index
        if(direction.equals(Direction.Right)){
            // Add swipedUserModel to the liked_list of the current user
            addUserToLikedList(FirebaseUtils.currentUserId(), swipedUserModel.getUserId());

            // Check if the current user is in the swiped user's liked_list
            if (swipedUserModel.getLiked_list() != null && swipedUserModel.getLiked_list().contains(FirebaseUtils.currentUserId())) {
                // It's a match!
                fcmSender.sendPushToSingleInstance(requireActivity(),swipedUserModel.getFcmToken(),"Common!","You have a match!");
                NotificationUtils.showLocalNotification(getContext(),"Woohoo", "You have a match!");
                FirebaseUtils.createEmptyChatroomDocumentWithId(userModel.getUserId(),swipedUserModel.getUserId());
            }
        }
        else{
            addUserToDisLikedList(FirebaseUtils.currentUserId(), swipedUserModel.getUserId());
        }
        // Add swipedUserModel to the swiped_list of the current user
        addUserToSwipedList(FirebaseUtils.currentUserId(), swipedUserModel.getUserId());

        if (manager.getTopPosition() == adapter.getItemCount()) {
            paginate();
        }
    }

    @Override
    public void onCardRewound() {
        Log.d("CardStackView", "onCardRewound: " + manager.getTopPosition());
    }

    @Override
    public void onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: " + manager.getTopPosition());
    }

    @Override
    public void onCardAppeared(View view, int position) {
        TextView textView = view.findViewById(R.id.item_name);
        Log.d("CardStackView", "onCardAppeared: (" + position + ") " + textView.getText());
    }

    @Override
    public void onCardDisappeared(View view, int position) {
        TextView textView = view.findViewById(R.id.item_name);
        Log.d("CardStackView", "onCardDisappeared: (" + position + ") " + textView.getText());
    }

    private void setupCardStackView(View view) {
        initialize(view);
    }

    private void setupButton(View view) {
        View skip = view.findViewById(R.id.skip_button);
        skip.setOnClickListener(v -> {
            // Swipe left logic
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            manager.setSwipeAnimationSetting(setting);
            cardStackView.swipe();
        });

        rewind = view.findViewById(R.id.rewind_button);
        rewind.setOnClickListener(v -> {
            RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new DecelerateInterpolator())
                    .build();
            manager.setRewindAnimationSetting(setting);
            cardStackView.rewind();
        });


        View like = view.findViewById(R.id.like_button);
        like.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            manager.setSwipeAnimationSetting(setting);
            cardStackView.swipe();
        });
    }

    private void initialize(View view) {
        manager = new CardStackLayoutManager(requireContext(), this);
        // Rest of the initialization code...

        cardStackView = view.findViewById(R.id.card_stack_view);
        if (manager != null) {
            cardStackView.setLayoutManager(manager);
        } else {
            // Handle the case where manager is null
            Log.e("CardStackView", "CardStackLayoutManager is null");
        }
        adapter = new CardStackAdapter(createUserModels());
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private void paginate() {
        List<UserModel> oldUserModels = adapter.getUserModels();

        if (oldUserModels != null) {
            userListViewModel.getUserListWithPref(FirebaseUtils.currentUserId(), userModel.getCurrentLocation(), userModel.getGenderPref(), userModel.getMaxDistPref(), userModel.getAgeRangePref()).observe(getViewLifecycleOwner(), userList -> {
                if (userList != null) {
                    // Update UI or adapter with the new user list
                    UserModelDiffCallback callback = new UserModelDiffCallback(oldUserModels, userList);
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
                    adapter.setUserModels(userList);
                    result.dispatchUpdatesTo(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
            List<UserModel> newUserModels = new ArrayList<>(oldUserModels);
            newUserModels.addAll(createUserModels()); // Assuming createUserModels() returns a list of new UserModels

        }
    }

    private UserModel createUserModel() {
        return new UserModel(
                "Yasaka Shrine",
                18,
                "https://source.unsplash.com/Xq1ntWruZQI/600x800"
        );
    }

    private List<UserModel> createUserModels() {
        List<String> images = new ArrayList<>();
        images.add("https://source.unsplash.com/Xq1ntWruZQI/600x800");
        images.add("https://source.unsplash.com/NYyCqdBOKwc/600x800");
        images.add("https://source.unsplash.com/buF62ewDLcQ/600x800");
        List<UserModel> userModels = new ArrayList<>();
        userModels.add(new UserModel("John Doe", 25));
        userModels.add(new UserModel("Jane Smith", 30));
        userModels.add(new UserModel("Bob Johnson", 28));
        // Add more users as needed
        return userModels;
    }

    private List<UserModel> createUserModels(int count) {
        List<UserModel> userModels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userModels.add(createUserModel());
        }
        return userModels;
    }

}
