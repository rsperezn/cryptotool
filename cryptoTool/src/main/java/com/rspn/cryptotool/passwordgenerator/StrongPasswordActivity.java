package com.rspn.cryptotool.passwordgenerator;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.rspn.cryptotool.R;

import java.util.ArrayList;
import java.util.List;

public class StrongPasswordActivity extends AbstractCryptActivity {

    public StrongPasswordActivity() {
        super(R.layout.activity_strongpassword,
                R.id.adView_InStrongPassword,
                R.id.drawer_layoutInStrongPasswordActivity);
    }

    @Override
    protected void drawerItemClick() {
        //no need to override
    }

    @Override
    protected String getSharableContent() {
        return "hi";
    }

    public void generateStrongPassword(View view) {
        int passwordLength = Integer.valueOf(((EditText) findViewById(R.id.strongPasswordLength_edit)).getText().toString());
        boolean excludeSimilarLookingCharacters = ((CheckBox) findViewById(R.id.checkBox_excludeSimilarLookingCharacters)).isChecked();
        EditText strongPassword_edit = (EditText) findViewById(R.id.edit_strongPassword);

        try {
            String strongPassword = PasswordGenerator.generatePassword(passwordLength,
                    excludeSimilarLookingCharacters,
                    getCheckedCharacterTypes());
            strongPassword_edit.setText(strongPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Characters.Types[] getCheckedCharacterTypes() {
        List<Characters.Types> result = new ArrayList<>();

        CheckBox lowerCase_cb = (CheckBox) findViewById(R.id.checkBox_lowerCase);
        CheckBox upperCase_cb = (CheckBox) findViewById(R.id.checkbox_upperCase);
        CheckBox digits_cb = (CheckBox) findViewById(R.id.checkBox_digits);
        CheckBox symbols_cb = (CheckBox) findViewById(R.id.checkBox_symbols);

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
}
