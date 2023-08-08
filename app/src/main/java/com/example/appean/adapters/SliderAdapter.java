package com.example.appean.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.appean.R;
import com.example.appean.models.SliderItem;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    //Contexto para determinar los elementos
    private Context context;
    //Lista que guarda cada item del slider
    private List<SliderItem> mSliderItems = new ArrayList<>();

    //Constructores
    public SliderAdapter(){}
    public SliderAdapter(Context context, List<SliderItem> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SliderItem sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    //Método principal que determina las configuraciones apenas se crea la vista
    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {

        //Me retorna una clase que creamos, pero en términos generales es un viewholder, que la inflamos
        // con la plantilla del slider que contendrá los item
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    //Establezco los valores que se van a mostrar en cada layout
    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderItem sliderItem = mSliderItems.get(position);

        if(sliderItem.getImageUrl() != null){
            if(!sliderItem.getImageUrl().isEmpty()){
                Picasso.with(context).load(sliderItem.getImageUrl()).into(viewHolder.imageViewSlider);
            }
        }

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    //Clase para complementar el adapter,
    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewSlider;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewSlider = itemView.findViewById(R.id.iv_auto_image_slider);
            this.itemView = itemView;
        }
    }

}
