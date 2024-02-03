package com.example.pitchdetection;


import static com.example.pitchdetection.TuningsList.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements PitchDetector.PitchDetectionListener, View.OnClickListener {

    private static final double MIN_PITCH_FREQUENCY = 40;
    private static final double MAX_PITCH_FREQUENCY = 500;
    private static final int    MAX_NOTE_CENTS = 500;

    public TextView       pitchTextView;
    public TextView       noteTextView;
    public TextView       noteFreqTextView;
    private PitchDetector pitchDetector;
    private Button        s1, s2, s3, s4, s5, s6;
    private String        tuningText = "Standard E (E2 A2 D3 G3 B3 e4)";
    private Button        selectedB;
    private Fragment      currentFragment = null;
    private List<String>  data;
    public static String[][] notesList = {

            {"E1", "41.02"},
            {"B1", "61.74"},
            {"C2", "65.41"},
            {"C#2", "69.30"},
            {"D2", "73.42"},
            {"D#2", "77.78"},
            {"E2", "82.41"},
            {"F2", "87.31"},
            {"F#2", "92.50"},
            {"G2", "98.00"},
            {"G#2", "103.83"},
            {"A2", "110.00"},
            {"A#2", "116.54"},
            {"B2", "123.47"},
            {"C3", "130.81"},
            {"C#3", "138.59"},
            {"D3", "146.83"},
            {"D#3", "155.56"},
            {"E3", "164.81"},
            {"F3", "174.61"},
            {"F#3", "185.00"},
            {"G3", "196.00"},
            {"G#3", "207.65"},
            {"A3", "220.00"},
            {"A#3", "233.08"},
            {"B3", "246.94"},
            {"C4", "261.63"},
            {"C#4", "277.18"},
            {"D4", "293.66"},
            {"D#4", "311.13"},
            {"E4", "329.63"},
            {"F4", "349.23"},
            {"F#4", "369.99"},
            {"G4", "392.00"},
            {"G#4", "415.30"},
            {"A4", "440.00"},


    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        pitchTextView = findViewById(R.id.freq);
        noteFreqTextView = findViewById(R.id.freq2);
        noteTextView = findViewById(R.id.note);

        // Initialising selected tuning (E Standard)
        s1 = findViewById(R.id.s1);
        s2 = findViewById(R.id.s2);
        s3 = findViewById(R.id.s3);
        s4 = findViewById(R.id.s4);
        s5 = findViewById(R.id.s5);
        s6 = findViewById(R.id.s6);
        s1.setText("E2");
        s2.setText("A2");
        s3.setText("D3");
        s4.setText("G3");
        s5.setText("B3");
        s6.setText("e4");
        selectedB = s1;
        changeColor(s1);
        updatePitchTextView(selectedB.getText().toString());

        // Settings onClick listener for each button for when a note is pressed.
        for (Button button : Arrays.asList(s1, s2, s3, s4, s5, s6)) {
            button.setOnClickListener(this);
        }
        pitchDetector = new PitchDetector();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 123);
        } else {
            pitchDetector.start(this);
        }
        pitchDetector.setPitchDetectionListener(this);

        // Get list of tunings
        data = getList(this);
        if(data.isEmpty()){
            data = TuningsList.fillList(this);
            saveList(this, data);
        }

        // Cardview for tunings
        CardView cardView = findViewById(R.id.tuningCardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickTuningsView();
            }
        });

        // Options for adding/editing tuning and onClick listener
        ImageView options = findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.PopupMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper, options);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                       if(menuItem.toString().equals("Add Tuning")){
                           onClickCustomTView("",0);
                       }else if(menuItem.toString().equals("Edit Tuning")){
                          onClickEditTView();
                       }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }



    // RecyclerView for selecting tuning
    public void onClickTuningsView() {
        if (currentFragment instanceof TuningFragment) {
            closeFragment(currentFragment);
        } else {
            openFragment(new TuningFragment(data), R.id.fragmentTuning);
            setBackground();

        }
    }

    //Fragment for adding custom tuning
    public void onClickCustomTView(String string, int position) {
        if (currentFragment instanceof CustomTuningFragment) {
            closeFragment(currentFragment);
        } else {
            openFragment(new CustomTuningFragment(string, position), R.id.fragmentCustomTuning);
        }
    }

    // Fragment for editing a view
    public void onClickEditTView(){
        if (currentFragment instanceof EditTuningFragment) {
            closeFragment(currentFragment);
        } else {
            openFragment(new EditTuningFragment(data), R.id.fragmentEditTuning);
        }
    }

    // Handling open/close fragment actions
    private void openFragment(Fragment fragment, int containerViewId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        pitchDetector.stop();
        fragmentTransaction.replace(containerViewId, fragment).commit();
        currentFragment = fragment;
    }

    private void closeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment).commit();
        pitchDetector.start(getApplicationContext());
        currentFragment = null;
    }
    // Handling pitch detector resume/pause actions
    @Override
    protected void onResume() {
        super.onResume();
        pitchDetector.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pitchDetector.stop();
    }
    // If a pitch is detected
    @Override
    public void onPitchDetected(final double pitchFrequency) {
        // Replace note text view with null
        if (selectedB != null) {
            noteTextView.setText(selectedB.getText().toString().replaceAll("\\d", ""));
        }
        // If pitch frequency within a certain range (so we filter out frequencies outside our notes list range).
        if (pitchFrequency > MIN_PITCH_FREQUENCY && pitchFrequency < MAX_PITCH_FREQUENCY) {
            double cents;
            double targetFrequency;
            // For every note:
            for (String[] strings : notesList) {
                // Calculate the cents based on target frequency (the note's frequency) and the pitch frequency we detected.
                targetFrequency = Double.parseDouble(strings[1]);
                cents = 1200 * Math.log(pitchFrequency / targetFrequency) / Math.log(2);
                // If tuning is set to automatic:
                if (tuningText.equals("Automatic Tuning")) {
                    // And if the cents are less than a quarter tone of that note then set the text views accordingly and update pointer.
                    if (Math.abs(cents) <= 50) {
                        pitchTextView.setText(String.format("%.2f", pitchFrequency) + " Hz");
                        noteFreqTextView.setText(String.format("%.2f", targetFrequency) + " Hz");
                        noteTextView.setText(strings[0]);
                        setPosition(cents);
                        break;
                    }
                // else if a tuning is selected, get the selected note and if the pitch frequency is within 500 cents update the position of the pointer.
                } else {
                    if (strings[0].equalsIgnoreCase(selectedB.getText().toString()) && (Math.abs(cents) <= MAX_NOTE_CENTS)) {
                        setPosition(cents);
                        break;
                    }
                }
            }
        }
    }

    // Setting the position of the pointer on the screen.
    private void setPosition(double cents) {
        ConstraintLayout visibleArea = findViewById(R.id.constraintL);
        int maxOffset =  600;
        double maxCents = 50.0;
        double minCents = -50.0;

        double rangeCents = maxCents - minCents;
        double offset = (cents - minCents) / rangeCents * (2 * maxOffset) - maxOffset;

        ImageView pointer = findViewById(R.id.pointer);
        int visibleWidth = visibleArea.getWidth();
        int indicatorWidth = pointer.getWidth();
        int maxVisibleOffset = visibleWidth - indicatorWidth;

        offset = Math.max(-maxVisibleOffset / 2.0, Math.min(maxVisibleOffset / 2.0, offset));
        float centerX = visibleArea.getX() + visibleWidth / 2f;
        float targetX = centerX + (float) offset;
        pointer.setTranslationX(targetX);
        // Tuning indicator based on where the pointer is on the screen
        if (!tuningText.equals("Automatic Tuning")) {
            // Applied threshold of 20%
            float threshold = 0.2f * centerX;

            if (targetX > centerX + threshold) {
                pitchTextView.setText("Tune DOWN!");
            } else if (targetX < centerX - threshold) {
                pitchTextView.setText("Tune UP!");
            } else {
                pitchTextView.setText("OK");
            }
        }



    }

    // Setting up the tuning when one is selected.
    public void setTuning(String[] notes, String text) {
        onClickTuningsView();
        setBackground();
        TextView tuning = findViewById(R.id.tuningText);
        tuning.setText(text);
        for (Button button : Arrays.asList(s1, s2, s3, s4, s5, s6)) {
            button.setText("");
        }
        tuningText = text;
        if (!tuningText.equals("Automatic Tuning")) {
            s1.setText(notes[0]);
            s2.setText(notes[1]);
            s3.setText(notes[2]);
            s4.setText(notes[3]);
            s5.setText(notes[4]);
            s6.setText(notes[5]);
            selectedB = s1;
            changeColor(s1);
            updatePitchTextView(selectedB.getText().toString());
            for (Button button : Arrays.asList(s1, s2, s3, s4, s5, s6)) {
                button.setOnClickListener(this);
            }
        }
    }

    // For when a tuning is selected from the tuning fragment
    @Override
    public void onClick(View v) {
        setBackground();
        if (tuningText != null) {
            for (Button button : Arrays.asList(s1, s2, s3, s4, s5, s6)) {
                if (!tuningText.equals("Automatic Tuning")) {
                    if (v.getId() == button.getId()) {
                        selectedB = button;
                        changeColor(button);
                        updatePitchTextView(selectedB.getText().toString());
                    }
                } else {
                    button.setOnClickListener(null);
                    selectedB = null;
                    return;
                }
            }

        }


    }

    // Updating the target note frequency
    private void updatePitchTextView(String selectedNote) {
        for (String[] strings : notesList) {
            if (strings[0].equalsIgnoreCase(selectedNote)) {
                double targetFrequency = Double.parseDouble(strings[1]);
                noteFreqTextView.setText(String.format("%.2f", targetFrequency) + " Hz");
                return;
            }
        }
    }

    // Setting the background of the buttons
    private void setBackground() {
        for (Button button : Arrays.asList(s1, s2, s3, s4, s5, s6)) {
            int color = Color.parseColor("#404040");
            button.setBackgroundTintList(ColorStateList.valueOf(color));
        }


    }
    // Changing the color of the selected button
    private void changeColor(Button button){
        int color;
        color = Color.parseColor("#A2FF86");
        button.setBackgroundTintList(ColorStateList.valueOf(color));
    }
    // Adding a new tuning to the list
   public void addTuning(String string, Context context){
       TuningsList.addToList(string, context);
       data = TuningsList.getList(context);
       onClickTuningsView();

   }

   // Edit an existing tuning
   public void replaceTuning(String string, Context context, int position){
       TuningsList.replaceToList(string, context,position);
       data = TuningsList.getList(context);
       onClickTuningsView();
   }
   // Delete a tuning from the list
   public void removeTuning(Context context, int position){
       data = TuningsList.removeFromList(context,position);
       onClickTuningsView();
   }

}

