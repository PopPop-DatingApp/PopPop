package com.example.poppop;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import com.example.poppop.Adapters.CardStackAdapter;
import com.example.poppop.Adapters.UserModelDiffCallback;
import com.example.poppop.Model.UserModel;
import com.example.poppop.cardstackview.CardStackLayoutManager;
import com.example.poppop.cardstackview.CardStackListener;
import com.example.poppop.cardstackview.CardStackView;
import com.example.poppop.cardstackview.Direction;
import com.example.poppop.cardstackview.Duration;
import com.example.poppop.cardstackview.RewindAnimationSetting;
import com.example.poppop.cardstackview.StackFrom;
import com.example.poppop.cardstackview.SwipeAnimationSetting;
import com.example.poppop.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity implements CardStackListener {

    private CardStackView cardStackView;
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Log.d("CardStackView", "create");

//        setupNavigation();
        setupCardStackView();
        setupButton();
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {
        Log.d("CardStackView", "onCardDragging: d = " + direction.name() + ", r = " + ratio);
    }

    @Override
    public void onCardSwiped(Direction direction) {
        Log.d("CardStackView", "onCardSwiped: p = " + manager.getTopPosition() + ", d = " + direction);
        if (manager.getTopPosition() == adapter.getItemCount() - 5) {
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

//    private void setupNavigation() {
//        // Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        // DrawerLayout
//        drawerLayout = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
//                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
//        actionBarDrawerToggle.syncState();
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//
//        // NavigationView
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener(menuItem -> {
//            switch (menuItem.getItemId()) {
//                case R.id.reload:
//                    reload();
//                    break;
//                case R.id.add_spot_to_first:
//                    addFirst(1);
//                    break;
//                case R.id.add_spot_to_last:
//                    addLast(1);
//                    break;
//                case R.id.remove_spot_from_first:
//                    removeFirst(1);
//                    break;
//                case R.id.remove_spot_from_last:
//                    removeLast(1);
//                    break;
//                case R.id.replace_first_spot:
//                    replace();
//                    break;
//                case R.id.swap_first_for_last:
//                    swap();
//                    break;
//            }
//            drawerLayout.closeDrawers();
//            return true;
//        });
//    }

    private void setupCardStackView() {
        initialize();
    }

    private void setupButton() {
        View skip = findViewById(R.id.skip_button);
        skip.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new AccelerateInterpolator())
                    .build();
            manager.setSwipeAnimationSetting(setting);
            cardStackView.swipe();
        });

        View rewind = findViewById(R.id.rewind_button);
        rewind.setOnClickListener(v -> {
            RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new DecelerateInterpolator())
                    .build();
            manager.setRewindAnimationSetting(setting);
            cardStackView.rewind();
        });

        View like = findViewById(R.id.like_button);
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

    private void initialize() {
        Log.e("CardStackView", "CardStackLayoutManager is null");
        manager = new CardStackLayoutManager(this, this);
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        cardStackView = findViewById(R.id.card_stack_view);
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
            List<UserModel> newUserModels = new ArrayList<>(oldUserModels);
            newUserModels.addAll(createUserModels()); // Assuming createUserModels() returns a list of new UserModels
            UserModelDiffCallback callback = new UserModelDiffCallback(oldUserModels, newUserModels);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            adapter.setUserModels(newUserModels);
            result.dispatchUpdatesTo(adapter);
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
        userModels.add(new UserModel("John Doe", 25, images));
        userModels.add(new UserModel("Jane Smith", 30, images));
        userModels.add(new UserModel("Bob Johnson", 28, images));
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

