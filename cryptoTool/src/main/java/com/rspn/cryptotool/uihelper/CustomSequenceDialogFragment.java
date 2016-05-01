package com.rspn.cryptotool.uihelper;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rspn.cryptotool.R;
import com.rspn.cryptotool.passwordgenerator.PronounceablePasswordActivity;

public class CustomSequenceDialogFragment extends DialogFragment implements OnClickListener {
    private TextView sequence_tv;
    private Button OK_bt;

    public CustomSequenceDialogFragment() {
    }

    public static CustomSequenceDialogFragment newInstance(String currentSequence) {
        CustomSequenceDialogFragment fragment = new CustomSequenceDialogFragment();
        Bundle args = new Bundle();
        args.putString("currentSequence", currentSequence);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_customsequence_dialogfragment, null);
        sequence_tv = (TextView) view.findViewById(R.id.textView_customSequence);
        OK_bt = (Button) view.findViewById(R.id.button_OK_CustomSequence);
        Button cancel_bt = (Button) view.findViewById(R.id.button_Cancel_CustomSequence);
        Button consonant_bt = (Button) view.findViewById(R.id.button_Consonant);
        Button vowel_bt = (Button) view.findViewById(R.id.button_Vowel);
        ImageButton backspace_bt = (ImageButton) view.findViewById(R.id.imageButton_backspace);

        OK_bt.setOnClickListener(this);
        cancel_bt.setOnClickListener(this);
        consonant_bt.setOnClickListener(this);
        vowel_bt.setOnClickListener(this);
        backspace_bt.setOnClickListener(this);

        OK_bt.setEnabled(false);
        setCurrentSequence();
        return view;
    }

    private void setCurrentSequence() {
        String currentSequence = getArguments().getString("currentSequence");
        if (currentSequence != null && !currentSequence.isEmpty()) {
            sequence_tv.setText(currentSequence);
            OK_bt.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_Consonant:
                sequence_tv.append("C");
                OK_bt.setEnabled(true);
                break;

            case R.id.button_Vowel:
                sequence_tv.append("V");
                OK_bt.setEnabled(true);
                break;
            case R.id.imageButton_backspace:
                String currentText = sequence_tv.getText().toString();
                if (!currentText.isEmpty()) {
                    sequence_tv.setText(currentText.substring(0, currentText.length() - 1));
                    if (sequence_tv.getText().toString().isEmpty()) {
                        OK_bt.setEnabled(false);
                    }
                }
                break;

            case R.id.button_OK_CustomSequence:
                PronounceablePasswordActivity callingActivity = (PronounceablePasswordActivity) getActivity();
                callingActivity.generatePronounceablePasswordFromCustomSequence(sequence_tv.getText().toString());
                dismiss();
                break;

            case R.id.button_Cancel_CustomSequence:
                dismiss();
                break;

        }


    }
}
