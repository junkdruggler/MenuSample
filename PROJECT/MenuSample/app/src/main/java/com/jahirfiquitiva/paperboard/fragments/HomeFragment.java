package com.jahirfiquitiva.paperboard.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jahirfiquitiva.paperboard.activities.MainActivity;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import com.junkdruggler.sample.R;

public class HomeFragment extends Fragment {

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";

    private String PlayStoreDevAccount, PlayStoreListing, AppOnePackage, AppTwoPackage, AppThreePackage;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.section_home, container, false);

        PlayStoreDevAccount = getResources().getString(R.string.play_store_dev_link);
        PlayStoreListing = getActivity().getPackageName();
        AppOnePackage = getResources().getString(R.string.app_one_package);
        AppTwoPackage = getResources().getString(R.string.app_two_package);
        AppThreePackage = getResources().getString(R.string.app_three_package);

        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (toolbar != null)
            toolbar.setTitle(R.string.app_name);

        ObservableScrollView content = (ObservableScrollView) root.findViewById(R.id.HomeContent);

        //Cards
        CardView cardone = (CardView) root.findViewById(R.id.cardOne);
        CardView cardtwo = (CardView) root.findViewById(R.id.cardTwo);
        CardView cardthree = (CardView) root.findViewById(R.id.cardThree);
        if (AppIsInstalled(AppOnePackage)) {
            cardone.setVisibility((cardone.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        }
        if (AppIsInstalled(AppTwoPackage)) {
            cardtwo.setVisibility((cardtwo.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        }
        if (AppIsInstalled(AppThreePackage)) {
            cardthree.setVisibility((cardthree.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        }


        TextView playbtn = (TextView) root.findViewById(R.id.like_button);
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent devPlay = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/159489687428141"));
                    startActivity(devPlay);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Halyards-Restaurant-159489687428141")));
                }

            }
        });

        TextView apponebtn = (TextView) root.findViewById(R.id.review_button);
        apponebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appone = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tripadvisor.com/Restaurant_Review-g35241-d1148126-Reviews-Halyards-Saint_Simons_Island_Golden_Isles_of_Georgia_Georgia.html"));
                startActivity(appone);
            }
        });

        TextView apptwobtn = (TextView) root.findViewById(R.id.reserve_button);
        apptwobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent apptwo = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.opentable.com/halyards"));
                startActivity(apptwo);
            }
        });

        TextView appthreebtn = (TextView) root.findViewById(R.id.learn_button);
        appthreebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appthree = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + AppThreePackage));
                startActivity(appthree);
            }
        });

        TextView ratebtn = (TextView) root.findViewById(R.id.learn_button);
        ratebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + PlayStoreListing));
                startActivity(rate);
            }
        });



        return root;
    }

    private boolean AppIsInstalled(String packageName) {
        final PackageManager pm = getActivity().getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

}
