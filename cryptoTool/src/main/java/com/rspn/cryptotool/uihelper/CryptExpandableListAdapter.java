package com.rspn.cryptotool.uihelper;

import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.rspn.cryptotool.R;
import com.rspn.cryptotool.breakencryption.BreakEncryptionActivity;
import com.rspn.cryptotool.calculatehashes.CalculateHashesActivity;
import com.rspn.cryptotool.decrypt.DecryptActivity;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.CryptGroup;
import com.rspn.cryptotool.passwordgenerator.StrongPasswordActivity;

public class CryptExpandableListAdapter extends BaseExpandableListAdapter {

    public static final String ENCRYPT = "Encrypt";
    public static final String DECRYPT = "Decrypt";
    public static final String BREAK_ENCRYPTION = "Break Encryption";
    public static final String FILE = " Generate of File";
    public static final String TEXT = "Generate of Text";
    public static final String STRONG_PASSWORD = "Generate Strong Password";
    private final SparseArray<CryptGroup> groups;
    private LayoutInflater inflater;
    private Activity activity;

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
        final String child = (String) getChild(groupPosition, childPosition);
        TextView textView;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        textView = (TextView) convertView.findViewById(R.id.textView_CryptExpandableListItem);
        textView.setText(child);
        textView.setCompoundDrawablesWithIntrinsicBounds(getChildDrawable(child), 0, 0, 0);
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (child) {
                    case ENCRYPT: {
                        Intent intent = new Intent(activity, EncryptActivity.class);
                        activity.startActivity(intent);
                        break;
                    }
                    case DECRYPT: {
                        Intent intent = new Intent(activity, DecryptActivity.class);
                        activity.startActivity(intent);
                        break;
                    }
                    case BREAK_ENCRYPTION: {
                        Intent intent = new Intent(activity, BreakEncryptionActivity.class);
                        activity.startActivity(intent);

                        break;
                    }
                    case FILE: {
                        Intent intent = new Intent(activity, CalculateHashesActivity.class);
                        intent.putExtra("HashType", "File");
                        activity.startActivity(intent);
                        break;
                    }
                    case TEXT: {
                        Intent intent = new Intent(activity, CalculateHashesActivity.class);
                        intent.putExtra("HashType", "Text");
                        activity.startActivity(intent);

                        break;
                    }
                    case STRONG_PASSWORD: {
                        Intent intent = new Intent(activity, StrongPasswordActivity.class);
                        activity.startActivity(intent);
                        break;
                    }
                }
            }
        });
        return convertView;
    }

    private int getChildDrawable(String child) {
        switch (child) {

            case ENCRYPT: {
                return R.drawable.lock_closed;
            }
            case DECRYPT: {
                return R.drawable.lock_open;
            }
            case BREAK_ENCRYPTION: {
                return R.drawable.lock_broken;
            }
            case FILE: {
                return R.drawable.folder_icon;
            }
            case TEXT: {
                return R.drawable.text_icon;
            }
            case STRONG_PASSWORD: {
                return R.drawable.password_asteriks;
            }
            default:
                return R.drawable.lock_closed;
        }
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
