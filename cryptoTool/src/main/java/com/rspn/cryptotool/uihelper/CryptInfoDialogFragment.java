package com.rspn.cryptotool.uihelper;

import com.rspn.cryptotool.R;
import com.rspn.cryptotool.utils.CTUtils;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class CryptInfoDialogFragment extends DialogFragment {

    String Url;
    String title;

    public CryptInfoDialogFragment() {
    }

    public CryptInfoDialogFragment(String request) {
        if (request.equals("encryptCaesar")) {
            Url = "http://en.wikipedia.org/wiki/Caesar_cipher";
            title = "Caesar Encryption";
        } else if (request.equals("encryptVigenere")) {
            Url = "http://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher#Description";
            title = "Vigenere Encryption";
        } else if (request.equals("decryptCaesar")) {
            Url = "http://en.wikipedia.org/wiki/Caesar_cipher";
            title = "Caesar Decryption";
        } else if (request.equals("decryptVigenere")) {
            Url = "http://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher#Description";
            title = "Vigenere Decryption";
        } else if (request.equals("breakCaesar")) {
            Url = "http://en.wikipedia.org/wiki/Caesar_cipher#Breaking_the_cipher";
            title = "Breaking Caesar Encryption";
        } else if (request.equals("breakVigenere")) {
            Url = "http://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher#Cryptanalysis";
            title = "Breaking Vigenere Encryption";
        } else if (request.equals("calculateHashes")) {
            Url = "http://en.wikipedia.org/wiki/Cryptographic_hash_function";
            title = "Hashing Algorithms";
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Loading info...");
        View view = inflater.inflate(R.layout.activity_cryptinfo, null, false);
        WebView webView = (WebView) view.findViewById(R.id.webView_cryptInfo);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar_LoadInfo);
        //Scroll bars should not be hidden  
        webView.setScrollbarFadingEnabled(false);
        //Disable the horizontal scroll bar  
        webView.setHorizontalScrollBarEnabled(false);
        //Enable JavaScript 
        webView.getSettings().setJavaScriptEnabled(true); //enable javascript
        //Set the user agent  
        webView.getSettings().setUserAgentString("AndroidWebView");
        //Clear the cache  
        webView.clearCache(true);
        //Make the webview load the specified URL 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            webView.getSettings().setDisplayZoomControls(false);

        webView.setWebViewClient(new CryptInfoViewClient(progressBar));
        webView.loadUrl(Url);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout((int) (CTUtils.windowWidth * 0.95), (int) (CTUtils.windowHeight * 0.95));

    }

    private class CryptInfoViewClient extends WebViewClient {
        private ProgressBar progressBar;

        public CryptInfoViewClient(ProgressBar progressBar) {
            this.progressBar = progressBar;
            this.progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#f27522"), android.graphics.PorterDuff.Mode.MULTIPLY);

            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            getDialog().setTitle(title);
        }
    }


}



