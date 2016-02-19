package com.rspn.cryptotool.breakencryption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.MainActivity;
import com.rspn.cryptotool.R;
import com.rspn.cryptotool.StatsActivity;
import com.rspn.cryptotool.uihelper.CryptInfoDialogFragment;
import com.rspn.cryptotool.uihelper.DrawerAdapter;
import com.rspn.cryptotool.uihelper.OpenDialogFragment;
import com.rspn.cryptotool.uihelper.SaveDialogFragment;
import com.rspn.cryptotool.decrypt.CaesarsDecryption;
import com.rspn.cryptotool.decrypt.VigenereDecryption;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.model.StatsResults;
import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.utils.CTUtils.EType;
import com.rspn.cryptotool.utils.IC;

public class BreakEncryptionActivity extends ActionBarActivity implements SaveDialogFragment.Communicator, OnItemClickListener, OnItemSelectedListener {
    static EditText encryptedText_edit;
    static EditText brokenText_edit;
    EType encryptionType = EType.NULL;
    EType prevencryptionType = EType.NULL;
    MenuItem saveMenuItem;
    boolean directActivity = true;
    Spinner possibleKeys_sp;
    CheckBox whitespaces_cb;

    DrawerLayout navDrawerLayout;
    ListView navList;
    List<NavigationItem> navItems;
    ActionBarDrawerToggle drawerToggle;
    CharSequence drawerTitle;
    CharSequence title;
    Vibrator vibrator;
    AdView adView;

    ProgressDialog progressDialog;

    NavigationItem encryptOutput_navItem = new NavigationItem("Encrypt Output", R.drawable.ic_go_to_encryption);
    NavigationItem info_navItem = new NavigationItem("Info", R.drawable.ic_info);
    NavigationItem stats_navItem = new NavigationItem("Stats", R.drawable.ic_stastistics);

