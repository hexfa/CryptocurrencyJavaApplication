package com.cryptocurrency.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cryptocurrency.R;
import com.cryptocurrency.databinding.SliderImageItemBinding;

import java.util.ArrayList;

public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.SliderImageViewHolder> {

    LayoutInflater layoutInflater;

    ArrayList<Integer> arrayList;

    public ImageCarouselAdapter(ArrayList<Integer> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public SliderImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        SliderImageItemBinding sliderImageItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.slider_image_item, parent, false);

        return new SliderImageViewHolder(sliderImageItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderImageViewHolder holder, int position) {
        holder.bind(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class SliderImageViewHolder extends RecyclerView.ViewHolder {
        SliderImageItemBinding sliderImageItemBinding;

        public SliderImageViewHolder(@NonNull SliderImageItemBinding sliderImageItemBinding) {
            super(sliderImageItemBinding.getRoot());
            this.sliderImageItemBinding = sliderImageItemBinding;
        }

        public void bind(int photo) {
            sliderImageItemBinding.viewfading.setVisibility(View.VISIBLE);
            Glide.with(sliderImageItemBinding.getRoot().getContext())
                    .load(photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(sliderImageItemBinding.imageSlide);
            sliderImageItemBinding.executePendingBindings();
        }
    }
}