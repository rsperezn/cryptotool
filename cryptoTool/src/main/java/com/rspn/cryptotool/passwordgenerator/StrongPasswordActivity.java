package com.rspn.cryptotool.passwordgenerator;

import com.rspn.cryptotool.R;

public class StrongPasswordActivity extends AbstractCryptActivity {

    public StrongPasswordActivity() {
        super(R.layout.activity_strongpassword,
                R.id.adView_InStrongPassword,
                R.id.drawer_layoutInStrongPasswordActivity);
    }

    @Override
    protected void onClickMainActionButton() {

    }

    @Override
    protected void drawerItemClick() {

    }

    @Override
    protected String getSharableContent() {
        return "hi";
    }
}
