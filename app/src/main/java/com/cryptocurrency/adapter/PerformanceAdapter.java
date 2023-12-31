package com.cryptocurrency.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.cryptocurrency.R;
import com.cryptocurrency.databinding.GainloseRvItemBinding;
import com.cryptocurrency.models.cryptolistmodel.DataItem;

import java.util.ArrayList;

public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.GainLoseRvHolder> {

    ArrayList<DataItem> dataItems;
    LayoutInflater layoutInflater;

    public PerformanceAdapter(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @Override
    public GainLoseRvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        GainloseRvItemBinding gainloseRvItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.gainlose_rv_item, parent, false);
        return new GainLoseRvHolder(gainloseRvItemBinding);
    }

    @Override
    public void onBindViewHolder(GainLoseRvHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(dataItems.get(position));

        // set onclick for RecyclerView Items
        holder.gainloseRvItemBinding.GainLoseRVCon.setOnClickListener(v -> {
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

    static class GainLoseRvHolder extends RecyclerView.ViewHolder {

        GainloseRvItemBinding gainloseRvItemBinding;

        public GainLoseRvHolder(GainloseRvItemBinding gainloseRvItemBinding) {
            super(gainloseRvItemBinding.getRoot());
            this.gainloseRvItemBinding = gainloseRvItemBinding;
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void bind(DataItem dataItem) {

            loadCoinLogo(dataItem);
            loadChart(dataItem);
            SetColorText(dataItem);
            gainloseRvItemBinding.GLCoinName.setText(dataItem.getName());
            gainloseRvItemBinding.GLcoinSymbol.setText(dataItem.getSymbol());
            SetDecimalsForPrice(dataItem);
            //set + or - before present change
            if (dataItem.getListQuote().get(0).getPercentChange24h() > 0) {
                gainloseRvItemBinding.UpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                gainloseRvItemBinding.GLcoinChange.setText(String.format("%.2f", dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            } else if (dataItem.getListQuote().get(0).getPercentChange24h() < 0) {
                gainloseRvItemBinding.UpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                gainloseRvItemBinding.GLcoinChange.setText(String.format("%.2f", dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            } else {
                gainloseRvItemBinding.GLcoinChange.setText(String.format("%.2f", dataItem.getListQuote().get(0).getPercentChange24h()) + "%");
            }
            gainloseRvItemBinding.executePendingBindings();
        }

        private void loadCoinLogo(DataItem dataItem) {
            Glide.with(gainloseRvItemBinding.getRoot().getContext())
                    .load("https://s2.coinmarketcap.com/static/img/coins/32x32/" + dataItem.getId() + ".png")
                    .thumbnail(Glide.with(gainloseRvItemBinding.getRoot().getContext()).load(R.drawable.loading))
                    .into(gainloseRvItemBinding.gainLoseCoinlogo);
        }

        private void loadChart(DataItem dataItem) {
            Glide.with(gainloseRvItemBinding.getRoot().getContext())
                    .load("https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + dataItem.getId() + ".png")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(gainloseRvItemBinding.imageView);
        }

        //set different decimals for different price
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        private void SetDecimalsForPrice(DataItem dataItem) {
            if (dataItem.getListQuote().get(0).getPrice() < 1) {
                gainloseRvItemBinding.GLcoinPrice.setText("$" + String.format("%.6f", dataItem.getListQuote().get(0).getPrice()));
            } else if (dataItem.getListQuote().get(0).getPrice() < 10) {
                gainloseRvItemBinding.GLcoinPrice.setText("$" + String.format("%.4f", dataItem.getListQuote().get(0).getPrice()));
            } else {
                gainloseRvItemBinding.GLcoinPrice.setText("$" + String.format("%.2f", dataItem.getListQuote().get(0).getPrice()));
            }
        }

        //set Color Green and Red for price
        private void SetColorText(DataItem dataItem) {
            int greenColor = Color.parseColor("#FF00FF40");
            int redColor = Color.parseColor("#FFFF0000");
            int whiteColor = Color.parseColor("#FFFFFF");
            if (dataItem.getListQuote().get(0).getPercentChange24h() < 0) {
                gainloseRvItemBinding.imageView.setColorFilter(redColor);
                gainloseRvItemBinding.GLcoinChange.setTextColor(Color.RED);
            } else if (dataItem.getListQuote().get(0).getPercentChange24h() > 0) {
                gainloseRvItemBinding.imageView.setColorFilter(greenColor);
                gainloseRvItemBinding.GLcoinChange.setTextColor(Color.GREEN);
            } else {
                gainloseRvItemBinding.imageView.setColorFilter(whiteColor);
                gainloseRvItemBinding.GLcoinChange.setTextColor(Color.WHITE);
            }
        }
    }
}