package com.rspn.cryptotool.decrypt;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rspn.cryptotool.AbstractCryptActivity;
import com.rspn.cryptotool.R;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.uihelper.CryptInfoDialogFragment;
import com.rspn.cryptotool.uihelper.HorizontalNumberPicker;
import com.rspn.cryptotool.uihelper.OpenDialogFragment;
import com.rspn.cryptotool.uihelper.SaveDialogFragment;
import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.utils.CTUtils.EType;

import java.util.ArrayList;
import java.util.List;

public class DecryptActivity extends AbstractCryptActivity implements OnItemSelectedListener, SaveDialogFragment.Communicator {
    private final static String ENCRYPT_OUTPUT = "Encrypt Output";
    private final static String INFO = "Info";
    private Spinner scheme_spinner;
    private EditText keyword_edit;
    private static EditText encryptedText_edit;
    private static EditText decryptedText_edit;
    private LinearLayout options_ll;
    private EType decryptionType;
    private Button run_decrypt_bt;
    private TextView shift_tv;
    private HorizontalNumberPicker horizontalNP;
    private MenuItem saveMenuItem;
    private boolean directActivity = true;
    private CheckBox whitespaces_cb;
    private boolean drawerIconsAdded = false;
    private boolean shouldShowSaveItem = false;

    public DecryptActivity() {
        super(R.layout.activity_decrypt,
                R.id.adView_InDecryptActivity,
                R.id.drawer_layoutInDecryptActivity,
                R.id.navigation_listInDecryptActivity,
                R.drawable.open_lock_binary);
    }

    @Override
    protected void findAndSetViews() {
        scheme_spinner = (Spinner) findViewById(R.id.spinner_InDecryptActivity);
        scheme_spinner.setOnItemSelectedListener(this);
        options_ll = (LinearLayout) findViewById(R.id.layout_options);
        keyword_edit = new EditText(this);
        keyword_edit.setHint("Keyword");
        setKeywordTextFilter(keyword_edit);
        decryptedText_edit = (EditText) findViewById(R.id.edit_decryptedText);
        whitespaces_cb = (CheckBox) findViewById(R.id.checkBox_spaces_InDecryptActivity);
        encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InDecryptActivity);
        run_decrypt_bt = (Button) findViewById(R.id.run_decryption);
        shift_tv = new TextView(this);
        shift_tv.setText(R.string.label_switchBy);
        horizontalNP = new HorizontalNumberPicker(this);
        horizontalNP.setMax(25);
    }

    @Override
    protected void drawerItemClick(int position) {
        NavigationItem navigationItem = ((NavigationItem) navigationList.getItemAtPosition(position));
        if (navigationItem == null) {
            return;
        }
        String selectedOption = navigationItem.getItemName();

        if (selectedOption.equals(ENCRYPT_OUTPUT)) {
            Intent intent = new Intent(this, EncryptActivity.class);
            intent.putExtra("data", decryptedText_edit.getText().toString());
            startActivity(intent);
        } else if (selectedOption.equals(INFO)) {
            if (decryptionType == EType.CAESARS) {
                FragmentManager manager = getFragmentManager();
                CryptInfoDialogFragment dialog = CryptInfoDialogFragment.newInstance("decryptCaesar");
                dialog.show(manager, "dialog");
            } else {
                FragmentManager manager = getFragmentManager();
                CryptInfoDialogFragment dialog = CryptInfoDialogFragment.newInstance("decryptVigenere");
                dialog.show(manager, "dialog");
            }
        }
    }

    @Override
    protected String getSharableContent() {
        return decryptedText_edit.getText().toString();
    }

    @Override
    protected boolean satisfiedMainButtonPreconditions() {
        return encryptedText_edit.getText().length() > 0;
    }

    @Override
    protected void setOnClickListener() {
        scheme_spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void setDataFromOriginatingActivity() {
        if (this.getIntent().hasExtra("data")) {
            encryptedText_edit.setText(this.getIntent().getStringExtra("data"));
            directActivity = false;
        }
    }

    private void updateNavigationDrawer() {
        if (drawerIconsAdded) {
            return;
        }

        if (!directActivity) {
            return;
        }
        navigationItems.add(new NavigationItem("Encrypt Output", R.drawable.ic_go_to_encryption));
        navigationItems.add(new NavigationItem("Info", R.drawable.ic_info));
        drawerIconsAdded = true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isNavigationDrawerClosed() && shouldShowSaveItem) {
            saveMenuItem.setVisible(true);
        }
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
            OpenDialogFragment dialog = OpenDialogFragment.newInstance("EncryptedText");//encrypted samples so user can choose to encrypt one
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
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void runDecryption(View v) {
        if (satisfiedMainButtonPreconditions()) {
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
                    vibrate();
                    break;
                case CAESARS:
                    hideKeyboard(encryptedText_edit);
                    CaesarsDecryption.initComponents();
                    new MyDecryptionTask().execute(EType.CAESARS);
                    vibrate();
                    break;
                default:
                    Toast.makeText(this, "Decryption Case error", Toast.LENGTH_LONG).show();
            }
            saveMenuItem.setVisible(true);
            shouldShowSaveItem = true;
        }
    }

    public void onUserSelectedTextSample(String selection) {
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

    private class MyDecryptionTask extends AsyncTask<EType, Void, String> {
        private String encryptedText,  keyword;
        private EditText encryptedText_edit;
        private EditText decryptedText_edit;
        private boolean isWhitespacesChecked;
        private int delta;
        private HorizontalNumberPicker hnp;

        @Override
        protected void onPreExecute() {
            encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InDecryptActivity);
            encryptedText = encryptedText_edit.getText().toString();
            isWhitespacesChecked = ((CheckBox) findViewById(R.id.checkBox_spaces_InDecryptActivity)).isChecked();
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
                    result = VigenereDecryption.runDecryption(encryptedText, keyword, isWhitespacesChecked);
                    break;
                case CAESARS:
                    result = CaesarsDecryption.runDecryption(encryptedText, delta, isWhitespacesChecked);
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
