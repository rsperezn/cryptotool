package com.rspn.cryptotool.calculatehashes;

import java.io.ByteArrayOutputStream;
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
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.orleonsoft.android.simplefilechooser.Constants;
import com.orleonsoft.android.simplefilechooser.ui.FileChooserActivity;
import com.rspn.cryptotool.MainActivity;
import com.rspn.cryptotool.R;
import com.rspn.cryptotool.uihelper.CryptInfoDialogFragment;
import com.rspn.cryptotool.uihelper.DrawerAdapter;
import com.rspn.cryptotool.uihelper.OpenComparisonInputsFragment;
import com.rspn.cryptotool.uihelper.OpenDialogFragment;
import com.rspn.cryptotool.model.NavigationItem;
import com.rspn.cryptotool.utils.CTUtils;


@SuppressWarnings("deprecation")
public class CalculateHashesActivity extends ActionBarActivity implements OnClickListener, OnItemSelectedListener, OnItemClickListener {

    EditText input1_edit;
    Button calculate_bt;
    Button copyToClipboard_bt;
    String algorithm;
    Spinner algorithms_sp;
    EditText output_edit;
    AdView adView;
    CharSequence title;
    ActionBarDrawerToggle drawerToggle;
    Vibrator vibrator;
    DrawerLayout navDrawerLayout;
    ListView navList;
    CharSequence drawerTitle;
    List<NavigationItem> navItems;
    LinearLayout calculate_layout;
    LinearLayout main_layout;
    boolean navDrawerOpen = false;
    final int FILE_CHOOSER = 1; // onActivityResult request code
    String hashType = "Text";
    RadioButton type_rb;
    CheckBox compare_chb;
    ProgressDialog progressDialog;
    long timerStartTime;
    long fileSize;
    double hashingTime;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    long lastModified;
    MenuItem hashingDetailsMenuItem;
    String[] fileHashingAlgorithms = new String[]{"Algorithms", "SHA-1", "SHA-256", "SHA-384", "SHA-512", "MD5"};
    String[] textHashingAlgorithms = new String[]{"Algorithms", "SHA-1", "SHA-256", "SHA-384", "SHA-512", "MD5", "RC4", "Blowfish", "AES"};
    boolean shouldShowDetailsItem = false;
    boolean comparingHashes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculatehashes);
        if (this.getIntent().hasExtra("HashType")) {
            hashType = this.getIntent().getStringExtra("HashType");
            setTitle("Calculate Hashes of " + hashType);
        }

        input1_edit = (EditText) findViewById(R.id.edit_inputHash);
        calculate_bt = (Button) findViewById(R.id.calculate_hash);
        calculate_bt.setOnClickListener(this);
        algorithms_sp = (Spinner) findViewById(R.id.spinner_hashAlgorithms);
        ArrayAdapter<String> algorithmsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, textHashingAlgorithms);
        algorithms_sp.setAdapter(algorithmsAdapter);

        algorithms_sp.setOnItemSelectedListener(this);
        setSpinnerAlgorithms();
        calculate_layout = (LinearLayout) findViewById(R.id.bottomCalculateHashes_layout);
        main_layout = (LinearLayout) findViewById(R.id.mainCalculateHashes_layout);
        output_edit = (EditText) findViewById(R.id.edit_hashedText);
        copyToClipboard_bt = (Button) findViewById(R.id.copyToClipboard_hash);
        copyToClipboard_bt.setOnClickListener(this);
        copyToClipboard_bt.setVisibility(View.INVISIBLE);
        compare_chb = (CheckBox) findViewById(R.id.checkBox_compareHashes);
        compare_chb.setOnClickListener(this);

        navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layoutInCalculateHashesActivity);
        navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        View header = getLayoutInflater().inflate(R.layout.header_navigation, null);
        ImageView iv = (ImageView) header.findViewById(R.id.header);
        iv.setImageResource(R.drawable.xor_binary);
        navList = (ListView) findViewById(R.id.navigation_listInCalculateHashesActivity);
        navList.addHeaderView(header);
        navList.setOnItemClickListener(this);
        navItems = new ArrayList<>();
        navItems.add(new NavigationItem("Home", R.drawable.ic_home));
        navItems.add(new NavigationItem("Share", R.drawable.ic_share));
        navItems.add(new NavigationItem("Info", R.drawable.ic_info));
        DrawerAdapter adapter = new DrawerAdapter(this, navItems);
        navList.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = drawerTitle = getTitle();
        drawerToggle = new ActionBarDrawerToggle(this, navDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                navDrawerOpen = false;
            }

            public void onDrawerOpened(View drawerView) {
                hideKeyboard();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                navDrawerOpen = true;
            }
        };

        navDrawerLayout.setDrawerListener(drawerToggle);
        setEditTextsHints();
        //Ads
        adView = (AdView) this.findViewById(R.id.adView_InCalculateHashesActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //		String all="";
        //		for (Provider provider : Security.getProviders()) {
        //            System.out.println("Provider: " + provider.getName());
        //            all+= "Provider: " + provider.getName();
        //            for (Provider.Service service : provider.getServices()) {
        //                System.out.println("  Algorithm: " + service.getAlgorithm());
        //                all +=  "  Algorithm: " + service.getAlgorithm();
        //            }
        //		}
        //
        //		input_edit.setText(all);
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
                String hashedData = output_edit.getText().toString();
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(hashedData);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copy of Hash data", hashedData);
                    clipboard.setPrimaryClip(clip);
                }
                startVibrate(15);
                break;

            case R.id.checkBox_compareHashes:
                if (((CheckBox) v).isChecked()) {
                    calculate_bt.setText("Compare Hashes");
                    comparingHashes = true;
                    setEditTextsHints();
                    copyToClipboard_bt.setVisibility(View.INVISIBLE);
                } else {
                    calculate_bt.setText("Calculate Hash");
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

        HasherDTO hasherDTO = new HasherDTO(algorithm, hashType, data);

        progressDialog = new ProgressDialog(this);
        timerStartTime = System.currentTimeMillis();
        progressDialog.setMessage("Calculating hash of " + hashType + " ...");
        progressDialog.show();
        new Hasher().execute(hasherDTO);
        startVibrate();
    }

    public void displayHashComparisonResult(boolean matching) {
        String resultToDisplay = hashType + "s' hashes" + (matching ? " match" : " do not match");
        Toast.makeText(this, resultToDisplay, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        if (position == 0) {
            calculate_bt.setEnabled(false);
            return;
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

    public void startVibrate() {
        if (CTUtils.vibrate) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
        }
    }

    public void startVibrate(int length) {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(length);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        NavigationItem navigationItem = ((NavigationItem) navList.getItemAtPosition(position));
        if (navigationItem == null) {
            return;
        }
        String selectedOption = navigationItem.getItemName();

        if (selectedOption.equals("Share")) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, output_edit.getText().toString());
            startActivity(shareIntent);
        } else if (selectedOption.equals("Home")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (selectedOption.equals("Info")) {
            FragmentManager manager = getFragmentManager();
            CryptInfoDialogFragment dialog = new CryptInfoDialogFragment("calculateHashes");
            dialog.show(manager, "dialog");
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input1_edit.getWindowToken(), 0);
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
            //TODO fix here the logic
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
            //hashingDetailsMenuItem.setVisible(false);
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

    public byte[] convertFileToByteArray(File f) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!navDrawerOpen && shouldShowDetailsItem && hashType.equals("File")) {
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

    //Async task
    class Hasher extends AsyncTask<HasherDTO, Integer, Void> {
        EditText output_edit;
        String hashType;
        ArrayList<String> dataToHash;
        String algorithm;
        ArrayList<String> hashResults = new ArrayList<>();
        boolean areMatchingHashes = false;

        @Override
        protected void onPreExecute() {
            output_edit = (EditText) findViewById(R.id.edit_hashedText);

        }

        @Override
        protected Void doInBackground(HasherDTO... params) {
            hashType = (String) ((HasherDTO) params[0]).getHashType();
            dataToHash = (ArrayList<String>) ((HasherDTO) params[0]).getData();
            algorithm = (String) ((HasherDTO) params[0]).getAlgorithm();
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
            int read = 0;
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
            byte[] byteArray = null;
            byteArray = text.getBytes();

            try {
                hideKeyboard();
                if (algorithm.equals("Blowfish")) {
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
                } else if (algorithm.equals("RC4")) {
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
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }
                    hashResults.add(CTUtils.bytesToHex(output));
                } else if (algorithm.equals("AES")) {
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
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }
                    hashResults.add(CTUtils.bytesToHex(output));
                } else {
                    MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
                    messageDigest.update(byteArray);
                    byte[] output = messageDigest.digest();
                    hashResults.add(CTUtils.bytesToHex(output));
                }
            } catch (NoSuchAlgorithmException e) {
                //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            copyToClipboard_bt.setVisibility(View.VISIBLE);
            if (!comparingHashes) {
                output_edit.setText(hashResults.get(0));
                if (hashType.equals("File")) {
                    long now = System.currentTimeMillis();
                    hashingTime = (now - timerStartTime) / 1000.0;
                    hashingDetailsMenuItem.setVisible(true);
                    shouldShowDetailsItem = true;
                }
            } else {
                displayHashComparisonResult(areMatchingHashes);
            }
        }
    }
}