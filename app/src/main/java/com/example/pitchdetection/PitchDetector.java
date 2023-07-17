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

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        double[] windowedBuffer = HammingWindow(audioBuffer);

        int bufferSize = windowedBuffer.length;

      // Autocorrelation function
        double[] difference=Autocorrelation(windowedBuffer);

       // Cumulative mean normalized difference function
        double[]  cumulativeMeanNormalizedDifference = CumulativeMeanNormalizedDifference(difference, bufferSize);

        // Absolute threshold
        int lag = AbsoluteThreshold(cumulativeMeanNormalizedDifference,bufferSize);


        // Octave-based thresholding
        lag= OctaveThreshold(bufferSize,lag,cumulativeMeanNormalizedDifference);

        //Calculating the interpolated peak using parabolic Interpolation along with absolute and octave based threshold
        double interpolatedPeak = parabolicInterpolation(cumulativeMeanNormalizedDifference  , lag );
        double pitchFrequency = SAMPLE_RATE / interpolatedPeak;

        if (listener != null) {
            listener.onPitchDetected(pitchFrequency);
        }

        return pitchFrequency;

    }

    private double[] HammingWindow( short[] audioBuffer){
        double[] windowedBuffer = new double[audioBuffer.length];
        double alpha = 0.7;
        double beta = 1 - alpha;
        for (int i = 0; i < audioBuffer.length; i++) {
            double window = alpha - beta * Math.cos((2 * Math.PI * i) / (audioBuffer.length - 1));
            windowedBuffer[i] = audioBuffer[i] / 32768.0 * window;
        }
        return windowedBuffer;
    }

    private double[] Autocorrelation(double[] windowedBuffer){
        int bufferSize = windowedBuffer.length;
        double[] difference = new double[bufferSize];

        for (int lag = 0; lag < bufferSize; lag++) {
            for (int index = 0; index < bufferSize - lag; index++) {
                double diff = windowedBuffer[index] - windowedBuffer[index + lag];
                difference[lag] += diff * diff;
            }
        }
        return difference;
    }

    private double[] CumulativeMeanNormalizedDifference(double[] difference, int bufferSize){
        double[] cumulativeMeanNormalizedDifference = new double[bufferSize];

        cumulativeMeanNormalizedDifference[0] = 1;
        for (int lag = 1; lag < bufferSize; lag++) {
            double cmndf = 0;
            for (int index = 1; index <= lag; index++) {
                cmndf += difference[index];
            }
            cumulativeMeanNormalizedDifference[lag] = difference[lag] / (cmndf / lag);
        }
        return cumulativeMeanNormalizedDifference;
    }

    private int AbsoluteThreshold(double[] cumulativeMeanNormalizedDifference, int bufferSize){


        // Absolute threshold
        double threshold = 0.31;



        int lag;

        for (  lag = 2; lag < bufferSize-1; lag++) {
            if(cumulativeMeanNormalizedDifference[lag-1]< threshold){
                while (lag+1<bufferSize && cumulativeMeanNormalizedDifference[lag+1] < cumulativeMeanNormalizedDifference[lag]) {
                    lag++;

                }
                break;
            }
        }
        lag = lag >= bufferSize ? bufferSize - 1 : lag;
        return lag;
    }

    private int OctaveThreshold(int bufferSize, int lag, double[] cumulativeMeanNormalizedDifference){
        int subOctaves =8;
        int subOctaveSize = bufferSize / subOctaves;
        int subOctaveStart = (lag / subOctaveSize) * subOctaveSize;
        int subOctaveEnd = subOctaveStart + subOctaveSize;
        for (int i = subOctaveStart + 1; i < subOctaveEnd && i < cumulativeMeanNormalizedDifference.length; i++) {
            if (cumulativeMeanNormalizedDifference[i] < cumulativeMeanNormalizedDifference[lag]) {
                lag = i;
            }
        }
        return lag;
    }

    public double parabolicInterpolation(double[] cumulativeMeanNormalizedDifference , int lag  ) {

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

            double s0 = cumulativeMeanNormalizedDifference[x0];
            double s1 = cumulativeMeanNormalizedDifference[lag];
            double s2 = cumulativeMeanNormalizedDifference[x2];

            newLag = lag + (s2 - s0) / (2 * (2 * s1 - s2 - s0));
        }



        return newLag;
    }





    }








