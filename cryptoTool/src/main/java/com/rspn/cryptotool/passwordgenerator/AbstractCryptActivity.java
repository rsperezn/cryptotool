package com.rspn.cryptotool.passwordgenerator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.MainActivity;
import com.rspn.cryptotool.R;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.uihelper.DrawerAdapter;
import com.rspn.cryptotool.utils.CTUtils;

import java.util.ArrayList;

public abstract class AbstractCryptActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private int layoutViewId;
    private int layoutAddId;
    private int layoutDrawerId;
    private ListView navigationList;
    private Vibrator vibrator;
    protected AdView adView;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout navDrawerLayout;
    private boolean navDrawerOpen;
    private ArrayList<NavigationItem> navigationItems;
    private CharSequence drawerTitle;
    private CharSequence title;

    public AbstractCryptActivity(int layoutViewId, int layoutAdId, int layoutDrawerId) {
        this.layoutViewId = layoutViewId;
        this.layoutAddId = layoutAdId;
        this.layoutDrawerId = layoutDrawerId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutViewId);
        setAds();
        setNavigationDrawer();
        hideAdOnSoftKeyboardDisplay();

    }

    private void hideAdOnSoftKeyboardDisplay() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    protected void setNavigationDrawer() {
        navDrawerLayout = (DrawerLayout) findViewById(layoutDrawerId);
        navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        View header = getLayoutInflater().inflate(R.layout.header_navigation, null);
        ImageView iv = (ImageView) header.findViewById(R.id.header);
        iv.setImageResource(R.drawable.closed_lock_binary);
        navigationList = (ListView) findViewById(R.id.navigation_listInStrongPasswordActivity);
        navigationList.addHeaderView(header);
        navigationList.setOnItemClickListener(this);
        navigationItems = new ArrayList<>();
        navigationItems.add(new NavigationItem("Home", R.drawable.ic_home));
        navigationItems.add(new NavigationItem("Share", R.drawable.ic_share));
        DrawerAdapter adapter = new DrawerAdapter(this, navigationItems);
        navigationList.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = drawerTitle = getTitle();
        drawerToggle = new ActionBarDrawerToggle(this, navDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                navDrawerOpen = false;
            }

            public void onDrawerOpened(View drawerView) {
//                hideKeyboard(plainText_edit);
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                navDrawerOpen = true;
            }
        };

        navDrawerLayout.setDrawerListener(drawerToggle);

    }

    protected void setAds() {
        adView = (AdView) this.findViewById(layoutAddId);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    protected void startVibrate(View v) {
        if (CTUtils.vibrate) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
        }
    }

    protected void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NavigationItem navigationItem = ((NavigationItem) navigationList.getItemAtPosition(position));
        if (navigationItem == null) {
            return;
        }
        String selectedOption = navigationItem.getItemName();
        Intent intent = new Intent();

        switch (selectedOption) {
            case "Share":
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getSharableContent());
                startActivity(intent);
                break;
            case "Home":
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            default:
                drawerItemClick();
                break;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getSupportActionBar().setTitle(this.title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract void drawerItemClick();

    protected abstract String getSharableContent();

}