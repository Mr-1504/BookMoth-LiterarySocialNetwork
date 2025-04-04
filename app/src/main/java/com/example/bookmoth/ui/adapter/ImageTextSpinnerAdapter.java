package com.example.bookmoth.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookmoth.R;
import com.example.bookmoth.core.libraryutils.ValueGen;
import com.example.bookmoth.ui.viewdata.ImageTextItem;

import java.util.List;

public class ImageTextSpinnerAdapter extends ArrayAdapter<ImageTextItem> {
    private Context context;
    private List<ImageTextItem> items;

    public ImageTextSpinnerAdapter(@NonNull Context context, @NonNull List<ImageTextItem> objects) {
        super(context, 0, objects);
        this.items = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int pos, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_spinner_textandimage, parent, false);
        }

        FrameLayout display = view.findViewById(R.id.item_display);
        TextView text = view.findViewById(R.id.item_text);

        ImageTextItem item = items.get(pos);
        if (!item.isImage()) display.setBackgroundColor(Color.parseColor(ValueGen.makeTransparencyParseColorValue(0, item.getRid())));
        text.setText(item.getText());

        return view;
    }
}
