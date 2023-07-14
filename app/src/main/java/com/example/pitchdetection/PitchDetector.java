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
        double threshold = 0.13;


        double energySum = 0.0;
        for (int i = 0; i < bufferSize; i++) {
            energySum += buffer[i] * buffer[i];
        }
        double averageEnergy = energySum / bufferSize;

        double normalizedEnergy = averageEnergy / (32768.0 * 32768.0);

        if (normalizedEnergy < 0.5) {
            threshold += 0.3;
        } else {
            threshold -= 0.1;
        }

        int pitchPeriod = 0;
        for (int lag = 1; lag < bufferSize; lag++) {
            if(cumulativeMeanNormalizedDifference[lag-1]< threshold){
              while (lag+1<bufferSize && cumulativeMeanNormalizedDifference[lag+1] < cumulativeMeanNormalizedDifference[lag]) {
                  lag++;
                  pitchPeriod = lag;

              }
              break;
            }
        }

        // Octave-based thresholding
        int subOctaves =  8;
        int subOctaveSize = bufferSize / subOctaves;
        int subOctaveStart = (pitchPeriod / subOctaveSize) * subOctaveSize;
        int subOctaveEnd = subOctaveStart + subOctaveSize;
        for (int lag = subOctaveStart + 1; lag < subOctaveEnd; lag++) {
            if (cumulativeMeanNormalizedDifference[lag] < cumulativeMeanNormalizedDifference[pitchPeriod]) {
                pitchPeriod = lag;
            }
        }


        // Multiple parabolic interpolations
        int numInterpolations =  10;
        double interpolatedPeak = pitchPeriod;
        for (int iteration = 0; iteration < numInterpolations; iteration++) {
            interpolatedPeak = pitchPeriod;
            if (pitchPeriod > 1 && pitchPeriod < bufferSize - 1) {
                double delta = dMean[pitchPeriod + 1] - dMean[pitchPeriod - 1];
                double thresholdDelta = 0.1* cumulativeMeanNormalizedDifference[pitchPeriod];
                if (delta != 0 && Math.abs(dMean[pitchPeriod] - dMean[pitchPeriod - 1]) <= thresholdDelta && Math.abs(dMean[pitchPeriod] - dMean[pitchPeriod + 1]) <= thresholdDelta) {
                    interpolatedPeak += (dMean[pitchPeriod - 1] - dMean[pitchPeriod + 1]) / (2 * delta);
                }
            }
            pitchPeriod = (int) interpolatedPeak;

        }
        return interpolatedPeak;
    }

    }








