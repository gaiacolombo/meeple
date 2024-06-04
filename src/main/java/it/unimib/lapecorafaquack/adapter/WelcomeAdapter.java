package it.unimib.lapecorafaquack.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import it.unimib.lapecorafaquack.R;
import it.unimib.lapecorafaquack.model.WelcomeCard;

public class WelcomeAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<WelcomeCard> modelArrayList;

    public WelcomeAdapter(Context context, ArrayList<WelcomeCard> modelArrayList){
        this.context = context;
        this.modelArrayList = modelArrayList;
    }


    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object){
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){

        View view = LayoutInflater.from(context).inflate(R.layout.card_item, container, false);

        ImageView imgIv = view.findViewById(R.id.img1);
        TextView titleTv = view.findViewById(R.id.title);
        TextView descriptionTv = view.findViewById(R.id.description);

        WelcomeCard model = modelArrayList.get(position);
        String title = model.getTitle();
        String description = model.getDescription();
        int image = model.getImage();

        imgIv.setImageResource(image);
        titleTv.setText(title);
        descriptionTv.setText(description);

        container.addView(view, position);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object){
        container.removeView((View)object);
    }

}