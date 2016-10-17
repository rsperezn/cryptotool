package com.rspn.cryptotool.breakencryption;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rspn.cryptotool.AbstractCryptActivity;
import com.rspn.cryptotool.R;
import com.rspn.cryptotool.StatsActivity;
import com.rspn.cryptotool.decrypt.VigenereDecryption;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.model.StatsResults;
import com.rspn.cryptotool.uihelper.CryptInfoDialogFragment;
import com.rspn.cryptotool.uihelper.OpenDialogFragment;
import com.rspn.cryptotool.uihelper.SaveDialogFragment;
import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.utils.CTUtils.EType;
import com.rspn.cryptotool.utils.IC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BreakEncryptionActivity extends AbstractCryptActivity implements SaveDialogFragment.Communicator, OnItemSelectedListener {
    private static EditText encryptedText_edit;
    private static EditText brokenText_edit;
    private final String STATS = "Stats";
    private final String INFO = "Info";
    private final String ENCRYPT_OUTPUT = "Encrypt Output";
    private EType encryptionType = EType.NULL;
    private EType prevEncryptionType = EType.NULL;
    private MenuItem saveMenuItem;
    private boolean directActivity = true;
    private Spinner possibleKeys_sp;
    private CheckBox whitespaces_cb;
    private ProgressDialog progressDialog;
    private NavigationItem encryptOutput_navItem = new NavigationItem("Encrypt Output", R.drawable.ic_go_to_encryption);
    private NavigationItem info_navItem = new NavigationItem("Info", R.drawable.ic_info);
    private NavigationItem stats_navItem = new NavigationItem("Stats", R.drawable.ic_stastistics);
    private boolean shouldShowSaveItem = false;
    private StatsResults statsResults = new StatsResults();

    public BreakEncryptionActivity() {
        super(R.layout.activity_breakencryption,
                R.id.adView_InBreakActivity,
                R.id.drawer_layoutInBreakActivity,
                R.id.navigation_listInBreakActivity,
                R.drawable.broken_lock);
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

    @Override
    protected void findAndSetViews() {
        encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InBreakActivity);
        brokenText_edit = (EditText) findViewById(R.id.edit_brokenText);
        DrawerLayout navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layoutInBreakActivity);
        navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        whitespaces_cb = (CheckBox) findViewById(R.id.checkBox_spaces_InBreakActivity);
        possibleKeys_sp = (Spinner) findViewById(R.id.spinner_PossibleKeys);
        possibleKeys_sp.setVisibility(View.INVISIBLE);
        navigationList = (ListView) findViewById(R.id.navigation_listInBreakActivity);
    }

    @Override
    protected void listItemClick(int position) {
        NavigationItem navigationItem = ((NavigationItem) navigationList.getItemAtPosition(position));
        if (navigationItem == null) {
            return;
        }
        String selectedOption = navigationItem.getItemName();

        switch (selectedOption) {
            case ENCRYPT_OUTPUT: {
                Intent intent = new Intent(this, EncryptActivity.class);
                intent.putExtra("data", brokenText_edit.getText().toString());
                startActivity(intent);
                break;
            }
            case STATS: {
                statsResults.setResultType(encryptionType.name());
                statsResults.setICforKeyLength(VigenereBreaker.getICs());
                Intent intent = new Intent(this, StatsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", statsResults);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
            case INFO:
                if (encryptionType == EType.VIGENERE) {
                    FragmentManager manager = getFragmentManager();
                    CryptInfoDialogFragment dialog = CryptInfoDialogFragment.newInstance("breakVigenere");
                    dialog.show(manager, "dialog");
                } else {
                    FragmentManager manager = getFragmentManager();
                    CryptInfoDialogFragment dialog = CryptInfoDialogFragment.newInstance("breakCaesar");
                    dialog.show(manager, "dialog");
                }
                break;
        }
    }

    @Override
    protected String getShareableContent() {
        return brokenText_edit.getText().toString();
    }

    @Override
    protected boolean satisfiedMainButtonPreconditions() {
        return encryptedText_edit.getText().length() > 0;
    }

    @Override
    protected void setOnClickListener() {
        possibleKeys_sp.setOnItemSelectedListener(this);
    }

    @Override
    protected void setDataFromOriginatingActivity() {
        if (this.getIntent().hasExtra("data")) {
            encryptedText_edit.setText(this.getIntent().getStringExtra("data"));
            directActivity = false;
        }
    }

    public void onUserSelectedTextSample(String selection) {
        encryptedText_edit.setText(selection);
    }

    private void updateNavigationDrawer() {
        if (prevEncryptionType == EType.NULL && encryptionType == EType.CAESARS) {
            if (directActivity)
                navigationItems.add(encryptOutput_navItem);
            navigationItems.add(info_navItem);
            navigationItems.add(stats_navItem);
        } else if (prevEncryptionType == EType.NULL && encryptionType == EType.VIGENERE) {
            if (directActivity)
                navigationItems.add(encryptOutput_navItem);
            navigationItems.add(info_navItem);
            navigationItems.add(stats_navItem);
        }
    }

    public void runBreakEncryption(View v) {
        if (satisfiedMainButtonPreconditions()) {
            prevEncryptionType = encryptionType;
            encryptionType = getEncryptionType(encryptedText_edit.getText().toString());
            MyEncryptionBreaker breaker = new MyEncryptionBreaker(this);
            breaker.execute(encryptionType);
            progressDialog = ProgressDialog.show(this, "Please Wait", "Breaking Encryption...");
            vibrate();
            shouldShowSaveItem = true;
        }
        hideKeyboard(encryptedText_edit);
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
        if (isNavigationDrawerClosed() && shouldShowSaveItem)
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
            OpenDialogFragment dialog = OpenDialogFragment.newInstance("EncryptedText");//encrypted samples so user can choose to encrypt one
            dialog.show(manager, "dialog");
        }
        if (id == R.id.action_save) {
            FragmentManager manager = getFragmentManager();
            SaveDialogFragment dialog = new SaveDialogFragment();//plaintext samples so user can choose to encrypt one
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedKey = possibleKeys_sp.getSelectedItem().toString();
        brokenText_edit.setText(VigenereDecryption.runDecryption(encryptedText_edit.getText().toString(), selectedKey, whitespaces_cb.isChecked()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onDialogMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private class MyEncryptionBreaker extends AsyncTask<EType, Integer, Void> {
        private EditText encryptedText_edit;
        private EditText brokenText_edit;
        private String encryptedText;
        private boolean isWhitespacesChecked;
        private EType encryption;
        private Activity activity;
        private HashMap<String, Object> resultsInfo;


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
            isWhitespacesChecked = ((CheckBox) findViewById(R.id.checkBox_spaces_InBreakActivity)).isChecked();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(EType... params) {
            publishProgress(30);
            encryption = params[0];
            switch (encryption) {
                case CAESARS:
                    publishProgress(50);
                    resultsInfo = CaesarBreaker.runBreak(encryptedText, isWhitespacesChecked);
                    publishProgress(80);
                    break;
                case VIGENERE:
                    publishProgress(50);
                    resultsInfo = VigenereBreaker.runBreak(encryptedText);
                    List<String> forcedKeys = (List<String>) resultsInfo.get("forcedKeys");

                    if (forcedKeys.isEmpty()) {//was not able to generate keys with Vigenere so force to break with Caesars
                        publishProgress(30);
                        encryptionType = EType.CAESARS;
                        doInBackground(EType.CAESARS);
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
            updateNavigationDrawer();
            progressDialog.dismiss();
        }
    }
}
