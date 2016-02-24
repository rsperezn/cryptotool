package com.rspn.cryptotool.passwordgenerator;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.rspn.cryptotool.R;

import java.util.ArrayList;
import java.util.List;

public class StrongPasswordActivity extends AbstractCryptActivity {

    private CheckBox lowerCase_cb;
    private CheckBox upperCase_cb;
    private CheckBox digits_cb;
    private CheckBox symbols_cb;
    private EditText strongPassword_edit;

    public StrongPasswordActivity() {
        super(R.layout.activity_strongpassword,
                R.id.adView_InStrongPassword,
                R.id.drawer_layoutInStrongPasswordActivity);
    }

    @Override
    protected void findViews() {
        lowerCase_cb = (CheckBox) findViewById(R.id.checkBox_lowerCase);
        upperCase_cb = (CheckBox) findViewById(R.id.checkbox_upperCase);
        digits_cb = (CheckBox) findViewById(R.id.checkBox_digits);
        symbols_cb = (CheckBox) findViewById(R.id.checkBox_symbols);
        strongPassword_edit = (EditText) findViewById(R.id.edit_strongPassword);
    }

    @Override
    protected void drawerItemClick() {
        //no need to override
    }

    @Override
    protected String getSharableContent() {
        return strongPassword_edit.getText().toString();
    }

    @Override
    protected boolean satisfiedMainButtonPreconditions() {
        return atLeastOneCharacterTypeSelected() && validPasswordLength();
    }

    public void generateStrongPassword(View view) {
        if (satisfiedMainButtonPreconditions()) {
            int passwordLength = Integer.valueOf(((EditText) findViewById(R.id.strongPasswordLength_edit)).getText().toString());
            boolean excludeSimilarLookingCharacters = ((CheckBox) findViewById(R.id.checkBox_excludeSimilarLookingCharacters)).isChecked();

            try {
                String strongPassword = PasswordGenerator.generatePassword(passwordLength,
                        excludeSimilarLookingCharacters,
                        getCheckedCharacterTypes());
                strongPassword_edit.setText(strongPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            displayToastMessage(errorMessage);
        }
        hideKeyboard(strongPassword_edit);
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
}
