package com.example.pitchdetection;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class CustomTuningFragment extends Fragment {





    public CustomTuningFragment() {

    }


    public static CustomTuningFragment newInstance(String param1, String param2) {
        CustomTuningFragment fragment = new CustomTuningFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_tuning, container, false);
        EditText editText = view.findViewById(R.id.editTextText);

        Spinner spin6 = view.findViewById(R.id.spin6);
        setTextSpinner(spin6, R.array.spin6);

        Spinner spin5 = view.findViewById(R.id.spin5);
        setTextSpinner(spin5, R.array.spin5);

        Spinner spin4 = view.findViewById(R.id.spin4);
        setTextSpinner(spin4, R.array.spin4);

        Spinner spin3 = view.findViewById(R.id.spin3);
        setTextSpinner(spin3, R.array.spin3);

        Spinner spin2 = view.findViewById(R.id.spin2);
        setTextSpinner(spin2, R.array.spin2);

        Spinner spin1 = view.findViewById(R.id.spin1);
        setTextSpinner(spin1, R.array.spin1);








        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {

                int length = editText.getText().length();
                if(length>8){
                    return "";
                }
                for (int i = start;i < end;i++) {

                    if (!Character.isLetterOrDigit(source.charAt(i))     )
                    {
                        return "";
                    }

                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[] { filter });



        return view;
    }

    public void setTextSpinner(Spinner spinner, int  array){

        Context context = spinner.getContext();
        String[] dataArray = context.getResources().getStringArray(array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,  R.layout.custom_spinner_text,R.id.textView1, dataArray);


        spinner.setAdapter(adapter);
    }
}