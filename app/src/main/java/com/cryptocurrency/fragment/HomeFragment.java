package com.cryptocurrency.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.cryptocurrency.MainActivity;
import com.cryptocurrency.R;
import com.cryptocurrency.adapter.ImageCarouselAdapter;
import com.cryptocurrency.adapter.TopCoinListAdapter;
import com.cryptocurrency.adapter.TopPerformersAdapter;
import com.cryptocurrency.databinding.FragmentHomeBinding;
import com.cryptocurrency.models.cryptolistmodel.AllMarketModel;
import com.cryptocurrency.models.cryptolistmodel.DataItem;
import com.cryptocurrency.viewmodel.AppViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    MainActivity mainActivity;
    AppViewModel appViewModel;
    TopCoinListAdapter topCoinRvAdapter;
    TopPerformersAdapter topGainLoserAdapter;

    public List<String> top_want = Arrays.asList("BTC", "ETH", "BNB", "ADA", "XRP", "DOGE", "DOT", "UNI", "LTC", "LINK");

    CompositeDisposable compositeDisposable;

    @Inject
    String name;

    // get instance of main activity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_home, container, false);

        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        compositeDisposable = new CompositeDisposable();
        setupViewPager2();
        getAllMarketDataFromDb();
        setupTablayout(binding.topGainIndicator, binding.topLoseIndicator);

        return binding.getRoot();
    }

    private void setupTablayout(View topGainIndicator, View topLoseIndicator) {

        topGainLoserAdapter = new TopPerformersAdapter(this);
        binding.viewPager2.setAdapter(topGainLoserAdapter);


        Animation gainAnimIn = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.slide_from_left);
        Animation gainAnimOut = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.slide_out_left);
        Animation loseAnimIn = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.slide_from_right);
        Animation loseAnimOut = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.slide_out_right);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 0) {
                    topLoseIndicator.startAnimation(loseAnimOut);
                    topLoseIndicator.setVisibility(View.GONE);
                    topGainIndicator.setVisibility(View.VISIBLE);
                    topGainIndicator.startAnimation(gainAnimIn);

                } else {
                    topGainIndicator.startAnimation(gainAnimOut);
                    topGainIndicator.setVisibility(View.GONE);
                    topLoseIndicator.setVisibility(View.VISIBLE);
                    topLoseIndicator.startAnimation(loseAnimIn);
                }
            }
        });

        new TabLayoutMediator(binding.tablayout, binding.viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Top Gain");
            } else {
                tab.setText("Top Lose");
            }
        }).attach();

    }

    private void setupViewPager2() {

        appViewModel.getMutableLiveData().observe(requireActivity(), pics -> {
            binding.viewPagerImageSlider.setAdapter(new ImageCarouselAdapter(pics));
            binding.viewPagerImageSlider.setOffscreenPageLimit(3);
            binding.viewPagerImageSlider.setVisibility(View.VISIBLE);
        });
    }

    private void getAllMarketDataFromDb() {
        Disposable disposable = appViewModel.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(marketListEntity -> {

                    AllMarketModel allMarketModel = marketListEntity.getAllMarketModel();
                    ArrayList<DataItem> top10 = new ArrayList<>();
                    for (int i = 0; i < allMarketModel.getRootData().getCryptoCurrencyList().size(); i++) {
                        for (int j = 0; j < top_want.size(); j++) {
                            String coin_name = top_want.get(j);
                            if (allMarketModel.getRootData().getCryptoCurrencyList().get(i).getSymbol().equals(coin_name)) {
                                DataItem dataItem = allMarketModel.getRootData().getCryptoCurrencyList().get(i);
                                top10.add(dataItem);
                            }
                        }
                    }

                    if (binding.TopCoinRv.getAdapter() != null) {
                        topCoinRvAdapter = (TopCoinListAdapter) binding.TopCoinRv.getAdapter();
                        topCoinRvAdapter.updateData(top10);
                    } else {
                        topCoinRvAdapter = new TopCoinListAdapter(top10);
                        binding.TopCoinRv.setAdapter(topCoinRvAdapter);
                    }
                });
        compositeDisposable.add(disposable);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(view);
    }

    private void setupToolbar(View view) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment).setOpenableLayout(mainActivity.drawerLayout).build();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        // set click listener for navigate profile fragment
        toolbar.setOnMenuItemClickListener(item -> NavigationUI.onNavDestinationSelected(item, navController));
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.homeFragment) {
                toolbar.setNavigationIcon(R.drawable.ic_baseline_sort_24);
                toolbar.setTitle("Home");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}