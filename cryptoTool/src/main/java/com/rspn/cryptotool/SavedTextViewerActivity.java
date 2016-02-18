package com.rspn.cryptotool;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.R.id;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.widget.ShareActionProvider;
import android.widget.Switch;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class SavedTextViewerActivity extends ActionBarActivity{

	EditText et;
	AdView adView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_savedtext);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = this.getIntent();

		et =  (EditText) findViewById(R.id.savedText);
		et.setText(intent.getStringExtra("text"));	

		//Ads
		adView = (AdView) this.findViewById(R.id.adView_InSavedTextsViewer);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_savedtext, menu);
		MenuItem item = (MenuItem)menu.findItem(R.id.share);

		return super.onCreateOptionsMenu(menu);
	}

	// TODO Auto-generated method stub



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.share){
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT,et.getText().toString());
			startActivity(shareIntent);
		}

		if(id==android.R.id.home){
			finish();
		}

		return super.onOptionsItemSelected(item);
	}










}