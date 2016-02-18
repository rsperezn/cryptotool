package com.rspn.cryptotool.UIHelper;

import java.util.HashMap;
import java.util.List;

import com.rspn.cryptotool.R;
import com.rspn.cryptotool.R.id;
import com.rspn.cryptotool.R.layout;
import com.rspn.cryptotool.breakencryption.BreakEncryptionActivity;
import com.rspn.cryptotool.calculatehashes.CalculateHashesActivity;
import com.rspn.cryptotool.db.TextSamplesDataSource;
import com.rspn.cryptotool.decrypt.DecryptActivity;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.Text;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class OpenDialogFragment extends DialogFragment implements OnItemClickListener {

    String textSampleTypeFilter;

    ListView mylist;
    TextSamplesDataSource datasource;
    List<Text> textSamples;
    HashMap<String, String> titles = new HashMap<>();
    EditText previewEditText = null;
    Boolean isComparingHahes = false;

    public OpenDialogFragment() {

    }

    public OpenDialogFragment(String textSampleTypeFilter) {
        this.textSampleTypeFilter = textSampleTypeFilter;
    }

    public OpenDialogFragment(EditText previewEditText) {
        isComparingHahes = true;
        this.textSampleTypeFilter = "All";
        this.previewEditText = previewEditText;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        titles.put("EncryptedText", "Encrypted Texts");
        titles.put("PlainText", "Plain Texts");
        titles.put("All", "All Saved Texts");


        View view = inflater.inflate(R.layout.activity_open_savedtextdialogfragment, null, false);
        mylist = (ListView) view.findViewById(R.id.list);

        registerForContextMenu(view);
        getDialog().setTitle(titles.get(textSampleTypeFilter) + " samples");//title

        //database
        datasource = new TextSamplesDataSource(getActivity());
        datasource.open();
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (textSampleTypeFilter.equals(("All"))) {
            textSamples = datasource.findAll();
        } else {
            textSamples = datasource.findFiltered("type='" + textSampleTypeFilter + "'", "title ASC");
        }

        SavedTextArrayAdapter adapter = new SavedTextArrayAdapter(getActivity(), textSamples);
        mylist.setAdapter(adapter);
        mylist.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        String selection = textSamples.get(position).getContent().toString();
        if (textSampleTypeFilter.equals("PlainText")) {//calling activity EncryptActivity
            EncryptActivity callingActivity = (EncryptActivity) getActivity();
            callingActivity.onUserSelectedTextSample(selection);
        } else if (textSampleTypeFilter.equals("EncryptedText")) {//calling activity DecryptActivity
            if (getActivity().getClass().getSimpleName().equals("BreakEncryptionActivity")) {
                BreakEncryptionActivity callingActivity = (BreakEncryptionActivity) getActivity();
                callingActivity.onUserSelectedTextSample(selection);

            } else {
                DecryptActivity callingActivity = (DecryptActivity) getActivity();
                callingActivity.onUserSelectedTextSample(selection);
            }
        } else {// All saved Texts called by HashActivity
            //TODO check if its comparing hashes of texts or files
            if (isComparingHahes) {
                previewEditText.setText(selection);
            } else {
                CalculateHashesActivity callingActivity = (CalculateHashesActivity) getActivity();
                callingActivity.setInputForHashCalculation(selection);
            }
        }
        datasource.close();
        dismiss();
    }

}
