package com.rspn.cryptotool.uihelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rspn.cryptotool.R;
import com.rspn.cryptotool.model.Text;

import java.util.List;

public class SavedTextArrayAdapter extends ArrayAdapter<Text> {

    Context context;
    List<Text> texts;

    public SavedTextArrayAdapter(Context context, List<Text> texts) {
        super(context, R.layout.single_row_savedtext, texts);
        this.context = context;
        this.texts = texts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_row_savedtext, null);

        Text text = texts.get(position);

        TextView textView = (TextView) view.findViewById(R.id.TitleTextView);
        textView.setText(text.getTitle());

        textView = (TextView) view.findViewById(R.id.ContentTextView);
        String preview = text.getContent().length() > 40 ? text.getContent().substring(0, 35) + " ..." : text.getContent();
        textView.setText(preview);

        return view;
    }

}
