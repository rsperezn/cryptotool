package com.rspn.cryptotool.encrypt;

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
import com.rspn.cryptotool.breakencryption.BreakEncryptionActivity;
import com.rspn.cryptotool.decrypt.DecryptActivity;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.uihelper.CryptInfoDialogFragment;
import com.rspn.cryptotool.uihelper.OpenDialogFragment;
import com.rspn.cryptotool.uihelper.SaveDialogFragment;
import com.rspn.cryptotool.utils.CTUtils.EType;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.ArrayList;
import java.util.List;

public class EncryptActivity extends AbstractCryptActivity implements OnItemSelectedListener, SaveDialogFragment.Communicator {
    private static final String DECRYPT_OUTPUT = "Decrypt Output";
    private static final String BREAK_ENCRYPTION_OF_OUTPUT = "Break Encryption of Output";
    private static final String INFO = "Info";
    static EditText plainText_edit;
    static EditText encryptedText_edit;
    private Spinner scheme_spinner;
    private EditText keyword_edit;
    private LinearLayout options_ll;
    private EType encryptionType;
    private Button run_encrypt_bt;
    private CheckBox whitespaces_cb;
    private TextView shift_tv;
    private NumberPicker numberPicker;
    private MenuItem saveMenuItem;
    private boolean directActivity = true;
    private boolean drawerIconsAdded = false;
    private boolean shouldShowShowSaveItem = false;

    public EncryptActivity() {
        super(R.layout.activity_encrypt,
                R.id.adView_InEncryptActivity,
                R.id.drawer_layoutInEncryptActivity,
                R.id.navigation_listInEncryptActivity,
                R.drawable.closed_lock_binary);
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
    protected void findAndSetViews() {
        scheme_spinner = (Spinner) findViewById(R.id.spinner_InEncryptActivity);
        options_ll = (LinearLayout) findViewById(R.id.layout_options_InEncryptActivity);
        keyword_edit = new EditText(this);
        keyword_edit.setHint("Keyword");
        setKeywordTextFilter(keyword_edit);
        plainText_edit = (EditText) findViewById(R.id.edit_plainText);
        whitespaces_cb = (CheckBox) findViewById(R.id.checkBox_spaces_InEncryptActivity);
        encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InEncryptActivity);
        run_encrypt_bt = (Button) findViewById(R.id.run_encryption);
        shift_tv = new TextView(this);
        shift_tv.setText(R.string.label_switchBy);
        numberPicker = (NumberPicker) findViewById(R.id.number_pickerInEncryptActivity);
        numberPicker.setMin(1);
        numberPicker.setMax(25);
        options_ll.removeView(numberPicker);
    }

    @Override
    protected void listItemClick(int position) {
        NavigationItem navigationItem = ((NavigationItem) navigationList.getItemAtPosition(position));
        if (navigationItem == null) {
            return;
        }
        String selectedOption = navigationItem.getItemName();
        switch (selectedOption) {
            case DECRYPT_OUTPUT: {
                Intent intent = new Intent(this, DecryptActivity.class);
                intent.putExtra("data", encryptedText_edit.getText().toString());
                startActivity(intent);
                break;
            }
            case BREAK_ENCRYPTION_OF_OUTPUT: {
                Intent intent = new Intent(this, BreakEncryptionActivity.class);
                intent.putExtra("data", encryptedText_edit.getText().toString());
                startActivity(intent);
                break;
            }
            case INFO:
                if (encryptionType == EType.CAESARS) {
                    FragmentManager manager = getFragmentManager();
                    CryptInfoDialogFragment dialog = CryptInfoDialogFragment.newInstance("encryptCaesar");
                    dialog.show(manager, "dialog");
                } else {
                    FragmentManager manager = getFragmentManager();
                    CryptInfoDialogFragment dialog = CryptInfoDialogFragment.newInstance("encryptVigenere");
                    dialog.show(manager, "dialog");
                }
                break;
        }
    }

