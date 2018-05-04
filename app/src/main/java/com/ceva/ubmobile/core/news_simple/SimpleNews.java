package com.ceva.ubmobile.core.news_simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.RecyclerItemClickListener;
import com.ceva.ubmobile.adapter.SimpleNewsAdapter;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.DashBoard;
import com.ceva.ubmobile.core.ui.LandingPage;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.NewsModel;
import com.ceva.ubmobile.network.RssService;
import com.ceva.ubmobile.security.UBNSession;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.toptas.rssconverter.RssConverterFactory;
import me.toptas.rssconverter.RssFeed;
import me.toptas.rssconverter.RssItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SimpleNews extends BaseActivity {

    UBNSession session;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_news);
        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/
        setToolbarTitle("News on the Go");
        session = new UBNSession(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //new PostTask().execute("https://news.google.com.ng/news?cf=all&hl=en&pz=1&ned=en_ng&topic=b&output=rss");
        List<NewsModel> newsModels = new ArrayList<>();
        newsModels.add(new NewsModel("UBN news", "MediaAndNewsUnionBank", R.drawable.ubn_logo, "https://feeds.feedburner.com"));
        newsModels.add(new NewsModel("Naija", "news?cf=all&hl=en&pz=1&ned=en_ng&topic=b&output=rss", R.drawable.flag_of_nigeria, "https://news.google.com.ng"));
        newsModels.add(new NewsModel("World", "rss/edition_world.rss", R.drawable.logo_cnn, "http://rss.cnn.com"));
        newsModels.add(new NewsModel("Fashion", "news?cf=all&hl=en&pz=1&ned=en_ng&q=fashion&output=rss", R.drawable.union_girl_full, "https://news.google.com.ng"));
        newsModels.add(new NewsModel("Sports", "news?cf=all&hl=en&pz=1&ned=en_ng&topic=s&output=rss", R.drawable.sports, "https://news.google.com.ng"));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), newsModels);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(mViewPager);

        if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
            Log.debug("LOGIN STATUS", " LOGGED IN");
        } else {
            Log.debug("LOGIN STATUS", " LOGGED OUT");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            Log.debug("home pressed");
            if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
                startActivity(new Intent(this, DashBoard.class));
            } else {
                startActivity(new Intent(this, LandingPage.class));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_DEF = "section_number";
        private static final String ARG_TITLE = "title";
        private static final String ARG_URL = "url";
        private static final String ARG_HOST = "hostname";
        @BindView(R.id.newslist)
        RecyclerView newsRecyclerView;
        //@BindView(R.id.smooth_progress)
        //SmoothProgressBar smoothProgressBar;
        @BindView(R.id.swiperefresh)
        SwipeRefreshLayout swipeRefreshLayout;
        String title = null;
        String url = null;
        int defaultUrl;
        String hostname = null;
        List<RssItem> rssItemList = new ArrayList<>();
        SimpleNewsAdapter mAdapter;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String title, String url, int defaultUrl, String hostname) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_TITLE, title);
            args.putString(ARG_URL, url);
            args.putInt(ARG_DEF, defaultUrl);
            args.putString(ARG_HOST, hostname);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                title = getArguments().getString(ARG_TITLE);
                url = getArguments().getString(ARG_URL);
                defaultUrl = getArguments().getInt(ARG_DEF);
                hostname = getArguments().getString(ARG_HOST);
                Log.debug("hostname", hostname);
                Log.debug("defaulturl", url);
            }
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_simple_news, container, false);
            ButterKnife.bind(this, rootView);
            // smoothProgressBar = (SmoothProgressBar) rootView.findViewById(R.id.smooth_progress);
            //smoothProgressBar.setVisibility(View.VISIBLE);
            fetchNews();

            mAdapter = new SimpleNewsAdapter(rssItemList, getContext(), defaultUrl);
            //swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            newsRecyclerView.setLayoutManager(mLayoutManager);
            newsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            newsRecyclerView.setAdapter(mAdapter);
            newsRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getContext(), NewsDetails.class);
                            intent.putExtra("url", rssItemList.get(position).getLink());
                            startActivity(intent);
                        }
                    }
                    ));
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    fetchNews();
                }
            });

            fetchNews();

            return rootView;
        }

        private void fetchNews() {
            //smoothProgressBar.setVisibility(View.VISIBLE);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(hostname)
                    .addConverterFactory(RssConverterFactory.create())
                    .build();

            RssService service = retrofit.create(RssService.class);
            service.getRss(url)
                    .enqueue(new Callback<RssFeed>() {
                        @Override
                        public void onResponse(Call<RssFeed> call, Response<RssFeed> response) {
                            // Populate list with response.body().getItems()
                            if (response.body().getItems() != null) {
                                rssItemList = response.body().getItems();

                            }
                            List<RssItem> revisedList = new ArrayList<RssItem>();

                            if (title.toLowerCase().contains("ubn")) {
                                revisedList.addAll(rssItemList);
                            } else {
                                for (int i = 0; i < rssItemList.size(); i++) {
                                    RssItem news = rssItemList.get(i);
                                    if (!news.getDescription().toLowerCase().contains("ubn") || !news.getDescription().toLowerCase().contains("union bank") || !news.getTitle().toLowerCase().contains("ubn") || !news.getTitle().toLowerCase().contains("union bank")) {
                                        revisedList.add(news);
                                    }
                                }
                            }
                            //smoothProgressBar.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                            newsRecyclerView.invalidate();
                            mAdapter = new SimpleNewsAdapter(revisedList, getContext(), defaultUrl);
                            newsRecyclerView.setAdapter(mAdapter);
                        }

                        @Override
                        public void onFailure(Call<RssFeed> call, Throwable t) {
                            // Show failure message
                        }
                    });
        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<NewsModel> newsModelList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm, List<NewsModel> newsModelList) {
            super(fm);
            this.newsModelList = newsModelList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            NewsModel model = newsModelList.get(position);

            return PlaceholderFragment.newInstance(model.getTitle(), model.getUrl(), model.getLogo(), model.getHostname());
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return newsModelList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return newsModelList.get(position).getTitle();
        }
    }
}
