package com.example.bookmoth.ui.activity.reader;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.viewmodel.reader.ReaderTextFormatViewModel;

import java.util.ArrayList;

public class TextFormatFragment extends Fragment {
    private ReaderTextFormatViewModel viewModel;

    private SeekBar textSize;
    private ArrayList<TextView> textDemos = new ArrayList<>();;
    private Spinner fontFamily;
    private ArrayList<String> availableFonts;
    private ArrayList<Integer> ridFonts;

    private float viewTextSize;
    private float defaultTextSize;
    private float sizePerStep;

    public TextFormatFragment() {}

    public static TextFormatFragment newInstance() {
        return new TextFormatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reader_textformat, container, false);

        textSize = v.findViewById(R.id.rdr_stg_textsize);
        textDemos.add(v.findViewById(R.id.textdemo_regular));
        textDemos.add(v.findViewById(R.id.textdemo_bold));
        textDemos.add(v.findViewById(R.id.textdemo_italic));
        textDemos.add(v.findViewById(R.id.textdemo_bolditalic));

        fontFamily = v.findViewById(R.id.rdr_sp_fontfamily);
        availableFonts = new ArrayList<>();
        availableFonts.add("Alegreya");
        availableFonts.add("Bitter");
        availableFonts.add("Bookely");
        availableFonts.add("Literata");
        ridFonts = new ArrayList<>();
        ridFonts.add(R.font.alegreya);
        ridFonts.add(R.font.bitter);
        ridFonts.add(R.font.bookerly);
        ridFonts.add(R.font.literata);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ReaderTextFormatViewModel.class);
        defaultTextSize = textDemos.get(0).getTextSize() / requireContext().getResources().getDisplayMetrics().scaledDensity;
        sizePerStep = defaultTextSize / 4;
        viewTextSize = viewModel.getTextSize().getValue();
        initialize();
    }

    private void initialize() {
        viewModel.getTextSize().observe(requireActivity(), v -> viewTextSize = v);

        float deltaSize = viewModel.getTextSize().getValue() - defaultTextSize;
        setDemoTextSize(viewTextSize);
        textSize.setProgress((int)(deltaSize / sizePerStep));

        textSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float delta = sizePerStep * i;
                setDemoTextSize(defaultTextSize + delta);
                viewModel.setTextSize(defaultTextSize + delta);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ArrayAdapter<String> fontListAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, availableFonts);
        fontFamily.setAdapter(fontListAdapter);
        fontFamily.setSelection(ridFonts.indexOf(viewModel.getFontFamily().getValue()));
        fontFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                setDemoFontFamily(ridFonts.get(pos));
                viewModel.setFontFamily(ridFonts.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setDemoTextSize(float value) {
        for (TextView tv: textDemos) tv.setTextSize(value);
    }
    private void setDemoFontFamily(int rid) {
        Typeface tf = ResourcesCompat.getFont(requireContext(), rid);
        textDemos.get(0).setTypeface(tf, Typeface.NORMAL);
        textDemos.get(1).setTypeface(tf, Typeface.ITALIC);
        textDemos.get(2).setTypeface(tf, Typeface.BOLD);
        textDemos.get(3).setTypeface(tf, Typeface.BOLD_ITALIC);
    }
}