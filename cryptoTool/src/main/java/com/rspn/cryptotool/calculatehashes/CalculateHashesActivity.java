package com.rspn.cryptotool.calculatehashes;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.orleonsoft.android.simplefilechooser.Constants;
import com.orleonsoft.android.simplefilechooser.ui.FileChooserActivity;
import com.rspn.cryptotool.AbstractCryptActivity;
import com.rspn.cryptotool.R;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.uihelper.CryptInfoDialogFragment;
import com.rspn.cryptotool.uihelper.OpenComparisonInputsFragment;
import com.rspn.cryptotool.uihelper.OpenDialogFragment;
import com.rspn.cryptotool.utils.CTUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class CalculateHashesActivity extends AbstractCryptActivity implements OnItemSelectedListener, View.OnClickListener {

    private EditText input1_edit;
    private Button calculate_bt;
    private Button copyToClipboard_bt;
    private String algorithm;
    private Spinner algorithms_sp;
    private EditText output_edit;
    private final int FILE_CHOOSER = 1; // onActivityResult request code
    private String hashType = "Text";
    private ProgressDialog progressDialog;
    private long timerStartTime;
    private long fileSize;
    private double hashingTime;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private long lastModified;
    private MenuItem hashingDetailsMenuItem;
    private String[] fileHashingAlgorithms = new String[]{"Algorithms", "SHA-1", "SHA-256", "SHA-384", "SHA-512", "MD5"};
    private String[] textHashingAlgorithms = new String[]{"Algorithms", "SHA-1", "SHA-256", "SHA-384", "SHA-512", "MD5", "RC4", "Blowfish", "AES"};
    private boolean shouldShowDetailsItem = false;
    private boolean comparingHashes = false;

    public CalculateHashesActivity() {
        super(R.layout.activity_calculatehashes,
                R.id.adView_InCalculateHashesActivity,
                R.id.drawer_layoutInCalculateHashesActivity,
                R.id.navigation_listInCalculateHashesActivity,
                R.drawable.xor_binary);
    }

    @Override
    protected void findAndSetViews() {
        input1_edit = (EditText) findViewById(R.id.edit_inputHash);
        calculate_bt = (Button) findViewById(R.id.calculate_hash);
        algorithms_sp = (Spinner) findViewById(R.id.spinner_hashAlgorithms);
        ArrayAdapter<String> algorithmsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, textHashingAlgorithms);
        algorithms_sp.setAdapter(algorithmsAdapter);
        setSpinnerAlgorithms();
        output_edit = (EditText) findViewById(R.id.edit_hashedText);
        copyToClipboard_bt = (Button) findViewById(R.id.copyToClipboard_hash);
        copyToClipboard_bt.setOnClickListener(this);
        copyToClipboard_bt.setVisibility(View.INVISIBLE);
        CheckBox compare_chb = (CheckBox) findViewById(R.id.checkBox_compareHashes);
        compare_chb.setOnClickListener(this);
        setEditTextsHints();
    }

    @Override
    protected void drawerItemClick(int position) {
        NavigationItem navigationItem = ((NavigationItem) navigationList.getItemAtPosition(position));
        if (navigationItem == null) {
            return;
        }
        String selectedOption = navigationItem.getItemName();
        if (selectedOption.equals("Info")) {
            FragmentManager manager = getFragmentManager();
            CryptInfoDialogFragment dialog = CryptInfoDialogFragment.newInstance("calculateHashes");
            dialog.show(manager, "dialog");
        }
    }

    @Override
    protected String getSharableContent() {
        return output_edit.getText().toString();
    }

    @Override
    protected boolean satisfiedMainButtonPreconditions() {
        return false;
    }

    @Override
    protected void setOnClickListener() {
        calculate_bt.setOnClickListener(this);
        algorithms_sp.setOnItemSelectedListener(this);
    }

    @Override
    protected void setDataFromOriginatingActivity() {
        if (this.getIntent().hasExtra("HashType")) {
            hashType = this.getIntent().getStringExtra("HashType");
            setTitle("Calculate Hashes of " + hashType);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.calculate_hash:
                if (hashType.equals("File")) {
                    if (filesPathExist()) {
                        calculateHashes();
                    }
                    break;
                } else {
                    calculateHashes();
                    break;
                }

            case R.id.copyToClipboard_hash:
                String hashedData = getSharableContent();
                copyToClipboard(hashedData);
                vibrate(15);
                break;

            case R.id.checkBox_compareHashes:
                if (((CheckBox) v).isChecked()) {
                    calculate_bt.setText(R.string.label_compareHashButton);
                    comparingHashes = true;
                    setEditTextsHints();
                    copyToClipboard_bt.setVisibility(View.INVISIBLE);
                    output_edit.setText("");
                } else {
                    calculate_bt.setText(R.string.label_calculateHashButton);
                    comparingHashes = false;
                    setEditTextsHints();
                    copyToClipboard_bt.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private boolean filesPathExist() {
        ArrayList<EditText> inputs_editTexts = new ArrayList<>();
        inputs_editTexts.add(input1_edit);
        if (comparingHashes) {
            inputs_editTexts.add(output_edit);
        }
        for (EditText input_editText : inputs_editTexts) {
            if (!fileExists(input_editText)) {
                return false;
            }
        }
        return true;
    }

    private void setEditTextsHints() {
        if (hashType.equals("File")) {
            if (comparingHashes) {
                input1_edit.setHint("File path 1");
                output_edit.setHint("File path 2");
            } else {
                input1_edit.setHint("File path");
                output_edit.setHint("Output");
            }
        } else {
            if (comparingHashes) {
                input1_edit.setHint("Text 1");
                output_edit.setHint("Text 2");
            } else {
                input1_edit.setHint("Text");
                output_edit.setHint("Output");
            }
        }
    }

    private void calculateHashes() {
        ArrayList<String> data = new ArrayList<>();
        data.add(input1_edit.getText().toString());
        if (comparingHashes) {
            data.add(output_edit.getText().toString());
        }

        HashData hashData = new HashData(algorithm, hashType, data);

        progressDialog = new ProgressDialog(this);
        timerStartTime = System.currentTimeMillis();
        progressDialog.setMessage("Calculating hash of " + hashType + " ...");
        progressDialog.show();
        new Hasher().execute(hashData);
        vibrate();
    }

    public void displayHashComparisonResult(boolean matching) {
        String resultToDisplay = hashType + "s' hashes" + (matching ? " match" : " do not match");
        Toast.makeText(this, resultToDisplay, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            calculate_bt.setEnabled(false);
        } else {
            calculate_bt.setEnabled(true);
            algorithm = algorithms_sp.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calculate_hash, menu);
        hashingDetailsMenuItem = menu.getItem(1);
        hashingDetailsMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        if (id == R.id.menu_open) {
            FragmentManager manager = getFragmentManager();
            if (hashType.equals("Text")) {
                if (comparingHashes) {
                    OpenComparisonInputsFragment dialog = new OpenComparisonInputsFragment("Text");
                    dialog.show(manager, "comparison manager");
                } else {
                    OpenDialogFragment dialog = new OpenDialogFragment("All");
                    dialog.show(manager, "dialog");
                }
            } else {
                if (comparingHashes) {
                    OpenComparisonInputsFragment dialog = new OpenComparisonInputsFragment("File");
                    dialog.show(manager, "comparison manager");
                } else {
                    Intent intent = new Intent(this, FileChooserActivity.class);
                    startActivityForResult(intent, FILE_CHOOSER);
                    return true;
                }
            }
        } else if (id == R.id.menu_hashing_details) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("File Hashing details")
                    .setIcon(R.drawable.ic_info)
                    .setMessage("File size: " + fileSize + " bytes \nLast modified: " + sdf.format(lastModified) + "\nHashing time: " + hashingTime + " seconds")
                    .setCancelable(true);
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == FILE_CHOOSER) && (resultCode == RESULT_OK)) {
            String fileSelected = data.getStringExtra(Constants.KEY_FILE_SELECTED);
            Toast.makeText(this, "file selected " + fileSelected, Toast.LENGTH_SHORT).show();
            input1_edit.setText(fileSelected);
        }
    }

    public void setInputsForHashComparison(String input1, String input2) {
        input1_edit.setText(input1);
        output_edit.setText(input2);
    }

    public void setInputForHashCalculation(String input1) {
        input1_edit.setText(input1);
    }

    public void setSpinnerAlgorithms() {
        ArrayAdapter<String> algorithmsAdapter;

        if (hashType.equals("Text")) {
            algorithms_sp.setAdapter(null);
            algorithmsAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, textHashingAlgorithms);
            algorithms_sp.setAdapter(algorithmsAdapter);
        } else if (hashType.equals("File")) {
            algorithms_sp.setAdapter(null);
            algorithmsAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, fileHashingAlgorithms);
            algorithms_sp.setAdapter(algorithmsAdapter);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isNavigationDrawerClosed() && shouldShowDetailsItem && hashType.equals("File")) {
            hashingDetailsMenuItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean fileExists(EditText editText) {
        File tmp = new File(editText.getText().toString());
        if (!tmp.exists()) {
            Toast.makeText(this, String.format("File at '%s' does not exist", editText.getHint()), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private class Hasher extends AsyncTask<HashData, Integer, Void> {
        private String hashType;
        private ArrayList<String> dataToHash;
        private String algorithm;
        private ArrayList<String> hashResults = new ArrayList<>();
        private boolean areMatchingHashes = false;

        @Override
        protected Void doInBackground(HashData... params) {
            hashType = params[0].getHashType();
            dataToHash = params[0].getData();
            algorithm = params[0].getAlgorithm();
            for (String data : dataToHash) {
                if (hashType.equals("Text")) {
                    calculateHashOfText(data);
                } else {
                    calculateHashOfFile(data);
                }
            }
            if (comparingHashes) {
                checkMatchingHashes();
            }

            return null;
        }

        private void checkMatchingHashes() {
            areMatchingHashes = hashResults.get(0).equals(hashResults.get(1));
        }

        private void calculateHashOfFile(String filePath) {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            File file = new File(filePath);
            fileSize = file.length();
            lastModified = file.lastModified();
            InputStream is = null;
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Log.i(CTUtils.TAG, "Exception while getting FileInputStream");
            }

            byte[] buffer = new byte[8192];
            int read;
            try {
                if (is != null)
                    while ((read = is.read(buffer)) > 0) {
                        digest.update(buffer, 0, read);
                    }
                byte[] algorithmSum = digest.digest();
                BigInteger bigInt = new BigInteger(1, algorithmSum);
                String output = bigInt.toString(16);
                // Fill to 32 chars
                output = String.format("%32s", output).replace(' ', '0');
                hashResults.add(output.toUpperCase());
            } catch (IOException e) {
                throw new RuntimeException("Unable to process file for MD5", e);
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    Log.i(CTUtils.TAG, "Exception on closing " + algorithm + " input stream");
                }
            }
        }

        private void calculateHashOfText(String text) {
            byte[] byteArray;
            byteArray = text.getBytes();

            try {
                hideKeyboard(input1_edit);
                switch (algorithm) {
                    case "Blowfish": {
                        KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
                        keyGenerator.init(128);
                        Key secretKey = keyGenerator.generateKey();
                        Cipher cipher = null;
                        try {
                            cipher = Cipher.getInstance("Blowfish/CFB/NoPadding");
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        }

                        try {
                            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                        byte output[] = cipher.getIV();
                        hashResults.add(CTUtils.bytesToHex(output));
                        break;
                    }
                    case "RC4": {
                        SecureRandom secureRandom = new SecureRandom(new byte[128]);
                        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
                        keyGenerator.init(secureRandom);
                        SecretKey sk = keyGenerator.generateKey();

                        // create an instance of cipher
                        Cipher cipher = null;
                        try {
                            cipher = Cipher.getInstance(algorithm);
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        }

                        // initialize the cipher with the key
                        try {
                            cipher.init(Cipher.ENCRYPT_MODE, sk);
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                        byte[] output = null;
                        try {
                            output = cipher.doFinal(byteArray);
                        } catch (IllegalBlockSizeException | BadPaddingException e) {
                            e.printStackTrace();
                        }
                        hashResults.add(CTUtils.bytesToHex(output));
                        break;
                    }
                    case "AES": {
                        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
                        keyGenerator.init(128);
                        Key key = keyGenerator.generateKey();
                        Cipher cipher = null;
                        try {
                            cipher = Cipher.getInstance("AES");
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        }
                        try {
                            cipher.init(Cipher.ENCRYPT_MODE, key);
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                        byte[] output = null;
                        try {
                            output = cipher.doFinal(byteArray);
                        } catch (IllegalBlockSizeException | BadPaddingException e) {
                            e.printStackTrace();
                        }
                        hashResults.add(CTUtils.bytesToHex(output));
                        break;
                    }
                    default: {
                        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
                        messageDigest.update(byteArray);
                        byte[] output = messageDigest.digest();
                        hashResults.add(CTUtils.bytesToHex(output));
                        break;
                    }
                }
            } catch (NoSuchAlgorithmException e) {
                //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            if (!comparingHashes) {
                output_edit.setText(hashResults.get(0));
                if (hashType.equals("File")) {
                    long now = System.currentTimeMillis();
                    hashingTime = (now - timerStartTime) / 1000.0;
                    hashingDetailsMenuItem.setVisible(true);
                    shouldShowDetailsItem = true;
                    copyToClipboard_bt.setVisibility(View.VISIBLE);
                }
            } else {
                displayHashComparisonResult(areMatchingHashes);
            }
        }
    }
}