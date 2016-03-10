package com.rspn.cryptotool.uihelper;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.rspn.cryptotool.R;
import com.rspn.cryptotool.utils.CTUtils;

public class CryptInfoDialogFragment extends DialogFragment {

    private String url;
    private String title;
    private String request;
    private ProgressBar progressBar;

    public static CryptInfoDialogFragment newInstance(String request) {
        CryptInfoDialogFragment fragment = new CryptInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString("request", request);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Loading info...");
        View view = inflater.inflate(R.layout.activity_cryptinfo, null, false);
        WebView webView = (WebView) view.findViewById(R.id.webView_cryptInfo);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_LoadInfo);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.getSettings().setDisplayZoomControls(false);
        }
        request = getArguments().getString("request");
        setContent();
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#f27522"), android.graphics.PorterDuff.Mode.MULTIPLY);
        webView.loadUrl(url);
        webView.setWebViewClient(new CryptInfoViewClient());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout((int) (CTUtils.windowWidth * 0.95), (int) (CTUtils.windowHeight * 0.95));
    }

    private void setContent() {
        switch (request) {
            case "encryptCaesar":
                url = "http://en.wikipedia.org/wiki/Caesar_cipher";
                title = "Caesar Encryption";
                break;
            case "encryptVigenere":
                url = "http://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher#Description";
                title = "Vigenere Encryption";
                break;
            case "decryptCaesar":
                url = "http://en.wikipedia.org/wiki/Caesar_cipher";
                title = "Caesar Decryption";
                break;
            case "decryptVigenere":
                url = "http://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher#Description";
                title = "Vigenere Decryption";
                break;
            case "breakCaesar":
                url = "http://en.wikipedia.org/wiki/Caesar_cipher#Breaking_the_cipher";
                title = "Breaking Caesar Encryption";
                break;
            case "breakVigenere":
                url = "http://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher#Cryptanalysis";
                title = "Breaking Vigenere Encryption";
                break;
            case "calculateHashes":
                url = "http://en.wikipedia.org/wiki/Cryptographic_hash_function";
                title = "Hashing Algorithms";
                break;
        }
    }

    private class CryptInfoViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            try {
                getDialog().setTitle(title);
                Log.i("Title", title);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}



