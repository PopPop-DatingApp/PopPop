package com.example.poppop.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.poppop.Fragments_Admin.HistoryFragment;
import com.example.poppop.Fragments_Admin.ReportCaseFragment;
import com.example.poppop.Fragments_Admin.UserListFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UserListFragment();
            case 1:
                return new ReportCaseFragment();
//            case 2:
//                return new HistoryFragment();
            default:
                return new UserListFragment();
        }
    }

    @Override
    public int getCount() {
        return 2; // Number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Set tab titles
        switch (position) {
            case 0:
                return "User List";
            case 1:
                return "Report cases";
//            case 2:
//                return "History";
            default:
                return null;
        }
    }
}

