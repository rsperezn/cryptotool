package com.rspn.cryptotool.uihelper;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.orleonsoft.android.simplefilechooser.Constants;
import com.orleonsoft.android.simplefilechooser.ui.FileChooserActivity;
import com.rspn.cryptotool.R;
import com.rspn.cryptotool.calculatehashes.CalculateHashesActivity;

public class OpenComparisonInputsFragment extends DialogFragment implements OnClickListener {

    private static final int FILE_CHOOSER = 0;
    private String comparisonType;
    private EditText input1_edit;
    private EditText input2_edit;
    private Button selectInput1_bt;
    private Button selectInput2_bt;
    private Button cancel_bt;
    private Button OK_bt;


    public OpenComparisonInputsFragment() {
    }

    public OpenComparisonInputsFragment(String comparisonType) {
        this.comparisonType = comparisonType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Compare Hashes of " + comparisonType + "s");
        View view = inflater.inflate(R.layout.activity_open_compare_imputs_fragment, null);
        input1_edit = (EditText) view.findViewById(R.id.compareInput_EditText1);
        input2_edit = (EditText) view.findViewById(R.id.compareInput_EditText2);
        selectInput1_bt = (Button) view.findViewById(R.id.buttonSelect_Input1);
        selectInput2_bt = (Button) view.findViewById(R.id.buttonSelect_Input2);
        cancel_bt = (Button) view.findViewById(R.id.button_Cancel_CompareHashes);
        OK_bt = (Button) view.findViewById(R.id.button_OK_CompareHashes);

        selectInput1_bt.setOnClickListener(this);
        selectInput2_bt.setOnClickListener(this);
        cancel_bt.setOnClickListener(this);
        OK_bt.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSelect_Input1:
                if (comparisonType.equals("Text")) {
                    OpenDialogFragment dialog = new OpenDialogFragment(input1_edit);
                    dialog.show(this.getFragmentManager(), "dialog");
                } else {
                    Intent intent = new Intent(getActivity(), FileChooserActivity.class);
                    startActivityForResult(intent, v.getId());
                }

                break;
            case R.id.buttonSelect_Input2:
                if (comparisonType.equals("Text")) {
                    OpenDialogFragment dialog = new OpenDialogFragment(input2_edit);
                    dialog.show(this.getFragmentManager(), "dialog");
                } else {
                    Intent intent = new Intent(getActivity(), FileChooserActivity.class);
                    startActivityForResult(intent, v.getId());
                }
                break;

            case R.id.button_Cancel_CompareHashes:
                dismiss();
                break;

            case R.id.button_OK_CompareHashes:
                CalculateHashesActivity callingActivity = (CalculateHashesActivity) getActivity();
                callingActivity.setInputsForHashComparison(input1_edit.getText().toString(), input2_edit.getText().toString());
                dismiss();
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == android.app.Activity.RESULT_OK) {
            String fileSelected = data.getStringExtra(Constants.KEY_FILE_SELECTED);
            switch (requestCode) {
                case R.id.buttonSelect_Input1:
                    input1_edit.setText(fileSelected);
                    break;
                case R.id.buttonSelect_Input2:
                    input2_edit.setText(fileSelected);
                    break;
                default:
                    break;
            }
        }
    }

}
