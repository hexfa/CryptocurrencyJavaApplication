package com.cryptocurrency.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cryptocurrency.fragment.TopPerformersFragment;


public class TopPerformersAdapter extends FragmentStateAdapter {
    public TopPerformersAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Fragment fragment = new TopPerformersFragment();

        Bundle args = new Bundle();
        args.putInt("pos", position);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
