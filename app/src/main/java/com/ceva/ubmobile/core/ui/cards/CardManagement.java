package com.ceva.ubmobile.core.ui.cards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.CardPagerAdapter;
import com.ceva.ubmobile.core.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class CardManagement extends BaseActivity {
    CardPagerAdapter pageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardmanagement);
        setToolbarTitle("Card information");
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setTitle("Card information");
        //TextView text = (TextView) findViewById(R.id.main_toolbar_title);
        // text.setTypeface(gotham);
        //text.setText(Utility.camelCase("Card information"));
        List<Fragment> fragments = getFragments();

        pageAdapter = new CardPagerAdapter(getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);

        pager.setAdapter(pageAdapter);
        pager.setClipToPadding(false);
        pager.setPadding(30, 0, 30, 0);//left,0,right,0
        pager.setPageMargin(0);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_new_card:
                startActivity(new Intent(this, CardRequest.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<Fragment> getFragments() {

        List<Fragment> fList = new ArrayList<Fragment>();

        fList.add(CardFragment.newInstance("Fragment 1"));

        fList.add(CardFragment.newInstance("Fragment 2"));

        fList.add(CardFragment.newInstance("Fragment 3"));

        return fList;

    }
}
