package com.rspn.cryptotool.uihelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.rspn.cryptotool.R;
import com.rspn.cryptotool.breakencryption.BreakEncryptionActivity;
import com.rspn.cryptotool.db.TextSamplesDataSource;
import com.rspn.cryptotool.decrypt.DecryptActivity;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.Text;

import java.util.List;

import static com.rspn.cryptotool.utils.TextTypes.BET;
import static com.rspn.cryptotool.utils.TextTypes.DT;
import static com.rspn.cryptotool.utils.TextTypes.ET;
import static com.rspn.cryptotool.utils.TextTypes.PT;

public class SaveDialogFragment extends DialogFragment implements OnClickListener {
    private CheckBox input_cb, output_cb;
    private EditText inputTitle_edit, outputTitle_edit;
    private Button save_bt, cancel_bt;
    private Communicator communicator;

    public SaveDialogFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("static-access")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("What do you want to Save?");
        View view = inflater.inflate(R.layout.activity_save_dialogfragment, null);
        input_cb = (CheckBox) view.findViewById(R.id.checkBox_saveInput);
        output_cb = (CheckBox) view.findViewById(R.id.checkBox_saveOutput);
        inputTitle_edit = (EditText) view.findViewById(R.id.editText_inputTitle);
        outputTitle_edit = (EditText) view.findViewById(R.id.editText_outputTitle);
        save_bt = (Button) view.findViewById(R.id.button_Save);
        cancel_bt = (Button) view.findViewById(R.id.button_Cancel);

        input_cb.setOnClickListener(this);
        output_cb.setOnClickListener(this);
        save_bt.setOnClickListener(this);
        cancel_bt.setOnClickListener(this);

