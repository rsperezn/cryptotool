package com.rspn.cryptotool;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.db.TextSamplesDataSource;
import com.rspn.cryptotool.model.Text;
import com.rspn.cryptotool.uihelper.SavedTextArrayAdapter;

import java.util.List;

import static com.rspn.cryptotool.utils.TextTypes.BET;
import static com.rspn.cryptotool.utils.TextTypes.DT;
import static com.rspn.cryptotool.utils.TextTypes.ET;
import static com.rspn.cryptotool.utils.TextTypes.PT;

public class SavedTextExplorerActivity extends ListActivity {

    private static final int MENU_DELETE_ID = 100;
    List<Text> texts;
    TextSamplesDataSource datasource;
    Text textToDelete;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_savedtexts);
        datasource = new TextSamplesDataSource(this);
        datasource.open();

        //Ads
        adView = (AdView) this.findViewById(R.id.adView_InExploreSavedTextsActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        texts = datasource.findAll();
        refreshDisplay();
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        int idSelected = (int) info.id;
        textToDelete = texts.get(idSelected);
        if (textToDelete.getDeletable() == 1) {
            menu.add(0, MENU_DELETE_ID, 1, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_DELETE_ID) {
            boolean result = datasource.delete(textToDelete.getId());
            if (result) {
                Toast.makeText(this, "Deleted Succesfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed Deleting", Toast.LENGTH_SHORT).show();
            }
            refreshDisplay();
        }


        return super.onContextItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, SavedTextViewerActivity.class);
        intent.putExtra("text", texts.get(position).getContent());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.explore_savedtexts, menu);
        //TODO add action bar http://stackoverflow.com/questions/13875090/actionbar-and-listactivity-in-one-activity
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_ExploreAll) {
            texts = datasource.findAll();
            refreshDisplay();
            return true;
        }
        if (id == R.id.action_ExplorePlainText) {
            texts = datasource.findFiltered("type='" + PT.toString() + "'", "title DESC");
            refreshDisplay();
            return true;
        }

        if (id == R.id.action_ExploreDecrypted) {
            texts = datasource.findFiltered("type='" + DT.toString() + "'", "title DESC");
            refreshDisplay();
            return true;
        }
        if (id == R.id.action_ExploreEncrypted) {
            texts = datasource.findFiltered("type='" + ET.toString() + "'", "title DESC");
            refreshDisplay();
            return true;
        }

        if (id == R.id.action_ExploreBrokenEncrypted) {
            texts = datasource.findFiltered("type='" + BET.toString() + "'", "title DESC");
            refreshDisplay();
            return true;
        }

        if (id == R.id.action_ExploreSamples) {
            texts = datasource.findSamples();
            refreshDisplay();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshDisplay() {
        ArrayAdapter<Text> adapter = new SavedTextArrayAdapter(this, texts);
        setListAdapter(adapter);
    }

}