    @Override
    protected String getShareableContent() {
        return encryptedText_edit.getText().toString();
    }

    @Override
    protected boolean satisfiedMainButtonPreconditions() {
        return plainText_edit.getText().length() > 0;
    }

    @Override
    protected void setOnClickListener() {
        scheme_spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void setDataFromOriginatingActivity() {
        if (this.getIntent().hasExtra("data")) {
            plainText_edit.setText(this.getIntent().getStringExtra("data"));
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
        navigationItems.add(new NavigationItem("Decrypt Output", R.drawable.ic_go_to_decryption));
        navigationItems.add(new NavigationItem("Break Encryption of Output", R.drawable.ic_go_to_break));
        navigationItems.add(new NavigationItem("Info", R.drawable.ic_info));

        drawerIconsAdded = true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isNavigationDrawerClosed() && shouldShowShowSaveItem)
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
            OpenDialogFragment dialog = OpenDialogFragment.newInstance("PlainText");//plaintext samples so user can choose to encrypt one
            dialog.show(manager, "dialog");

        }
        if (id == R.id.action_save) {
            FragmentManager manager = getFragmentManager();
            SaveDialogFragment dialog = new SaveDialogFragment();//plaintext samples so user can choose to encrypt one
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
            options_ll.removeView(numberPicker);
            options_ll.removeView(keyword_edit);
            encryptionType = EType.NULL;
        } else if (position == 1) {//Caesar's
            run_encrypt_bt.setEnabled(true);
            options_ll.removeView(keyword_edit);
            options_ll.addView(shift_tv);
            options_ll.addView(numberPicker);
            encryptionType = EType.CAESARS;
        } else {//Vigenere
            run_encrypt_bt.setEnabled(true);
            options_ll.removeView(shift_tv);
            options_ll.removeView(numberPicker);
            options_ll.addView(keyword_edit);
            encryptionType = EType.VIGENERE;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void runEncryption(View v) {
        if (satisfiedMainButtonPreconditions()) {
            switch (encryptionType) {
                case VIGENERE:
                    if (keyword_edit.getText().toString().equals("")) {
                        Toast.makeText(this, "Please enter a keyword", Toast.LENGTH_SHORT).show();
                        hideKeyboard(keyword_edit);
                        return;
                    }
                    hideKeyboard(plainText_edit);

                    new MyEncryptionTask().execute(EType.VIGENERE);
                    vibrate();
                    break;

                case CAESARS:
                    hideKeyboard(plainText_edit);
                    new MyEncryptionTask().execute(EType.CAESARS);
                    vibrate();
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
    }

    public void onUserSelectedTextSample(String selection) {
        plainText_edit.setText(selection);
    }

    @Override
    public void onDialogMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private class MyEncryptionTask extends AsyncTask<EType, Void, String> {
        private String plainText, keyword;
        private int delta;
        private EditText plainText_edit;
        private EditText encryptedText_edit;
        private boolean isWhitespacesChecked;

        @Override
        protected void onPreExecute() {
            plainText_edit = (EditText) findViewById(R.id.edit_plainText);
            plainText = plainText_edit.getText().toString();
            delta = numberPicker.getValue();
            encryptedText_edit = (EditText) findViewById(R.id.edit_encryptedText_InEncryptActivity);
            isWhitespacesChecked = ((CheckBox) findViewById(R.id.checkBox_spaces_InEncryptActivity)).isChecked();
            keyword = keyword_edit.getText().toString();
        }

        @Override
        protected String doInBackground(EType... params) {
            EType encryption = params[0];
            String result = null;
            switch (encryption) {
                case VIGENERE:
                    result = VigenereEncryption.runEncryption(plainText, keyword, isWhitespacesChecked);
                    break;

                case CAESARS:
                    result = CaesarEncryption.runEncryption(plainText, delta, isWhitespacesChecked);
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
