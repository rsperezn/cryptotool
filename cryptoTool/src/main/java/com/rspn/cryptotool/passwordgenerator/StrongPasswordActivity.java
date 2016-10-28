package com.rspn.cryptotool.passwordgenerator;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rspn.cryptotool.AbstractCryptActivity;
import com.rspn.cryptotool.R;

import java.util.ArrayList;
import java.util.List;

public class StrongPasswordActivity extends AbstractCryptActivity implements View.OnClickListener {

    private CheckBox lowerCase_cb;
    private CheckBox upperCase_cb;
    private CheckBox digits_cb;
    private CheckBox symbols_cb;
    private EditText passwordLength_edit;
    private TextView strongPassword_textView;
    private Spinner numberOfPasswords_spinner;
    private ListView strongPasswords_list;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> passwordsArrayAdapter;

    public StrongPasswordActivity() {
        super(R.layout.activity_strongpassword,
                R.id.adView_InStrongPassword,
                R.id.drawer_layoutInStrongPasswordActivity,
                R.id.navigation_listInStrongPasswordActivity,
                R.drawable.asteriscs);
    }

    @Override
    protected void findAndSetViews() {
        lowerCase_cb = (CheckBox) findViewById(R.id.checkBox_lowerCase);
        upperCase_cb = (CheckBox) findViewById(R.id.checkbox_upperCase);
        digits_cb = (CheckBox) findViewById(R.id.checkBox_digits);
        symbols_cb = (CheckBox) findViewById(R.id.checkBox_symbols);
        passwordLength_edit = (EditText) findViewById(R.id.strongPasswordLength_edit);
        passwordLength_edit.setText("8");
        strongPassword_textView = (TextView) findViewById(R.id.textView_strongPassword);
        numberOfPasswords_spinner = (Spinner) findViewById(R.id.spinner_InStrongPasswords);
        strongPasswords_list = (ListView) findViewById(R.id.list_StrongPasswords);
        passwordsArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        strongPasswords_list.setAdapter(passwordsArrayAdapter);
        ArrayAdapter<Integer> numberOfPasswordsArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, generateList());
        numberOfPasswords_spinner.setAdapter(numberOfPasswordsArrayAdapter);
        numberOfPasswords_spinner.setSelection(4);
    }

    @Override
    protected void listItemClick(int position) {
        copyToClipboard(list.get(position));
    }

    @Override
    protected String getShareableContent() {
        return list.toString();
    }

    @Override
    protected boolean satisfiedMainButtonPreconditions() {
        return atLeastOneCharacterTypeSelected() && validPasswordLength();
    }

    @Override
    protected void setOnClickListener() {
        strongPasswords_list.setOnItemClickListener(this);
    }

    @Override
    protected void setDataFromOriginatingActivity() {
        //nothing to do
    }

    @Override
    public void onClick(View v) {
    }

    public void generateStrongPassword(View view) {
        if (satisfiedMainButtonPreconditions()) {
            EditText password_edit = (EditText) findViewById(R.id.strongPasswordLength_edit);
            int passwordLength = Integer.valueOf(password_edit.getText().toString());
            int numberOfPasswords = (int) numberOfPasswords_spinner.getItemAtPosition(numberOfPasswords_spinner.getSelectedItemPosition());
            boolean excludeSimilarLookingCharacters = ((CheckBox) findViewById(R.id.checkBox_excludeSimilarLookingCharacters)).isChecked();
            Characters.Types[] checkedCharacterTypes = getCheckedCharacterTypes();
            if (checkedCharacterTypes.length > passwordLength) {
                passwordLength = checkedCharacterTypes.length;
                password_edit.setText(String.valueOf(passwordLength));
            }
            try {
                List<String> strongPasswords = PasswordGenerator.generatePassword(passwordLength,
                        numberOfPasswords,
                        excludeSimilarLookingCharacters,
                        checkedCharacterTypes);
                updatePasswordsList(strongPasswords);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            displayToastMessage(errorMessage);
        }
        hideKeyboard(strongPassword_textView);
        vibrate();
    }

    private Characters.Types[] getCheckedCharacterTypes() {
        List<Characters.Types> result = new ArrayList<>();

        if (lowerCase_cb.isChecked()) {
            result.add(Characters.Types.LOWER_CASE);
        }
        if (upperCase_cb.isChecked()) {
            result.add(Characters.Types.UPPER_CASE);
        }
        if (digits_cb.isChecked()) {
            result.add(Characters.Types.DIGITS);
        }
        if (symbols_cb.isChecked()) {
            result.add(Characters.Types.SYMBOLS);
        }
        return result.toArray(new Characters.Types[result.size()]);
    }

    private boolean validPasswordLength() {
        EditText passwordLength_edit = (EditText) findViewById(R.id.strongPasswordLength_edit);

        String passwordLength = passwordLength_edit.getText().toString();
        if (passwordLength.isEmpty()) {
            errorMessage = "Please enter password Length";
            return false;
        } else if (Integer.valueOf(passwordLength) > 1024 || Integer.valueOf(passwordLength) < 1) {
            errorMessage = "Password length must be between 1 and 1024";
            return false;
        }
        return true;
    }

    private boolean atLeastOneCharacterTypeSelected() {
        if (lowerCase_cb.isChecked()
                || upperCase_cb.isChecked()
                || digits_cb.isChecked()
                || symbols_cb.isChecked()) {
            return true;
        }
        errorMessage = "Please select at least one character type";
        return false;
    }

    private List<Integer> generateList() {
        List<Integer> integers = new ArrayList<>();
        for (int i = 1; i <=20 ; i++) {
            integers.add(i);
        }
        return integers;
    }

    private void updatePasswordsList(List<String> generatedPasswords) {
        passwordsArrayAdapter = null;
        list.clear();
        list.addAll(generatedPasswords);
        passwordsArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        strongPasswords_list.setAdapter(passwordsArrayAdapter);
        passwordsArrayAdapter.notifyDataSetChanged();
    }
}
