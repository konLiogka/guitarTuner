package com.example.pitchdetection;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.text.DecimalFormat;
import java.util.Arrays;

public class PitchDetector {
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 1024 * 4;

    private AudioRecord audioRecord;
    private boolean isRecording = false;

    private PitchDetectionListener listener;
    private Handler handler;

    public interface PitchDetectionListener {
        void onPitchDetected(double pitchFrequency);
    }

    public void setPitchDetectionListener(PitchDetectionListener listener) {
        this.listener = listener;
    }

    public void start(Context context) {

        if (isRecording) return;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        audioRecord.startRecording();
        isRecording = true;

        handler = new Handler(Looper.getMainLooper());
        handler.post(updatePitch);
    }

    public void stop() {
        if (!isRecording) return;

        audioRecord.stop();
        audioRecord.release();
        isRecording = false;

        if (handler != null) {
            handler.removeCallbacks(updatePitch);
            handler = null;
        }
    }

    private final Runnable updatePitch = new Runnable() {
        @Override
        public void run() {
            short[] buffer = new short[BUFFER_SIZE];
            int bytesRead = audioRecord.read(buffer, 0, BUFFER_SIZE);

            if (bytesRead > 0) {
                double pitchFrequency = computePitchFrequency(buffer);
                if (listener != null) {
                    listener.onPitchDetected(pitchFrequency);
                }
            }

            if (isRecording) {
                handler.post(this);
            }
        }
    };


    private double computePitchFrequency(short[] audioBuffer) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double[] buffer = new double[audioBuffer.length];
        for (int i = 0; i < audioBuffer.length; i++) {
            buffer[i] = audioBuffer[i] / 32768.0; // Convert from short to double in the range [-1, 1]
        }

        int bufferSize = buffer.length;
        double[] difference = new double[bufferSize];
        double[] cumulativeMeanNormalizedDifference = new double[bufferSize];
        double[] dMean = new double[bufferSize];


        // Autocorrelation function
        for (int lag = 0; lag < bufferSize; lag++) {
            for (int index = 0; index < bufferSize - lag; index++) {
                double diff = buffer[index] - buffer[index + lag];
                difference[lag] += diff * diff;
            }
        }

        // Cumulative mean normalized difference function
        cumulativeMeanNormalizedDifference[0] = 1;
        for (int lag = 1; lag < bufferSize; lag++) {
            double cmndf = 0;

            for (int index = 1; index <= lag; index++) {
                cmndf += difference[index];
            }
            cumulativeMeanNormalizedDifference[lag] = difference[lag] / (cmndf / lag);

        }

        double interpolatedPeak = calculateInterpolatedPeak(cumulativeMeanNormalizedDifference, bufferSize, dMean);


        double pitchFrequency = SAMPLE_RATE / interpolatedPeak;


        if (listener != null) {
            listener.onPitchDetected(pitchFrequency);
        }

        return pitchFrequency;


    }

    public double calculateInterpolatedPeak(double[] cumulativeMeanNormalizedDifference, int bufferSize, double[] dMean) {
        // Absolute threshold and octave-based threshold
        double threshold = 0.25;
        int pitchPeriod = 0;
        for (int lag = 1; lag < bufferSize; lag++) {
            if (cumulativeMeanNormalizedDifference[lag] < threshold) {
                pitchPeriod = lag;
                break;
            }
        }

        // Octave-based thresholding
        int subOctaves = 5;
        int subOctaveSize = bufferSize / subOctaves;
        int subOctaveStart = (pitchPeriod / subOctaveSize) * subOctaveSize;
        int subOctaveEnd = subOctaveStart + subOctaveSize;
        for (int lag = subOctaveStart + 1; lag < subOctaveEnd; lag++) {
            if (cumulativeMeanNormalizedDifference[lag] < cumulativeMeanNormalizedDifference[pitchPeriod]) {
                pitchPeriod = lag;
            }
        }

        // Multiple parabolic interpolations
        int numInterpolations = 16;
        double interpolatedPeak = pitchPeriod;
        for (int iteration = 0; iteration < numInterpolations; iteration++) {
            interpolatedPeak = pitchPeriod;
            if (pitchPeriod > 1 && pitchPeriod < bufferSize - 1) {
                double delta = dMean[pitchPeriod + 1] - dMean[pitchPeriod - 1];
                double thresholdDelta = 0.060* cumulativeMeanNormalizedDifference[pitchPeriod]; // Adjust the threshold as needed
                if (delta != 0 && Math.abs(dMean[pitchPeriod] - dMean[pitchPeriod - 1]) <= thresholdDelta && Math.abs(dMean[pitchPeriod] - dMean[pitchPeriod + 1]) <= thresholdDelta) {
                    interpolatedPeak += (dMean[pitchPeriod - 1] - dMean[pitchPeriod + 1]) / (2 * delta);
                }
            }
            pitchPeriod = (int) interpolatedPeak;

        }
       return interpolatedPeak;
    }


}