    boolean navDrawerOpen = false;
    boolean shouldShowSaveItem = false;
    StatsResults statsResults = new StatsResults();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakencryption);
        encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InBreakActivity);
        brokenText_edit = (EditText) findViewById(R.id.edit_brokenText);

        //if activity created via NavigationDrawer
        if (this.getIntent().hasExtra("data")) {
            encryptedText_edit.setText(this.getIntent().getStringExtra("data"));
            directActivity = false;
        }

        navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layoutInBreakActivity);
        //navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        View header = getLayoutInflater().inflate(R.layout.header_navigation, null);
        ImageView iv = (ImageView) header.findViewById(R.id.header);
        iv.setImageResource(R.drawable.broken_lock_binary);
        navList = (ListView) findViewById(R.id.navigation_listInBreakActivity);
        navList.addHeaderView(header);
        navList.setOnItemClickListener(this);
        navItems = new ArrayList<>();
        navItems.add(new NavigationItem("Home", R.drawable.ic_home));
        navItems.add(new NavigationItem("Share", R.drawable.ic_share));
        DrawerAdapter adapter = new DrawerAdapter(this, navItems);
        navList.setAdapter(adapter);
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
                hideKeyboard(encryptedText_edit);
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                navDrawerOpen = true;
            }
        };

        navDrawerLayout.setDrawerListener(drawerToggle);

        whitespaces_cb = (CheckBox) findViewById(R.id.checkBox_spaces_InBreakActivity);

        possibleKeys_sp = (Spinner) findViewById(R.id.spinner_PossibleKeys);
        possibleKeys_sp.setOnItemSelectedListener(this);
        possibleKeys_sp.setVisibility(View.INVISIBLE);

        navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layoutInBreakActivity);
        navList = (ListView) findViewById(R.id.navigation_listInBreakActivity);


        //Ads
        adView = (AdView) this.findViewById(R.id.adView_InBreakActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void onUserSelectedTextSample(String selection) {
        encryptedText_edit.setText("");
        encryptedText_edit.setText(selection);

    }

    private void updateNavigationDrawer() {

        if (prevencryptionType == encryptionType) {
            return;
        } else if (prevencryptionType == EType.NULL && encryptionType == EType.CAESARS) {
            if (directActivity)
                navItems.add(encryptOutput_navItem);
            navItems.add(info_navItem);
            navItems.add(stats_navItem);
        } else if (prevencryptionType == EType.NULL && encryptionType == EType.VIGENERE) {
            if (directActivity)
                navItems.add(encryptOutput_navItem);
            navItems.add(info_navItem);
            navItems.add(stats_navItem);
            //navItems.add(keys_navItem);
        }

    }

    //onClick
    public void runBreakEncryption(View v) {
        if (encryptedText_edit.getText().length() == 0) return;
        prevencryptionType = encryptionType;
        encryptionType = getEncryptionType(encryptedText_edit.getText().toString());
        MyEncryptionBreaker breaker = new MyEncryptionBreaker(this);
        breaker.execute(encryptionType);
        progressDialog = ProgressDialog.show(this, "Please Wait", "Breaking Encryption...");
        startVibrate(v);
        shouldShowSaveItem = true;
    }

    public CTUtils.EType getEncryptionType(String encryptedText) {
        encryptedText = CTUtils.sanitize(encryptedText);
        HashMap<String, Object> info = IC.calculte(encryptedText);
        statsResults.setCharCount((int[]) info.get("charCount"));
        float ic = (Float) info.get("IC");
        if (ic > CTUtils.EnglishIC) {//if the text is too short the IC will be high for Caesar encryption
            return CTUtils.EType.CAESARS;
        }
        if (ic < CTUtils.EnglishIC && ic > 0.059) {
            return CTUtils.EType.CAESARS;
        } else {
            return CTUtils.EType.VIGENERE;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!navDrawerOpen && shouldShowSaveItem)
            saveMenuItem.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crypt_operation, menu);
        saveMenuItem = menu.getItem(1);
        saveMenuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_open) {
            FragmentManager manager = getFragmentManager();
            OpenDialogFragment dialog = new OpenDialogFragment("EncryptedText");//encrypted samples so user can choose to encrypt one
            dialog.show(manager, "dialog");
        }
        if (id == R.id.action_save) {
            FragmentManager manager = getFragmentManager();
            SaveDialogFragment dialog = new SaveDialogFragment(CTUtils.BEA);//plaintext samples so user can choose to encrypt one
            dialog.show(manager, "dialog");
        }

        if (id == R.id.clear_fields) {
            encryptedText_edit.setText((""));
            brokenText_edit.setText("");
            whitespaces_cb.setChecked(false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NavigationItem navigationItem = ((NavigationItem) navList.getItemAtPosition(position));
        if (navigationItem == null) {
            return;
        }
        String selectedOption = navigationItem.getItemName();

        if (selectedOption.equals("Home")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (selectedOption.equals("Encrypt Output")) {
            Intent intent = new Intent(this, EncryptActivity.class);
            intent.putExtra("data", brokenText_edit.getText().toString());
            startActivity(intent);
        } else if (selectedOption.equals("Stats")) {
            statsResults.setResultType(encryptionType.name());
            statsResults.setICforKeyLength(VigenereBreaker.getICs());
            Intent intent = new Intent(this, StatsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("data", statsResults);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (selectedOption.equals("Keys")) {
//			FragmentManager manager= getFragmentManager();
//			CryptInfoDialogFragment dialog= new CryptInfoDialogFragment("asdf");//plaintext samples so user can choose to encrypt one 
//			dialog.show(manager, "dialog");	
        } else if (selectedOption.equals("Info")) {
            if (encryptionType == EType.VIGENERE) {
                FragmentManager manager = getFragmentManager();
                CryptInfoDialogFragment dialog = new CryptInfoDialogFragment("breakVigenere");
                dialog.show(manager, "dialog");
            } else {
                FragmentManager manager = getFragmentManager();
                CryptInfoDialogFragment dialog = new CryptInfoDialogFragment("breakCaesar");
                dialog.show(manager, "dialog");
            }
        } else if (selectedOption.equals("Share")) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, brokenText_edit.getText().toString());
            startActivity(shareIntent);
        }

        //String selectedKey = possibleKeys_sp.getItemAtPosition(position).toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedKey = possibleKeys_sp.getSelectedItem().toString();
        brokenText_edit.setText("");
        VigenereDecryption.initComponents();
        brokenText_edit.setText(VigenereDecryption.runDecryption(encryptedText_edit.getText().toString(), selectedKey, whitespaces_cb.isChecked()));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDialogMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public static List<String> getTextsToSave(boolean input, boolean output) {
        List<String> results = new ArrayList<>();
        if (input && !output) {
            results.add(encryptedText_edit.getText().toString());
        } else if (!input && output) {

            results.add(brokenText_edit.getText().toString());
        } else {
            results.add(encryptedText_edit.getText().toString());
            results.add(brokenText_edit.getText().toString());
        }

        return results;
    }

    public void startVibrate(View v) {
        if (CTUtils.vibrate) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
        }
    }

    public void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    ///Async Task
    class MyEncryptionBreaker extends AsyncTask<EType, Integer, Void> {
        EditText encryptedText_edit;
        EditText brokenText_edit;// represents the broken encryption edit text
        String encryptedText;
        CheckBox whitespcaces_cb;
        EType encryption;
        Activity activity;
        public HashMap<String, Object> resultsInfo;


        public MyEncryptionBreaker(BreakEncryptionActivity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminate(false);
            setProgressBarVisibility(true);
            encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InBreakActivity);
            brokenText_edit = (EditText) findViewById(R.id.edit_brokenText);
            encryptedText = encryptedText_edit.getText().toString();
            whitespcaces_cb = (CheckBox) findViewById(R.id.checkBox_spaces_InBreakActivity);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(EType... params) {
            publishProgress(30);
            encryption = params[0];
            switch (encryption) {
                case CAESARS:
                    CaesarsDecryption.initComponents();
                    publishProgress(50);
                    resultsInfo = CaesarBreaker.runBreak(encryptedText, whitespcaces_cb.isChecked());
                    publishProgress(80);
                    break;
                case VIGENERE:
                    publishProgress(50);
                    resultsInfo = VigenereBreaker.runBreak(encryptedText, false);
                    List<String> forcedKeys = (List<String>) resultsInfo.get("forcedKeys");

                    if (forcedKeys.isEmpty())//was not able to generate keys with Viginere so force to break with Caesars
                    {
                        publishProgress(30);

                        encryptionType = EType.CAESARS;
                        doInBackground(new EType[]{EType.CAESARS});

                    }
                    publishProgress(80);
                    break;

                default:
                    break;
            }

            publishProgress(90);
            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            setProgress(value[0] * 1000);
        }

        @Override
        protected void onPostExecute(Void nothing) {
            setProgressBarVisibility(false);
            TextView tv = (TextView) findViewById(R.id.textview_ResultTitle);
            if (encryptionType == EType.CAESARS) {
                ((BreakEncryptionActivity) activity).possibleKeys_sp.setVisibility(View.INVISIBLE);
                int shifted = (Integer) resultsInfo.get("shift");
                String decryptedText = (String) resultsInfo.get("decryptedText");
                brokenText_edit.setText(decryptedText);
                tv.setText("Caesar's encryption shift:" + shifted);
            } else if (encryptionType == EType.VIGENERE) {

                ((BreakEncryptionActivity) activity).possibleKeys_sp.setVisibility(View.VISIBLE);
                @SuppressWarnings("unchecked")
                List<String> possibleKeys = (List<String>) resultsInfo.get("forcedKeys");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, possibleKeys);
                possibleKeys_sp.setAdapter(adapter);
                tv.setText("Vigenere encryption possible keys:");
            }

            saveMenuItem.setVisible(true);
            //update navigation drawer
            updateNavigationDrawer();
            progressDialog.dismiss();

        }

    }


}
