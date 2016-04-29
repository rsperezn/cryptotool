package com.rspn.cryptotool;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.passwordgenerator.PronounceablePasswordActivity;
import com.rspn.cryptotool.uihelper.DrawerAdapter;
import com.rspn.cryptotool.utils.CTUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCryptActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String SHARE = "Share";
    private final String HOME = "Home";
    private int layoutViewId;
    private int layoutAddId;
    private int layoutDrawerId;
    private int navigationListDrawerId;
    private int navigationDrawerIcon;
    protected ListView navigationList;
    protected AdView adView;
    protected ActionBarDrawerToggle drawerToggle;
    private boolean navDrawerOpen;
    protected ArrayList<NavigationItem> navigationItems;
    private CharSequence drawerTitle;
    private CharSequence title;
    protected String errorMessage;

    public AbstractCryptActivity(int layoutViewId,
                                 int layoutAdId,
                                 int layoutDrawerId,
                                 int navigationListDrawerId,
                                 int navigationDrawerIcon) {
        this.layoutViewId = layoutViewId;
        this.layoutAddId = layoutAdId;
        this.layoutDrawerId = layoutDrawerId;
        this.navigationListDrawerId = navigationListDrawerId;
        this.navigationDrawerIcon = navigationDrawerIcon;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(layoutViewId);
        setAds();
        setNavigationDrawer();
        hideAdOnSoftKeyboardDisplay();
        findAndSetViews();
        setOnClickListener();
        setDataFromOriginatingActivity();
    }

    private void hideAdOnSoftKeyboardDisplay() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    protected void setNavigationDrawer() {
        DrawerLayout navDrawerLayout = (DrawerLayout) findViewById(layoutDrawerId);
        navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        View header = getLayoutInflater().inflate(R.layout.header_navigation, null);
        ImageView imageView = (ImageView) header.findViewById(R.id.header);
        imageView.setImageResource(navigationDrawerIcon);
        navigationList = (ListView) findViewById(navigationListDrawerId);
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
                hideKeyboard(drawerView);
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

    protected void vibrate() {
        vibrate(50);
    }

    protected void vibrate(int length) {
        if (CTUtils.vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(length);
        }
    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void displayToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void copyToClipboard(String textToCopy) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(textToCopy);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copy of data", textToCopy);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    protected boolean isNavigationDrawerClosed() {
        return !navDrawerOpen;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (this instanceof PronounceablePasswordActivity && isNotListItemFromMenu(view)) {
            listItemClick(position);
        } else {
            NavigationItem navigationItem = (NavigationItem) navigationList.getItemAtPosition(position);

            String selectedOption = navigationItem.getItemName();
            Intent intent = new Intent();

            switch (selectedOption) {
                case SHARE:
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, getSharableContent());
                    startActivity(intent);
                    break;
                case HOME:
                    intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                default:
                    listItemClick(position);
                    break;
            }
        }
    }

    private boolean isNotListItemFromMenu(View view) {
        List<Integer> idsOfNonNavigationDrawerListItems = Collections.singletonList(R.id.list_PronounceablePasswords);
        return idsOfNonNavigationDrawerListItems.contains(((View) view.getParent()).getId());
    }

    protected void setKeywordTextFilter(EditText editText) {
        InputFilter filterKeyword = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i))) {
                        return source.subSequence(0, i);
                    }
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filterKeyword});
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

    protected abstract void findAndSetViews();

    protected abstract void listItemClick(int position);

    protected abstract String getSharableContent();

    protected abstract boolean satisfiedMainButtonPreconditions();

    protected abstract void setOnClickListener();

    protected abstract void setDataFromOriginatingActivity();

}