package com.example.appean.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.appean.R;
import com.example.appean.adapters.SliderAdapter;
import com.example.appean.models.SliderItem;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private SliderView mSliderView;

    private SliderAdapter mSliderAdapter;

    private List<SliderItem> mSliderItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderItems = new ArrayList<>();

        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);

        mSliderView = findViewById(R.id.imageSlider);

        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.ZOOMOUTTRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(R.color.verde);
        mSliderView.setIndicatorUnselectedColor(R.color.gris);

    }
}