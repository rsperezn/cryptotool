package com.rspn.cryptotool.passwordgenerator;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.rspn.cryptotool.AbstractCryptActivity;
import com.rspn.cryptotool.R;

import java.util.ArrayList;
import java.util.List;

public class PronounceablePasswordActivity extends AbstractCryptActivity implements View.OnClickListener{
    private Spinner numberOfPasswords_spinner;
    private EditText passwordLength_edit;
    private ListView passwords_list;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> passwordsArrayAdapter;
    private ArrayAdapter<Integer> numberOfPasswordsArrayAdapter;

    public PronounceablePasswordActivity() {
        super(R.layout.activity_pronounceablepassword,
                R.id.adView_InPronounceablePassword,
                R.id.drawer_layoutInPronounceablePasswordActivity,
                R.id.navigation_listInPronounceablePasswordActivity,
                R.drawable.password_key_with_background);
    }

    @Override
    protected void findAndSetViews() {
        numberOfPasswords_spinner = (Spinner) findViewById(R.id.spinner_InPronounceablePasswords);
        passwordLength_edit = (EditText) findViewById(R.id.pronounceablePasswordLength_edit);
        passwordLength_edit.setText("8");
        passwords_list = (ListView) findViewById(R.id.list_PronounceablePasswords);
        passwordsArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        passwords_list.setAdapter(passwordsArrayAdapter);
        numberOfPasswordsArrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, generateList());
        numberOfPasswords_spinner.setAdapter(numberOfPasswordsArrayAdapter);

    }

    private List<Integer> generateList() {
        List<Integer> integers = new ArrayList<>();
        for (int i = 1; i <=20 ; i++) {
            integers.add(i);
        }
        return integers;
    }

    @Override
    protected void listItemClick(int position) {
        copyToClipboard(list.get(position));
    }

    @Override
    protected String getSharableContent() {
        return null;
    }

    @Override
    protected boolean satisfiedMainButtonPreconditions() {
        if (passwordLength_edit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter password length", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int maxPasswordLength = 40;
            if (Integer.valueOf(passwordLength_edit.getText().toString()) > maxPasswordLength) {
                Toast.makeText(this, "Maximum password length is " + maxPasswordLength, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void setOnClickListener() {
        passwords_list.setOnItemClickListener(this);
    }

    @Override
    protected void setDataFromOriginatingActivity() {
        //nothing to do
    }

    @Override
    public void onClick(View v) {
    }

    public void generatePronounceablePassword(View view) {
        if (satisfiedMainButtonPreconditions()) {
            int passwordLength = Integer.valueOf(passwordLength_edit.getText().toString());
            int numberOfPasswords = (int) numberOfPasswords_spinner.getItemAtPosition(numberOfPasswords_spinner.getSelectedItemPosition());
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioSequence);
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            switch (checkedRadioButtonId) {
                case R.id.radioButton_CV:
                    updatePasswordsList(
                            PronounceablePasswordGenerator
                                    .generateCVPassword(passwordLength, numberOfPasswords)
                    );
                    break;
                case R.id.radioButtonCVV:
                    updatePasswordsList(
                            PronounceablePasswordGenerator
                                    .generateCVVPassword(passwordLength, numberOfPasswords));
                    break;
                case R.id.radioButton_custom:
                    break;
            }
        }
        hideKeyboard(numberOfPasswords_spinner);
        vibrate();
    }

    private void updatePasswordsList(List<String> generatedPasswords) {
        passwordsArrayAdapter = null;
        list.clear();
        list.addAll(generatedPasswords);
        passwordsArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        passwords_list.setAdapter(passwordsArrayAdapter);
        passwordsArrayAdapter.notifyDataSetChanged();
    }
}
