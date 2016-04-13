package com.rspn.cryptotool;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.uihelper.CryptExpandableListAdapter;
import com.rspn.cryptotool.db.TextSamplesDataSource;
import com.rspn.cryptotool.model.CryptGroup;
import com.rspn.cryptotool.model.Text;
import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.xml.TextSamplesJDOMParser;

import java.util.List;

import static com.rspn.cryptotool.uihelper.CryptExpandableListAdapter.*;

public class MainActivity extends ActionBarActivity {

    private SharedPreferences prefs;
    private boolean retainCase;
    private boolean vibrate;
    private TextSamplesDataSource dataSource;
    private AdView adView;
    // more efficient than HashMap for mapping integers to objects
    private SparseArray<CryptGroup> groups = new SparseArray<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createExpandableListGroups();
        //Initialized CTUtils
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

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.cryptotool_list);
        CryptExpandableListAdapter adapter = new CryptExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);
    }

    public void createExpandableListGroups() {
        CryptGroup classicalCrypto = new CryptGroup("Classical Cipher Tool");
        classicalCrypto.children.add(ENCRYPT);
        classicalCrypto.children.add(DECRYPT);
        classicalCrypto.children.add(BREAK_ENCRYPTION);

        CryptGroup calculateHashes = new CryptGroup("Hash Generator");
        calculateHashes.children.add(FILE);
        calculateHashes.children.add(TEXT);

        CryptGroup strongPassword = new CryptGroup("Password Generator");
        strongPassword.children.add(STRONG_PASSWORD);

        groups.append(0, classicalCrypto);
        groups.append(1, calculateHashes);
        groups.append(2, strongPassword);

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
