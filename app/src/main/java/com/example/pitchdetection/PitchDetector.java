package com.example.pitchdetection;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.ActivityCompat;

 
//Still WIP for some reason its having trouble detecting lower notes when plucking with a pick. Applying Hamming windowing helped a lot with octave jumping but its still present in some cases
//My best option is to introduce manual tuning and tuning based on predefined frequencies, for example Standard E tuning.

public class PitchDetector {
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 1024 * 4;
    public short[] buffer = new short[BUFFER_SIZE];
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

        // Hamming Windowing for reducing distortion
        double[] windowedBuffer = new double[audioBuffer.length];
        double alpha = 0.6;
        double beta = 1 - alpha;
        for (int i = 0; i < audioBuffer.length; i++) {
            double window = alpha - beta * Math.cos((2 * Math.PI * i) / (audioBuffer.length - 1));
            windowedBuffer[i] = audioBuffer[i] / 32768.0 * window;
        }

        int bufferSize = windowedBuffer.length;
        double[] difference = new double[bufferSize];
        double[] cumulativeMeanNormalizedDifference = new double[bufferSize];
        double[] dMean = new double[bufferSize];

      // Autocorrelation function
        for (int lag = 0; lag < bufferSize; lag++) {
            for (int index = 0; index < bufferSize - lag; index++) {
                double diff = windowedBuffer[index] - windowedBuffer[index + lag];
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
        //Calculating the interpolated peak using parabolicInterpolation along with absolute and octave based threshold
        double interpolatedPeak = calculateInterpolatedPeak(cumulativeMeanNormalizedDifference, bufferSize, dMean);
        double pitchFrequency = SAMPLE_RATE / interpolatedPeak;

        if (listener != null) {
            listener.onPitchDetected(pitchFrequency);
        }

        return pitchFrequency;

    }

    public double calculateInterpolatedPeak(double[] cumulativeMeanNormalizedDifference, int bufferSize, double[] dMean ) {


        // Absolute threshold
        double threshold = 0.12;
        double energySum = 0.0;
        for (int i = 0; i < bufferSize; i++) {
            energySum += buffer[i] * buffer[i];
        }
        double averageEnergy = energySum / bufferSize;

        double normalizedEnergy = averageEnergy / (32768.0 * 32768.0);

        if (normalizedEnergy < 0.3  ) {
            threshold += 0.25;
        }
        int lag;

        for (  lag = 2; lag < bufferSize; lag++) {
            if(cumulativeMeanNormalizedDifference[lag-1]< threshold){
              while (lag+1<bufferSize && cumulativeMeanNormalizedDifference[lag+1] < cumulativeMeanNormalizedDifference[lag]) {
                  lag++;

              }
              break;
            }
        }

        lag = lag >= bufferSize ? bufferSize - 1 : lag;

        // Octave-based thresholding
        int subOctaves = 12;
        int subOctaveSize = bufferSize / subOctaves;
        int subOctaveStart = (lag / subOctaveSize) * subOctaveSize;
        int subOctaveEnd = subOctaveStart + subOctaveSize;
        for (int i = subOctaveStart + 1; i < subOctaveEnd; i++) {
            if (cumulativeMeanNormalizedDifference[i] < cumulativeMeanNormalizedDifference[lag]) {
                lag = i;
            }
        }
        int x0 = lag < 1 ? lag : lag - 1;
        int x2 = lag + 1 < cumulativeMeanNormalizedDifference.length ? lag + 1 : lag;


        double newLag;

        if (x0 == lag) {
            if (cumulativeMeanNormalizedDifference[lag] <= cumulativeMeanNormalizedDifference[x2]) {
                newLag = lag;
            } else {
                newLag = x2;
            }
        } else if (x2 == lag) {
            if (cumulativeMeanNormalizedDifference[lag] <= cumulativeMeanNormalizedDifference[x0]) {
                newLag = lag;
            } else {
                newLag = x0;
            }
        } else {
            // Fit the parabola between the first point, current tau, and the last point to find a
            // better tau estimate.
            double s0 = cumulativeMeanNormalizedDifference[x0];
            double s1 = cumulativeMeanNormalizedDifference[lag];
            double s2 = cumulativeMeanNormalizedDifference[x2];

            newLag = lag + (s2 - s0) / (2 * (2 * s1 - s2 - s0));
        }



        return newLag;
    }

    }








