package com.rspn.cryptotool.UIHelper;

import com.rspn.cryptotool.breakencryption.BreakEncryptionActivity;
import com.rspn.cryptotool.calculatehashes.CalculateHashesActivity;
import com.rspn.cryptotool.decrypt.DecryptActivity;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.CryptGroup;
import com.rspn.cryptotool.passwordgenerator.Characters.Types;
import com.rspn.cryptotool.passwordgenerator.PasswordGenerator;
import com.rspn.cryptotool.R;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

public class CryptExpandableListAdapter extends BaseExpandableListAdapter {

    private final SparseArray<CryptGroup> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public CryptExpandableListAdapter(Activity act, SparseArray<CryptGroup> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText(children);
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (children.equals("Encrypt")) {
                    Intent intent = new Intent(activity, EncryptActivity.class);
                    activity.startActivity(intent);
                } else if (children.equals("Decrypt")) {
                    Intent intent = new Intent(activity, DecryptActivity.class);
                    activity.startActivity(intent);
                } else if (children.equals("Break Encryption")) {
                    Intent intent = new Intent(activity, BreakEncryptionActivity.class);
                    activity.startActivity(intent);

                } else if (children.equals("File")) {
                    Intent intent = new Intent(activity, CalculateHashesActivity.class);
                    intent.putExtra("HashType", "File");
                    activity.startActivity(intent);
                } else if (children.equals("Text")) {
                    Intent intent = new Intent(activity, CalculateHashesActivity.class);
                    intent.putExtra("HashType", "Text");
                    activity.startActivity(intent);

                } else if (children.equals("Strong Password")) {
                    try {
                        String password = PasswordGenerator.generatePassword(
                                8, false,
                                Types.UPPER_CASE,
                                Types.LOWER_CASE,
                                Types.DIGITS);
                        Log.i("Password", password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        CryptGroup group = (CryptGroup) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
