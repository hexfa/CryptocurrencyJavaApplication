package com.cryptocurrency.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cryptocurrency.R;
import com.cryptocurrency.adapter.GainLoseRvAdapter;
import com.cryptocurrency.databinding.FragmentTopGainLoseBinding;
import com.cryptocurrency.models.cryptolistmodel.AllMarketModel;
import com.cryptocurrency.models.cryptolistmodel.DataItem;
import com.cryptocurrency.viewmodel.AppViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TopGainLoseFragment extends Fragment {

    FragmentTopGainLoseBinding binding;
    AppViewModel appViewModel;
    List<DataItem> data;
    GainLoseRvAdapter gainLoseRvAdapter;
    CompositeDisposable compositeDisposable;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_gain_lose, container, false);
        compositeDisposable = new CompositeDisposable();

        Bundle args = getArguments();
        assert args != null;
        int pos = args.getInt("pos");
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        setupRecyclerView(pos);


        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setupRecyclerView(int pos) {

        Disposable disposable = appViewModel.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(roomMarketEntity -> {
                    AllMarketModel allMarketModel = roomMarketEntity.getAllMarketModel();
                    data = allMarketModel.getRootData().getCryptoCurrencyList();

                    // sort Model list by change percent (lowest to highest)
                    Collections.sort(data, Comparator.comparingInt(o -> (int) o.getListQuote().get(0).getPercentChange24h()));

                    try {
                        ArrayList<DataItem> dataItems = new ArrayList<>();
                        //if page was top Gainers
                        if (pos == 0){
                            //get 10 last Item
                            for (int i = 0;i < 10;i++){
                                dataItems.add(data.get(data.size() - 1 - i));
                            }

                            //if page was top Losers
                        }else if (pos == 1){
                            //get 10 first Item
                            for (int i = 0;i < 10;i++){
                                dataItems.add(data.get(i));
                            }
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        binding.gainLoseRv.setLayoutManager(linearLayoutManager);

                        if (binding.gainLoseRv.getAdapter() == null){
                            gainLoseRvAdapter = new GainLoseRvAdapter(dataItems);
                            binding.gainLoseRv.setAdapter(gainLoseRvAdapter);
                        }else {
                            gainLoseRvAdapter = (GainLoseRvAdapter) binding.gainLoseRv.getAdapter();
                            gainLoseRvAdapter.updateData(dataItems);
                        }
                        binding.gainloseTashieLoader.setVisibility(View.GONE);

                    }catch (Exception e){
                        Log.e("exception", "setupRecyclerView: " + e.getMessage());
                    }

                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
