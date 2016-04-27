package com.rspn.cryptotool.passwordgenerator;

import android.view.View;

import com.rspn.cryptotool.AbstractCryptActivity;
import com.rspn.cryptotool.R;

public class PronounceablePasswordActivity extends AbstractCryptActivity implements View.OnClickListener {

    public PronounceablePasswordActivity() {
        super(R.layout.activity_pronounceablepassword,
                R.id.adView_InPronounceablePassword,
                R.id.drawer_layoutInPronounceablePasswordActivity,
                R.id.navigation_listInPronounceablePasswordActivity,
                R.drawable.password_key_with_background);
    }

    @Override
    protected void findAndSetViews() {

    }

    @Override
    protected void drawerItemClick(int position) {
        //no need to override
    }

    @Override
    protected String getSharableContent() {
        return null;
    }

    @Override
    protected boolean satisfiedMainButtonPreconditions() {
        return false;
    }

    @Override
    protected void setOnClickListener() {
    }

    @Override
    protected void setDataFromOriginatingActivity() {
        //nothing to do
    }

    @Override
    public void onClick(View v) {
    }

    public void generateStrongPassword(View view) {

    }

}
