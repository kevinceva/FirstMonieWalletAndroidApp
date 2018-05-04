package com.ceva.ubmobile.core.lifestyle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.DealItem;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {

    int width, height;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static final ImageFragment newInstance(String imageURL) {

        ImageFragment f = new ImageFragment();

        Bundle bdl = new Bundle(1);

        bdl.putString(DealItem.KEY_IMAGE_1, imageURL);
        Log.debug("product image", imageURL);

        f.setArguments(bdl);

        return f;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String url = getArguments().getString(DealItem.KEY_IMAGE_1);
        View root = inflater.inflate(R.layout.fragment_image, container, false);

        final ImageView image = root.findViewById(R.id.productImage);

        final ProgressBar progressBar = root.findViewById(R.id.progressBar);
        Picasso.with(getContext())
                .load(url)
                .error(getContext().getResources().getDrawable(R.drawable.placeholder))
                .into(image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //do smth when picture is loaded successfully
                        progressBar.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        //do smth when there is picture loading error
                        progressBar.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    }
                });
        return root;
    }

}
