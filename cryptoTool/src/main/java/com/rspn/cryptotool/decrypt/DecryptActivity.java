package com.rspn.cryptotool.decrypt;

import java.util.ArrayList;
import java.util.List;

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
import com.rspn.cryptotool.uihelper.CryptInfoDialogFragment;
import com.rspn.cryptotool.uihelper.DrawerAdapter;
import com.rspn.cryptotool.uihelper.HorizontalNumberPicker;
import com.rspn.cryptotool.uihelper.OpenDialogFragment;
import com.rspn.cryptotool.uihelper.SaveDialogFragment;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.utils.CTUtils.EType;

@SuppressWarnings("deprecation")
public class DecryptActivity extends ActionBarActivity implements OnItemSelectedListener, SaveDialogFragment.Communicator, OnItemClickListener {
    /*global variables*/
    Spinner scheme_spinner;
    EditText keyword_edit;
    static EditText encryptedText_edit;
    static EditText decryptedText_edit;
    LinearLayout options_ll;
    EType decryptionType;
    Button run_decrypt_bt;
    TextView shift_tv;
    HorizontalNumberPicker horizontalNP;
    MenuItem saveMenuItem;
    boolean directActivity = true;
    CheckBox whitespaces_cb;
    DrawerLayout navDrawerLayout;
    ListView navList;
    List<NavigationItem> navItems;
    ActionBarDrawerToggle drawerToggle;
    CharSequence drawerTitle;
    CharSequence title;
    boolean drawerIconsAdded = false;
    Vibrator vibrator;
    AdView adView;
    boolean shouldShowSaveItem = false;
    boolean navDrawerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);

        scheme_spinner = (Spinner) findViewById(R.id.spinner_InDecryptActivity);
        scheme_spinner.setOnItemSelectedListener(this);
        options_ll = (LinearLayout) findViewById(R.id.layout_options);

        keyword_edit = new EditText(this);
        keyword_edit.setHint("Keyword");
        encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InDecryptActivity);

        //if activity created via SlidingDrawer
        if (this.getIntent().hasExtra("data")) {
            encryptedText_edit.setText(this.getIntent().getStringExtra("data"));
            directActivity = false;
        }
        whitespaces_cb = (CheckBox) findViewById(R.id.checkBox_spaces_InDecryptActivity);

        decryptedText_edit = (EditText) findViewById(R.id.edit_decryptedText);

        run_decrypt_bt = (Button) findViewById(R.id.run_decryption);
        run_decrypt_bt.setEnabled(false);

        navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layoutInDecryptActivity);
        navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        View header = getLayoutInflater().inflate(R.layout.header_navigation, null);
        ImageView iv = (ImageView) header.findViewById(R.id.header);
        iv.setImageResource(R.drawable.open_lock_binary);
        navList = (ListView) findViewById(R.id.navigation_listInDecryptActivity);
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


        shift_tv = new TextView(this);
        shift_tv.setText("shift by");
        horizontalNP = new HorizontalNumberPicker(this);
        horizontalNP.setMax(25);

        addEditTextFilter();


        //Ads
        adView = (AdView) this.findViewById(R.id.adView_InDecryptActivity);
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
        navItems.add(new NavigationItem("Encrypt Output", R.drawable.ic_go_to_encryption));
        navItems.add(new NavigationItem("Info", R.drawable.ic_info));

        drawerIconsAdded = true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!navDrawerOpen && shouldShowSaveItem)
            saveMenuItem.setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crypt_operation, menu);
        saveMenuItem = menu.getItem(1);
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
            OpenDialogFragment dialog = new OpenDialogFragment("EncryptedText");//encrypted samples so user can choose to encrypt one
            dialog.show(manager, "dialog");
        }
        if (id == R.id.action_save) {
            FragmentManager manager = getFragmentManager();
            SaveDialogFragment dialog = new SaveDialogFragment(CTUtils.DA);//plaintext samples so user can choose to encrypt one
            dialog.show(manager, "dialog");
        }

        if (id == R.id.clear_fields) {
            encryptedText_edit.setText((""));
            decryptedText_edit.setText("");
            whitespaces_cb.setChecked(false);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        hideKeyboard(encryptedText_edit);

        if (position == 0) {//selecting nothing
            run_decrypt_bt.setEnabled(false);
            options_ll.removeView(shift_tv);
            options_ll.removeView(horizontalNP);
            options_ll.removeView(keyword_edit);
            decryptionType = EType.NULL;
        } else if (position == 1) {//Caesar's
            run_decrypt_bt.setEnabled(true);
            options_ll.removeView(keyword_edit);
            options_ll.addView(shift_tv);
            options_ll.addView(horizontalNP);
            decryptionType = EType.CAESARS;
        } else {//Vigenere
            run_decrypt_bt.setEnabled(true);
            options_ll.removeView(shift_tv);
            options_ll.removeView(horizontalNP);
            options_ll.addView(keyword_edit);
            decryptionType = EType.VIGENERE;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NavigationItem navigationItem = ((NavigationItem) navList.getItemAtPosition(position));
        if (navigationItem == null) {
            return;
        }
        String selectedOption = navigationItem.getItemName();

        if (selectedOption.equals("Encrypt Output")) {
            Intent intent = new Intent(this, EncryptActivity.class);
            intent.putExtra("data", decryptedText_edit.getText().toString());
            startActivity(intent);
        } else if (selectedOption.equals("Share")) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, decryptedText_edit.getText().toString());
            startActivity(shareIntent);
        } else if (selectedOption.equals("Info")) {
            if (decryptionType == EType.CAESARS) {
                FragmentManager manager = getFragmentManager();
                CryptInfoDialogFragment dialog = new CryptInfoDialogFragment("decryptCaesar");
                dialog.show(manager, "dialog");
            } else {
                FragmentManager manager = getFragmentManager();
                CryptInfoDialogFragment dialog = new CryptInfoDialogFragment("decryptVigenere");
                dialog.show(manager, "dialog");
            }
        } else if (selectedOption.equals("Home")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void runDecryption(View v) {
        if (encryptedText_edit.getText().length() == 0) return;
        switch (decryptionType) {
            case VIGENERE:
                if (keyword_edit.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter a keyword", Toast.LENGTH_SHORT).show();
                    hideKeyboard(encryptedText_edit);
                    return;
                }
                hideKeyboard(encryptedText_edit);

                VigenereDecryption.initComponents();
                new MyDecryptionTask().execute(EType.VIGENERE);
                startVibrate(v);
                break;
            case CAESARS:
                hideKeyboard(encryptedText_edit);
                CaesarsDecryption.initComponents();
                new MyDecryptionTask().execute(EType.CAESARS);
                startVibrate(v);
                break;
            default:
                Toast.makeText(this, "Decryption Case error", Toast.LENGTH_LONG).show();
        }
        saveMenuItem.setVisible(true);
        shouldShowSaveItem = true;
    }

    public void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public void onUserSelectedTextSample(String selection) {
        encryptedText_edit.setText("");
        encryptedText_edit.setText(selection);

    }

    public static List<String> getTextsToSave(boolean input, boolean output) {
        List<String> results = new ArrayList<>();
        if (input && !output) {
            results.add(encryptedText_edit.getText().toString());
        } else if (!input && output) {

            results.add(decryptedText_edit.getText().toString());
        } else {
            results.add(encryptedText_edit.getText().toString());
            results.add(decryptedText_edit.getText().toString());
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
	 * ASYNC
	 * */

    class MyDecryptionTask extends AsyncTask<EType, Void, String> {
        String encryptedText, decryptedText, keyword;
        EditText encryptedText_edit;
        EditText decryptedText_edit;
        CheckBox whitespaces_cb;
        int delta;
        HorizontalNumberPicker hnp;

        @Override
        protected void onPreExecute() {
            encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InDecryptActivity);
            encryptedText = encryptedText_edit.getText().toString();
            whitespaces_cb = (CheckBox) findViewById(R.id.checkBox_spaces_InDecryptActivity);
            decryptedText_edit = (EditText) findViewById(R.id.edit_decryptedText);
            hnp = horizontalNP;
            delta = hnp.getValue();
            keyword = keyword_edit.getText().toString();
        }

        @Override
        protected String doInBackground(EType... params) {
            EType decryption = params[0];
            String result = null;

            switch (decryption) {
                case VIGENERE:
                    result = VigenereDecryption.runDecryption(encryptedText, keyword, whitespaces_cb.isChecked());
                    break;
                case CAESARS:
                    result = CaesarsDecryption.runDecryption(encryptedText, delta, whitespaces_cb.isChecked());
                    break;
                default:
                    Toast.makeText(DecryptActivity.this, "Error case in AsyncTask", Toast.LENGTH_LONG).show();
                    break;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            decryptedText_edit.setText(result);
            updateNavigationDrawer();
        }
    }

}
