package com.rspn.cryptotool;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.db.TextSamplesDataSource;
import com.rspn.cryptotool.model.CryptCategory;
import com.rspn.cryptotool.model.Text;
import com.rspn.cryptotool.uihelper.CryptRecyclerViewAdapter;
import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.xml.TextSamplesJDOMParser;

import java.util.ArrayList;
import java.util.List;

import static com.rspn.cryptotool.uihelper.CryptRecyclerViewAdapter.BREAK_ENCRYPTION;
import static com.rspn.cryptotool.uihelper.CryptRecyclerViewAdapter.DECRYPT;
import static com.rspn.cryptotool.uihelper.CryptRecyclerViewAdapter.ENCRYPT;
import static com.rspn.cryptotool.uihelper.CryptRecyclerViewAdapter.FILE;
import static com.rspn.cryptotool.uihelper.CryptRecyclerViewAdapter.PRONOUNCEABLE_PASSWORD;
import static com.rspn.cryptotool.uihelper.CryptRecyclerViewAdapter.STRONG_PASSWORD;
import static com.rspn.cryptotool.uihelper.CryptRecyclerViewAdapter.TEXT;
import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    private static final String TITLE = "  CryptoTool";
    private SharedPreferences prefs;
    private boolean retainCase;
    private boolean vibrate;
    private TextSamplesDataSource dataSource;
    private AdView adView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CTUtils.initialize();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        CTUtils.retainCase = prefs.getBoolean("pref_letterCase", false);
        OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                retainCase = prefs.getBoolean("pref_letterCase", false);
                vibrate = prefs.getBoolean("pref_vibrate", false);
                CTUtils.retainCase = retainCase;
                CTUtils.vibrate = vibrate;
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);
        setContentView(R.layout.activity_main);
        displayLogoInActionBar();

        dataSource = new TextSamplesDataSource(this);
        dataSource.open();
        //if first time opening the application create the data
        if (dataSource.findAll().size() == 0) {
            createData();
        }

        //Ads
        adView = (AdView) this.findViewById(R.id.adView_InMainActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        CTUtils.windowWidth = size.x;
        CTUtils.windowHeight = size.y;

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CryptRecyclerViewAdapter(getDataSet(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<CryptCategory> getDataSet() {
        List<CryptCategory> results = new ArrayList<>();
        CryptCategory classicalTool = new CryptCategory(CTUtils.CLASSICAL_CIPHER_TOOL,
                asList(ENCRYPT, DECRYPT, BREAK_ENCRYPTION));
        CryptCategory hashesGenerator = new CryptCategory(CTUtils.HASH_GENERATOR,
                asList(FILE, TEXT));
        CryptCategory passwordGenerator = new CryptCategory(CTUtils.PASSWORD_GENERATOR,
                asList(STRONG_PASSWORD, PRONOUNCEABLE_PASSWORD));

        results.add(classicalTool);
        results.add(hashesGenerator);
        results.add(passwordGenerator);

        return results;
    }

    private void displayLogoInActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(TITLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks appcompat v7 when creating android app. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_preferences:
                Intent preferenceIntent = new Intent(this, PreferencesActivity.class);
                startActivity(preferenceIntent);
                break;
            case R.id.action_rateApp:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                break;

            case R.id.action_manageSaved:
                Intent savedManagerIntent = new Intent(this, SavedTextExplorerActivity.class);
                startActivity(savedManagerIntent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void createData() {//from XML file
        TextSamplesJDOMParser parser = new TextSamplesJDOMParser();
        List<Text> textSamples = parser.parseXML(this);
        for (Text textSample : textSamples) {
            dataSource.create(textSample);
        }
    }
}
