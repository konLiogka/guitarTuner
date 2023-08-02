package com.example.pitchdetection;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class CustomTuningFragment extends Fragment {

    public CustomTuningFragment() {

    }

    public static CustomTuningFragment newInstance( ) {
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
        EditText editText = view.findViewById(R.id.customName);

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

        for (Spinner spin : Arrays.asList(spin1,spin2,spin3,spin4,spin5,spin6)) {
            spin.setSelection(5);
        }

        Button cancel = view.findViewById(R.id.cancel);
        Button add = view.findViewById(R.id.add);
        MainActivity activity = (MainActivity) getActivity();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.onClickImageView();

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editText.getText().toString();

                String s6= spin6.getSelectedItem().toString();
                String s5= spin5.getSelectedItem().toString();
                String s4= spin4.getSelectedItem().toString();
                String s3= spin3.getSelectedItem().toString();
                String s2= spin2.getSelectedItem().toString();
                String s1= spin1.getSelectedItem().toString();


                String newTuning = name+"("+s6+" "+s5+" "+s4+" "+s3+" "+s2+" "+s1.toLowerCase()+")";
                activity.addTuning(newTuning, getActivity());
                activity.onClickImageView();


            }
        });









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
