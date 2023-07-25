# guitarTuner
Simple Guitar tuner app with JAVA (still WIP). I need to add plenty more features while others are incomplete.

Right now I use a combination of methods. First of all Hamming windowing is applied to the signal. The base pitch detection algorithm is YIN which utilizes autocorrelation and cumulative mean difference. It then sends the calculated peak to a parabolic interpolation function for better accuracy. Before calculating the interpolated peak however, its important to filter the frequencies we want. I've found a combination of adaptive absolute threshold and octave threshold to have a nice result, however it still needs some fine tuning.

Octave jumps do occur as well as unwanted frequencies. However that's going to be a problem only with manual tuning. If user wants to select a specific tuning, for example Standard E, the error rate will be much smaller.

I will add an extensive description when its finished!

Download app:  https://github.com/konLiogka/guitarTuner/blob/master/stringSync.apk

Standard E tuning with manual tuning introduces less errors and octave jumps:

![Screenshot_20230725_115342_StringSync](https://github.com/konLiogka/guitarTuner/assets/78957746/18d134db-8eb2-4ab8-bfa5-c11b58221442)


Automatic tuning is more versatile but be aware of some errors and octave jumps!:

![Screenshot_20230725_115353_StringSync](https://github.com/konLiogka/guitarTuner/assets/78957746/c398a8eb-499c-45d5-aceb-0c605e3c34dc)


My goal is to also add custom tunings so you can select whatever note you want.
