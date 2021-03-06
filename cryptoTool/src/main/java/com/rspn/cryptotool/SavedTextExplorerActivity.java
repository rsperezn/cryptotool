package com.rspn.cryptotool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.db.TextSamplesDataSource;
import com.rspn.cryptotool.model.Text;

import java.util.List;

import static com.rspn.cryptotool.utils.TextTypes.BET;
import static com.rspn.cryptotool.utils.TextTypes.DT;
import static com.rspn.cryptotool.utils.TextTypes.ET;
import static com.rspn.cryptotool.utils.TextTypes.PT;

public class SavedTextExplorerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int MENU_DELETE_ID = 100;
    private List<Text> texts;
    private TextSamplesDataSource dataSource;
    private Text textToDelete;
    private ListView listView;
    private MenuItem currentMenuItem;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_savedtexts);
        listView = (ListView) findViewById(R.id.list_SavedTexts);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
        dataSource = new TextSamplesDataSource(this);
        dataSource.open();

        AdView adView = (AdView) this.findViewById(R.id.adView_InExploreSavedTextsActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        texts = dataSource.findAll();
        refreshDisplay();
        setBackButton();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        int idSelected = (int) info.id;
        textToDelete = texts.get(idSelected);
        menu.add(0, MENU_DELETE_ID, 1, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_DELETE_ID && textToDelete.isDeletable() == 1) {
            boolean succeededDeleting = dataSource.delete(textToDelete.getId());
            if (!succeededDeleting) {
                Toast.makeText(this, "Failed Deleting", Toast.LENGTH_SHORT).show();
            }
            onOptionsItemSelected(currentMenuItem);
        } else {
            Toast.makeText(this, "Sample texts can't be deleted", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.explore_savedtexts, menu);
        currentMenuItem = menu.getItem(0);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, SavedTextViewerActivity.class);
        intent.putExtra("text", texts.get(position).getContent());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentMenuItem = item;
        int id = currentMenuItem.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_ExploreAll) {
            texts = dataSource.findAll();
            refreshDisplay();
            return true;
        }
        if (id == R.id.action_ExplorePlainText) {
            texts = dataSource.findFiltered("type='" + PT.toString() + "'", "title DESC");
            refreshDisplay();
            return true;
        }

        if (id == R.id.action_ExploreDecrypted) {
            texts = dataSource.findFiltered("type='" + DT.toString() + "'", "title DESC");
            refreshDisplay();
            return true;
        }
        if (id == R.id.action_ExploreEncrypted) {
            texts = dataSource.findFiltered("type='" + ET.toString() + "'", "title DESC");
            refreshDisplay();
            return true;
        }

        if (id == R.id.action_ExploreBrokenEncrypted) {
            texts = dataSource.findFiltered("type='" + BET.toString() + "'", "title DESC");
            refreshDisplay();
            return true;
        }

        if (id == R.id.action_ExploreSamples) {
            texts = dataSource.findSamples();
            refreshDisplay();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setBackButton() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void refreshDisplay() {
        ArrayAdapter<Text> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, texts);
        listView.setAdapter(adapter);
    }
}