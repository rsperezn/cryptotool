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
import android.view.View;
import android.widget.ExpandableListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.UIHelper.CryptExpandableListAdapter;
import com.rspn.cryptotool.breakencryption.BreakEncryptionActivity;
import com.rspn.cryptotool.calculatehashes.CalculateHashesActivity;
import com.rspn.cryptotool.db.TextSamplesDataSource;
import com.rspn.cryptotool.decrypt.DecryptActivity;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.CryptGroup;
import com.rspn.cryptotool.model.Text;
import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.xml.TextSamplesJDOMParser;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    SharedPreferences prefs;
    boolean retainCase;
    boolean vibrate;
    OnSharedPreferenceChangeListener listener;
    TextSamplesDataSource datasource;
    AdView adView;
    // more efficient than HashMap for mapping integers to objects
    SparseArray<CryptGroup> groups = new SparseArray<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createExpandableListGroups();
        //Initialized CTUtils
        CTUtils.initialize();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        CTUtils.retainCase = prefs.getBoolean("pref_letterCase", false);
        listener = new OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                retainCase = prefs.getBoolean("pref_letterCase", false);
                vibrate = prefs.getBoolean("pref_vibrate", false);
                CTUtils.retainCase = retainCase;
                CTUtils.vibrate = vibrate;
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);
        setContentView(R.layout.activity_mainv2);

        datasource = new TextSamplesDataSource(this);
        datasource.open();
        //if first time opening the application create the data
        if (datasource.findAll().size() == 0) {
            createData();
        }

        //Ads
        adView = (AdView) this.findViewById(R.id.adView_InMainActivity_v2);
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
        CryptGroup clasicalCrypto = new CryptGroup("Classical Cryptography");
        clasicalCrypto.children.add("Encrypt");
        clasicalCrypto.children.add("Decrypt");
        clasicalCrypto.children.add("Break Encryption");

        CryptGroup calculateHashes = new CryptGroup("Calculate Hashes");
        calculateHashes.children.add("File");
        calculateHashes.children.add("Text");

        CryptGroup strongPassword = new CryptGroup("Password Generator");
        strongPassword.children.add("Strong Password");

        groups.append(0, clasicalCrypto);
        groups.append(1, calculateHashes);
        groups.append(2, strongPassword);

    }

	/*

		prefs= PreferenceManager.getDefaultSharedPreferences(this);
		CTUtils.retainCase= prefs.getBoolean("pref_letterCase", false);
		listener= new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
				retainCase= prefs.getBoolean("pref_letterCase", false);
				vibrate = prefs.getBoolean("pref_vibrate", false);
				CTUtils.retainCase=retainCase;
				CTUtils.vibrate= vibrate;
			}
		};

		prefs.registerOnSharedPreferenceChangeListener(listener);
		setContentView(R.layout.activity_main);


		datasource= new TextSamplesDataSource(this);
		datasource.open();
		//if first time opening the application create the data
		if (datasource.findAll().size()==0){
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

	}
	*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks herappcompat v7 when creating android appe. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_preferences:
                Intent prefintent = new Intent(this, PreferencesActivity.class);
                startActivity(prefintent);
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

            //case R.id.action_about:
            //return true;
            //break;
            case R.id.action_manageSaved:
                Intent savedmanagerintent = new Intent(this, SavedTextExplorerActivity.class);
                startActivity(savedmanagerintent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //onclick buttons
    public void encrypt(View v) {
        Intent intent = new Intent(this, EncryptActivity.class);
        startActivity(intent);
    }

    public void decrypt(View v) {
        Intent intent = new Intent(this, DecryptActivity.class);
        startActivity(intent);

    }

    public void breakEncryption(View v) {
        Intent intent = new Intent(this, BreakEncryptionActivity.class);
        startActivity(intent);
    }

    public void calculateHashes(View v) {
        try {
            //PasswordGenerator.generatePassword(8,true,true,true,true,true);
        } catch (Exception e) {


        }
        Intent intent = new Intent(this, CalculateHashesActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        datasource.open();
        if (adView != null) {
            adView.resume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        datasource.close();
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
            datasource.create(textSample);
        }
    }
}
