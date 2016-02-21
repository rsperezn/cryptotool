package com.rspn.cryptotool.passwordgenerator;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
    CharSequence drawerTitle;
    CharSequence title;

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
        // TODO: 20/02/16 investigate what is this doing
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    protected abstract void onClickMainActionButton();

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

    public void startVibrate(View v) {
        if (CTUtils.vibrate) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
        }
    }

    protected void hideKeyboard(EditText et){
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
