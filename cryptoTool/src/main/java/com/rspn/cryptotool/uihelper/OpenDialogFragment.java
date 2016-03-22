package com.rspn.cryptotool.uihelper;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.rspn.cryptotool.R;
import com.rspn.cryptotool.breakencryption.BreakEncryptionActivity;
import com.rspn.cryptotool.calculatehashes.CalculateHashesActivity;
import com.rspn.cryptotool.db.TextSamplesDataSource;
import com.rspn.cryptotool.decrypt.DecryptActivity;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OpenDialogFragment extends DialogFragment implements OnItemClickListener {

    private String textSampleTypeFilter;
    private ListView mylist;
    private TextSamplesDataSource datasource;
    private List<Text> textSamples;
    private static Map<String, String> titles;
    private EditText previewEditText = null;
    private Boolean isComparingHashes = false;

    static {
        titles = new HashMap<>();
        titles.put("EncryptedText", "Encrypted Texts");
        titles.put("PlainText", "Plain Texts");
        titles.put("All", "All Saved Texts");
    }

    public OpenDialogFragment() {
    }

    public static OpenDialogFragment newInstance(String request) {
        OpenDialogFragment fragment = new OpenDialogFragment();
        Bundle args = new Bundle();
        args.putString("filter", request);
        fragment.setArguments(args);
        return fragment;
    }

    public OpenDialogFragment(EditText previewEditText) {
        isComparingHashes = true;
        this.textSampleTypeFilter = "All";
        this.previewEditText = previewEditText;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_open_savedtextdialogfragment, null, false);
        mylist = (ListView) view.findViewById(R.id.list);
        textSampleTypeFilter = getArguments().getString("filter");

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selection = textSamples.get(position).getContent();
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
            if (isComparingHashes) {
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
