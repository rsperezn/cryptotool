package com.rspn.cryptotool.encrypt;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.MainActivity;
import com.rspn.cryptotool.R;
import com.rspn.cryptotool.breakencryption.BreakEncryptionActivity;
import com.rspn.cryptotool.decrypt.DecryptActivity;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.uihelper.CryptInfoDialogFragment;
import com.rspn.cryptotool.uihelper.DrawerAdapter;
import com.rspn.cryptotool.uihelper.HorizontalNumberPicker;
import com.rspn.cryptotool.uihelper.OpenDialogFragment;
import com.rspn.cryptotool.uihelper.SaveDialogFragment;
import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.utils.CTUtils.EType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class EncryptActivity extends ActionBarActivity implements OnItemSelectedListener, SaveDialogFragment.Communicator, OnItemClickListener {
    Spinner scheme_spinner;
    EditText keyword_edit;
    static EditText plainText_edit;
    static EditText encryptedText_edit;
    LinearLayout options_ll;
    EType encryptionType;
    Button run_encrypt_bt;
    CheckBox whitespaces_cb;
    TextView shift_tv;
    HorizontalNumberPicker horizontalNP;
    MenuItem saveMenuItem;
    boolean directActivity = true;

    DrawerLayout navDrawerLayout;
    ListView navList;
    List<NavigationItem> navItems;
    ActionBarDrawerToggle drawerToggle;
    CharSequence drawerTitle;
    CharSequence title;
    boolean drawerIconsAdded = false;
    Vibrator vibrator;
    AdView adView;
    boolean shouldShowShowSaveItem = false;
    boolean navDrawerOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        scheme_spinner = (Spinner) findViewById(R.id.spinner_InEncryptActivity);
        scheme_spinner.setOnItemSelectedListener(this);
        options_ll = (LinearLayout) findViewById(R.id.layout_options_InEncryptActivity);

        keyword_edit = new EditText(this);
        keyword_edit.setHint("Keyword");

        plainText_edit = (EditText) findViewById(R.id.edit_plainText);
        if (this.getIntent().hasExtra("data")) {
            plainText_edit.setText(this.getIntent().getStringExtra("data"));
            directActivity = false;
        }
        whitespaces_cb = (CheckBox) findViewById(R.id.checkBox_spaces_InEncryptActivity);

        encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InEncryptActivity);
        run_encrypt_bt = (Button) findViewById(R.id.run_encryption);
        run_encrypt_bt.setEnabled(false);

        navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layoutInEncryptActivity);
        navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        View header = getLayoutInflater().inflate(R.layout.header_navigation, null);
        ImageView iv = (ImageView) header.findViewById(R.id.header);
        iv.setImageResource(R.drawable.closed_lock_binary);
        navList = (ListView) findViewById(R.id.navigation_listInEncryptActivity);
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
                hideKeyboard(plainText_edit);
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                navDrawerOpen = true;
            }
        };

        navDrawerLayout.setDrawerListener(drawerToggle);

        shift_tv = new TextView(this);
        shift_tv.setText(R.string.label_switchBy);
        horizontalNP = new HorizontalNumberPicker(this);
        horizontalNP.setMax(25);

        addEditTextFilter();

        //Ads
        adView = (AdView) this.findViewById(R.id.adView_InEncryptActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void addEditTextFilter() {
        InputFilter filterKeyword = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        keyword_edit.setFilters(new InputFilter[]{filterKeyword});
    }

    private void updateNavigationDrawer() {


        if (drawerIconsAdded) {
            return;
        }

        if (!directActivity) {
            return;
        }
        //else
        navItems.add(new NavigationItem("Decrypt Output", R.drawable.ic_go_to_decryption));
        navItems.add(new NavigationItem("Break Encryption of Output", R.drawable.ic_go_to_break));
        navItems.add(new NavigationItem("Info", R.drawable.ic_info));

        drawerIconsAdded = true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!navDrawerOpen && shouldShowShowSaveItem)
            saveMenuItem.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crypt_operation, menu);
        saveMenuItem = menu.getItem(1);//save option
        saveMenuItem.setVisible(false);

        return true;
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
            OpenDialogFragment dialog = new OpenDialogFragment("PlainText");//plaintext samples so user can choose to encrypt one
            dialog.show(manager, "dialog");

        }
        if (id == R.id.action_save) {
            FragmentManager manager = getFragmentManager();
            SaveDialogFragment dialog = new SaveDialogFragment(CTUtils.EA);//plaintext samples so user can choose to encrypt one
            dialog.show(manager, "dialog");
        }

        if (id == R.id.clear_fields) {
            plainText_edit.setText("");
            encryptedText_edit.setText("");
            whitespaces_cb.setChecked(false);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {

        hideKeyboard(plainText_edit);

        if (position == 0) {
            run_encrypt_bt.setEnabled(false);
            options_ll.removeView(shift_tv);
            options_ll.removeView(horizontalNP);
            options_ll.removeView(keyword_edit);
            encryptionType = EType.NULL;
        } else if (position == 1) {//Caesar's
            run_encrypt_bt.setEnabled(true);
            options_ll.removeView(keyword_edit);
            options_ll.addView(shift_tv);
            options_ll.addView(horizontalNP);
            encryptionType = EType.CAESARS;
        } else {//Vigenere
            run_encrypt_bt.setEnabled(true);
            options_ll.removeView(shift_tv);
            options_ll.removeView(horizontalNP);
            options_ll.addView(keyword_edit);
            encryptionType = EType.VIGENERE;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void runEncryption(View v) {
        if (plainText_edit.getText().length() == 0) return;
        switch (encryptionType) {

            case VIGENERE:
                if (keyword_edit.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter a keyword", Toast.LENGTH_SHORT).show();
                    hideKeyboard(keyword_edit);
                    return;
                }
                hideKeyboard(plainText_edit);

                VigenereEncryption.initComponents();
                new MyEncryptionTask().execute(EType.VIGENERE);
                startVibrate(v);
                break;

            case CAESARS:
                hideKeyboard(plainText_edit);
                CaesarEncryption.initComponents();
                new MyEncryptionTask().execute(EType.CAESARS);
                startVibrate(v);
                break;
            case NULL:
                break;

            default:
                Toast.makeText(this, "Encryption Case error", Toast.LENGTH_LONG).show();
                break;
        }
        saveMenuItem.setVisible(true);//save button
        shouldShowShowSaveItem = true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NavigationItem navigationItem = ((NavigationItem) navList.getItemAtPosition(position));
        if (navigationItem == null) {
            return;
        }
        String selectedOption = navigationItem.getItemName();

        switch (selectedOption) {
            case "Decrypt Output": {
                Intent intent = new Intent(this, DecryptActivity.class);
                intent.putExtra("data", encryptedText_edit.getText().toString());
                startActivity(intent);
                break;
            }
            case "Break Encryption of Output": {
                Intent intent = new Intent(this, BreakEncryptionActivity.class);
                intent.putExtra("data", encryptedText_edit.getText().toString());
                startActivity(intent);
                break;
            }
            case "Share":
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, encryptedText_edit.getText().toString());
                startActivity(shareIntent);
                break;
            case "Info":
                if (encryptionType == EType.CAESARS) {
                    FragmentManager manager = getFragmentManager();
                    CryptInfoDialogFragment dialog = new CryptInfoDialogFragment("encryptCaesar");
                    dialog.show(manager, "dialog");
                } else {
                    FragmentManager manager = getFragmentManager();
                    CryptInfoDialogFragment dialog = new CryptInfoDialogFragment("encryptVigenere");
                    dialog.show(manager, "dialog");
                }
                break;
            case "Home": {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            }
        }
    }

    public void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public void onUserSelectedTextSample(String selection) {
        plainText_edit.setText("");
        plainText_edit.setText(selection);
    }

    public static List<String> getTextsToSave(boolean input, boolean output) {
        List<String> results = new ArrayList<>();
        if (input && !output) {
            results.add(plainText_edit.getText().toString());
        } else if (!input && output) {

            results.add(encryptedText_edit.getText().toString());
        } else {
            results.add(plainText_edit.getText().toString());
            results.add(encryptedText_edit.getText().toString());
        }

        return results;
    }

    @Override
    public void onDialogMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

    public void startVibrate(View v) {
        if (CTUtils.vibrate) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
        }
    }
    
	
	/*
     * ASYNC TASK
	 * */

    class MyEncryptionTask extends AsyncTask<EType, Void, String> {
        String plainText, keyword;
        int delta;
        EditText plainText_edit;
        EditText encryptedText_edit;
        CheckBox whitespaces_cb;

        @Override
        protected void onPreExecute() {
            plainText_edit = (EditText) findViewById(R.id.edit_plainText);
            plainText = plainText_edit.getText().toString();
            delta = horizontalNP.getValue();
            encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InEncryptActivity);
            whitespaces_cb = (CheckBox) findViewById(R.id.checkBox_spaces_InEncryptActivity);
            keyword = keyword_edit.getText().toString();
        }

        @Override
        protected String doInBackground(EType... params) {
            EType encryption = params[0];
            String result = null;
            switch (encryption) {
                case VIGENERE:
                    result = VigenereEncryption.runEncryption(plainText, keyword, whitespaces_cb.isChecked());
                    break;

                case CAESARS:
                    result = CaesarEncryption.runEncryption(plainText, delta, whitespaces_cb.isChecked());
                    break;

                case NULL:
                    break;

                default:
                    Toast.makeText(EncryptActivity.this, "Error case in AsyncTask", Toast.LENGTH_LONG).show();
                    break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            encryptedText_edit.setText(result);
            updateNavigationDrawer();
        }
    }
}
