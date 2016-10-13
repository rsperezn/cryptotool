package com.rspn.cryptotool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rspn.cryptotool.breakencryption.BreakEncryptionActivity;
import com.rspn.cryptotool.calculatehashes.CalculateHashesActivity;
import com.rspn.cryptotool.decrypt.DecryptActivity;
import com.rspn.cryptotool.encrypt.EncryptActivity;
import com.rspn.cryptotool.model.CryptCategory;
import com.rspn.cryptotool.passwordgenerator.PronounceablePasswordActivity;
import com.rspn.cryptotool.passwordgenerator.StrongPasswordActivity;
import com.rspn.cryptotool.utils.CTUtils;

import java.util.List;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CryptRecyclerViewAdapter extends RecyclerView.Adapter<CryptRecyclerViewAdapter.DataObjectHolder> implements View.OnClickListener {
    private List<CryptCategory> cryptCategories;
    private Context context;
    public static final String ENCRYPT = "Encrypt Message";
    public static final String DECRYPT = "Decrypt Message";
    public static final String BREAK_ENCRYPTION = "Break Encrypted Message";
    public static final String FILE = "Generate of File";
    public static final String TEXT = "Generate of Text";
    public static final String STRONG_PASSWORD = "Generate Strong Password";
    public static final String PRONOUNCEABLE_PASSWORD = "Generate Pronounceable Password";

    public CryptRecyclerViewAdapter(List<CryptCategory> cryptCategories, Context context) {
        this.cryptCategories = cryptCategories;
        this.context = context;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        CryptCategory cryptCategory = cryptCategories.get(position);
        String category = cryptCategory.getTitle();
        holder.label.setText(category);
        addCategoryImageView(holder,category);
        List<String> subcategories = cryptCategory.getSubcategories();
        int totalNumberOfSubcategories = subcategories.size();
        for (int i = 0; i < totalNumberOfSubcategories; i++) {
            String subcategory = subcategories.get(i);
            addSubcategoryTextView(holder, subcategory);
            if (thereExistsMoreSubcategories(totalNumberOfSubcategories, i)) {
                addSubcategorySeparator(holder);
            }
        }
    }

    private void addSubcategorySeparator(DataObjectHolder holder) {
        View itemSeparator;
        itemSeparator = new View(context);
        RecyclerView.LayoutParams dividerLayoutParams = new RecyclerView.LayoutParams(
                MATCH_PARENT,
                3);
        itemSeparator.setLayoutParams(dividerLayoutParams);
        itemSeparator.setBackgroundColor(Color.LTGRAY);
        holder.linearLayout.addView(itemSeparator);
    }

    private void addCategoryImageView(DataObjectHolder holder, String subcategory) {
        switch (subcategory) {
            case CTUtils.CLASSICAL_CIPHER_TOOL:
                holder.imageview.setImageResource(R.drawable.lock_broken_orange);
                break;
            case CTUtils.HASH_GENERATOR:
                holder.imageview.setImageResource(R.drawable.hash);
                break;
            case CTUtils.PASSWORD_GENERATOR:
                holder.imageview.setImageResource(R.drawable.password_asteriks_orange);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        String selectedItem = ((TextView) v).getText().toString();
        switch (selectedItem) {
            case ENCRYPT: {
                Intent intent = new Intent(context, EncryptActivity.class);
                context.startActivity(intent);
                break;
            }
            case DECRYPT: {
                Intent intent = new Intent(context, DecryptActivity.class);
                context.startActivity(intent);
                break;
            }
            case BREAK_ENCRYPTION: {
                Intent intent = new Intent(context, BreakEncryptionActivity.class);
                context.startActivity(intent);

                break;
            }
            case FILE: {
                Intent intent = new Intent(context, CalculateHashesActivity.class);
                intent.putExtra("HashType", "File");
                context.startActivity(intent);
                break;
            }
            case TEXT: {
                Intent intent = new Intent(context, CalculateHashesActivity.class);
                intent.putExtra("HashType", "Text");
                context.startActivity(intent);

                break;
            }
            case STRONG_PASSWORD: {
                Intent intent = new Intent(context, StrongPasswordActivity.class);
                context.startActivity(intent);
                break;
            }

            case PRONOUNCEABLE_PASSWORD: {
                Intent intent = new Intent(context, PronounceablePasswordActivity.class);
                context.startActivity(intent);
                break;
            }
        }
    }

    private void addSubcategoryTextView(DataObjectHolder holder, String subcategory) {
        TextView subcategory_tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                MATCH_PARENT,
                140);
        layoutParams.setMargins(45, 0, 0, 0);
        subcategory_tv.setText(subcategory);
        subcategory_tv.setGravity(CENTER_VERTICAL);
        //TODO set item ID
        subcategory_tv.setOnClickListener(this);
        subcategory_tv.setLayoutParams(layoutParams);
        holder.linearLayout.addView(subcategory_tv);
    }

    private boolean thereExistsMoreSubcategories(int totalNumberOfSubcategories, int currentIteration) {
        int nextIteration = ++currentIteration;
        return nextIteration < totalNumberOfSubcategories;
    }

    @Override
    public int getItemCount() {
        return cryptCategories.size();
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView label;
        LinearLayout linearLayout;
        ImageView imageview;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textViewCryptGroup);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutCardView);
            imageview = (ImageView) itemView.findViewById(R.id.imageCryptGroup);
        }

    }
}