        inputTitle_edit.setVisibility(view.INVISIBLE);
        outputTitle_edit.setVisibility(view.INVISIBLE);
        save_bt.setEnabled(false);
        return view;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_Cancel:
                dismiss();
                break;
            case R.id.button_Save:
                TextSamplesDataSource dataSource = new TextSamplesDataSource(getActivity());
                dataSource.open();
                //need to know who created this object
                String parentClass = getActivity().getClass().getSimpleName();
                switch (parentClass) {
                    case "EncryptActivity": {
                        List<String> contentToSave = EncryptActivity.getTextsToSave(input_cb.isChecked(), output_cb.isChecked());
                        if (contentToSave.size() == 1) {
                            //check what type of text it is
                            Text text = new Text();
                            if (input_cb.isChecked()) {//saving a plainText
                                if (inputTitle_edit.getText().toString().trim().isEmpty()) {
                                    text.setTitle("Plain Text");
                                } else {
                                    text.setTitle(inputTitle_edit.getText().toString());
                                }
                                text.setType(PT.toString());
                                text.setContent(contentToSave.get(0));
                                text.setDeletable(1);
                                dataSource.create(text);
                            } else {//saving an EncryptedText
                                if (outputTitle_edit.getText().toString().trim().isEmpty()) {
                                    text.setTitle("Encrypted Text");
                                } else {
                                    text.setTitle(outputTitle_edit.getText().toString());
                                }
                                text.setType(ET.toString());
                                text.setContent(contentToSave.get(0));
                                text.setDeletable(1);
                                dataSource.create(text);
                            }
                        }
                        //if returning two strings, first one is plaintext second one is encrypted text
                        else {
                            //input
                            Text text = new Text();
                            if (inputTitle_edit.getText().toString().trim().isEmpty()) {
                                text.setTitle("Plain Text");
                            } else {
                                text.setTitle(inputTitle_edit.getText().toString());
                            }
                            text.setType(PT.toString());
                            text.setContent(contentToSave.get(0));
                            text.setDeletable(1);
                            dataSource.create(text);
                            //output
                            text = new Text();
                            if (outputTitle_edit.getText().toString().trim().isEmpty()) {
                                text.setTitle("Plain Text");
                            } else {
                                text.setTitle(outputTitle_edit.getText().toString());
                            }
                            text.setType(ET.toString());
                            text.setContent(contentToSave.get(1));
                            text.setDeletable(1);
                            dataSource.create(text);
                        }

                        break;
                    }
                    case "DecryptActivity": {
                        List<String> contentToSave = DecryptActivity.getTextsToSave(input_cb.isChecked(), output_cb.isChecked());
                        if (contentToSave.size() == 1) {
                            //check what type of text it is
                            Text text = new Text();
                            if (input_cb.isChecked()) {//saving a Encrypted Text
                                if (inputTitle_edit.getText().toString().trim().isEmpty()) {
                                    text.setTitle("Encrypted Text");
                                } else {
                                    text.setTitle(inputTitle_edit.getText().toString());
                                }
                                text.setType(ET.toString());
                                text.setContent(contentToSave.get(0));
                                text.setDeletable(1);
                                dataSource.create(text);
                            } else {//saving an Decrypted
                                if (outputTitle_edit.getText().toString().trim().isEmpty()) {
                                    text.setTitle("Decrypted Text");
                                } else {
                                    text.setTitle(outputTitle_edit.getText().toString());
                                }
                                text.setType(DT.toString());
                                text.setContent(contentToSave.get(0));
                                text.setDeletable(1);
                                dataSource.create(text);
                            }
                        }
                        //if returning two strings, first one is plaintext second one is encrypted text
                        else {
                            //input
                            Text text = new Text();
                            if (inputTitle_edit.getText().toString().trim().isEmpty()) {
                                text.setTitle("Encrypted Text");
                            } else {
                                text.setTitle(inputTitle_edit.getText().toString());
                            }
                            text.setType(ET.toString());
                            text.setContent(contentToSave.get(0));
                            text.setDeletable(1);
                            dataSource.create(text);
                            //output
                            text = new Text();
                            if (outputTitle_edit.getText().toString().trim().isEmpty()) {
                                text.setTitle("Decrypted Text");
                            } else {
                                text.setTitle(outputTitle_edit.getText().toString());
                            }
                            text.setType(DT.toString());
                            text.setContent(contentToSave.get(1));
                            text.setDeletable(1);
                            dataSource.create(text);
                        }
                        break;
                    }
                    default: //BreakEncryptionActivity
                        List<String> contentToSave = BreakEncryptionActivity.getTextsToSave(input_cb.isChecked(), output_cb.isChecked());
                        if (contentToSave.size() == 1) {
                            //check what type of text it is
                            Text text = new Text();
                            if (input_cb.isChecked()) {//saving a Encrypted Text
                                if (inputTitle_edit.getText().toString().trim().isEmpty()) {
                                    text.setTitle("Encrypted Text");
                                } else {
                                    text.setTitle(inputTitle_edit.getText().toString());
                                }
                                text.setType(ET.toString());
                                text.setContent(contentToSave.get(0));
                                text.setDeletable(1);
                                dataSource.create(text);
                            } else {//saving an Broken Encryption Text
                                if (outputTitle_edit.getText().toString().trim().isEmpty()) {
                                    text.setTitle("Broken Encryption Text");
                                } else {
                                    text.setTitle(outputTitle_edit.getText().toString());
                                }
                                text.setType(BET.toString());
                                text.setContent(contentToSave.get(0));
                                text.setDeletable(1);
                                dataSource.create(text);
                            }
                        } else {
                            //input
                            Text text = new Text();
                            if (inputTitle_edit.getText().toString().trim().isEmpty()) {
                                text.setTitle("Encrypted Text");
                            } else {
                                text.setTitle(inputTitle_edit.getText().toString());
                            }
                            text.setType(ET.toString());
                            text.setContent(contentToSave.get(0));
                            text.setDeletable(1);
                            dataSource.create(text);
                            //output
                            text = new Text();
                            if (outputTitle_edit.getText().toString().trim().isEmpty()) {
                                text.setTitle("Broken Encryption Text");
                            } else {
                                text.setTitle(outputTitle_edit.getText().toString());
                            }
                            text.setType(BET.toString());
                            text.setContent(contentToSave.get(1));
                            text.setDeletable(1);
                            dataSource.create(text);
                        }
                        break;
                }

                dataSource.close();
                communicator.onDialogMessage("Saved successfully");
                dismiss();
                break;
            case R.id.checkBox_saveInput:
                if (input_cb.isChecked()) {
                    inputTitle_edit.setVisibility(v.VISIBLE);
                    save_bt.setEnabled(true);
                } else {
                    inputTitle_edit.setVisibility(v.INVISIBLE);
                    if (output_cb.isChecked()) {
                        save_bt.setEnabled(true);
                    } else {
                        save_bt.setEnabled(false);
                    }
                }
                break;
            case R.id.checkBox_saveOutput:
                if (output_cb.isChecked()) {
                    outputTitle_edit.setVisibility(v.VISIBLE);
                    save_bt.setEnabled(true);

                } else {
                    outputTitle_edit.setVisibility(v.INVISIBLE);
                    if (input_cb.isChecked()) {
                        save_bt.setEnabled(true);
                    } else {
                        save_bt.setEnabled(false);
                    }
                }
                break;

            default:
                break;

        }

    }

    //for interfragment communication
    public interface Communicator {
        public void onDialogMessage(String message);

    }

}
