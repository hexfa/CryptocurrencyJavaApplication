package com.cryptocurrency.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cryptocurrency.R;
import com.cryptocurrency.databinding.MarketfragRvItemBinding;
import com.cryptocurrency.models.cryptolistmodel.DataItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.MarketRV_Holder> {


    ArrayList<DataItem> dataItems;
    LayoutInflater layoutInflater;

    public MarketListAdapter(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @NotNull
    @Override
    public MarketRV_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        MarketfragRvItemBinding marketfragRvItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.marketfrag_rv_item, parent, false);
        return new MarketRV_Holder(marketfragRvItemBinding);
    }

    @Override
    public void onBindViewHolder(MarketRV_Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(dataItems.get(position), position);

        // set onclick for RecyclerView Items

        holder.marketfragRvItemBinding.marketRVCon.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<DataItem> newData) {
        dataItems = newData;
        notifyDataSetChanged();

    }

    static class MarketRV_Holder extends RecyclerView.ViewHolder {
        MarketfragRvItemBinding marketfragRvItemBinding;

        public MarketRV_Holder(MarketfragRvItemBinding marketfragRvItemBinding) {
            super(marketfragRvItemBinding.getRoot());
            this.marketfragRvItemBinding = marketfragRvItemBinding;
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void bind(DataItem dataItem, int position) {

            loadCoinLogo(dataItem);
            loadChart(dataItem);
            SetColorText(dataItem);
            marketfragRvItemBinding.marketCoinNumber.setText(String.valueOf(position + 1));
            marketfragRvItemBinding.marketCoinName.setText(dataItem.getName());
            marketfragRvItemBinding.marketCoinSymbol.setText(dataItem.getSymbol());
            SetDecimalsForPrice(dataItem);
            //set + or - before present change
            if (dataItem.getListQuote().get(0).getPercentChange24h() > 0) {
                marketfragRvItemBinding.MarketUpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                marketfragRvItemBinding.marketCoinChange.setText(String.format("%.2f", dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            } else if (dataItem.getListQuote().get(0).getPercentChange24h() < 0) {
                marketfragRvItemBinding.MarketUpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                marketfragRvItemBinding.marketCoinChange.setText(String.format("%.2f", dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            } else {
                marketfragRvItemBinding.marketCoinChange.setText(String.format("%.2f", dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }
            marketfragRvItemBinding.executePendingBindings();
        }

        private void loadCoinLogo(DataItem dataItem) {
            Glide.with(marketfragRvItemBinding.getRoot().getContext())
                    .load("https://s2.coinmarketcap.com/static/img/coins/32x32/" + dataItem.getId() + ".png")
                    .thumbnail(Glide.with(marketfragRvItemBinding.getRoot().getContext()).load(R.drawable.loading))
                    .into(marketfragRvItemBinding.coinlogo);
        }

        private void loadChart(DataItem dataItem) {

            Glide.with(marketfragRvItemBinding.getRoot().getContext())
                    .load("https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + dataItem.getId() + ".png")
                    .into(marketfragRvItemBinding.chartImage);
        }

        //set different decimals for different price
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        private void SetDecimalsForPrice(DataItem dataItem) {
            if (dataItem.getListQuote().get(0).getPrice() < 1) {
                marketfragRvItemBinding.marketCoinPrice.setText("$" + String.format("%.6f", dataItem.getListQuote().get(0).getPrice()));
            } else if (dataItem.getListQuote().get(0).getPrice() < 10) {
                marketfragRvItemBinding.marketCoinPrice.setText("$" + String.format("%.4f", dataItem.getListQuote().get(0).getPrice()));
            } else {
                marketfragRvItemBinding.marketCoinPrice.setText("$" + String.format("%.2f", dataItem.getListQuote().get(0).getPrice()));
            }
        }

        //set Color Green and Red for price and chart
        private void SetColorText(DataItem dataItem) {
            int greenColor = Color.parseColor("#FF00FF40");
            int redColor = Color.parseColor("#FFFF0000");
            int whiteColor = Color.parseColor("#FFFFFF");
            if (dataItem.getListQuote().get(0).getPercentChange24h() < 0) {
                marketfragRvItemBinding.chartImage.setColorFilter(redColor);
                marketfragRvItemBinding.marketCoinChange.setTextColor(Color.RED);
            } else if (dataItem.getListQuote().get(0).getPercentChange24h() > 0) {
                marketfragRvItemBinding.chartImage.setColorFilter(greenColor);
                marketfragRvItemBinding.marketCoinChange.setTextColor(Color.GREEN);
            } else {
                marketfragRvItemBinding.chartImage.setColorFilter(whiteColor);
                marketfragRvItemBinding.marketCoinChange.setTextColor(Color.WHITE);
            }
        }
    }
}
