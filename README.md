# guitarTuner
Simple Guitar tuner app with JAVA (still WIP). I need to add plenty more features while others are incomplete.

Right now I use a combination of methods. First of all Hamming windowing is applied to the signal. The base pitch detection algorithm is YIN which utilizes autocorrelation and cumulative mean difference. It then sends the calculated peak to a parabolic interpolation function for better accuracy. Before calculating the interpolated peak however, its important to filter the frequencies we want. I've found a combination of adaptive absolute threshold and octave threshold to have a nice result, however it still needs some fine tuning.

Octave jumps do occur as well as unwanted frequencies. However that's going to be a problem only with manual tuning. If user wants to select a specific tuning, for example Standard E, the error rate will be much smaller.

I will add an extensive description when its finished!

Download app:  https://github.com/konLiogka/guitarTuner/blob/master/stringSync.apk
