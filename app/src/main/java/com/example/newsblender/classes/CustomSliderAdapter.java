package com.example.newsblender.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newsblender.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class CustomSliderAdapter extends
        SliderViewAdapter<CustomSliderAdapter.SliderAdapterVH> {

    private final Context context;
    private final ArrayList<SliderItem> photoLinks;

    public CustomSliderAdapter(Context context, ArrayList<String> photoLinks) {
        this.context = context;
        this.photoLinks = new ArrayList<>();
        photoLinks.forEach(item -> this.photoLinks.add(new SliderItem(item)));
    }

    public void deleteItem(int position) {
        this.photoLinks.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
        this.photoLinks.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderItem sliderItem = photoLinks.get(position);
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImageUrl())
                .fitCenter()
                .into(viewHolder.imageViewBackground);
    }

    @Override
    public int getCount() {
        return photoLinks.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            this.imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            this.itemView = itemView;
        }
    }

}