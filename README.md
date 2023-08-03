Guitar tuner app with JAVA (!!STILL WIP!!)

This app was utilizes the YIN algorithm for pitch detection, made thanks to this paper: http://audition.ens.fr/adc/pdf/2002_JASA_YIN.pdf

I will add an extensive description once its finished!!

The pitch detection algorithm works like this: First of all windowing is applied to the signal, which is then sent to the YIn algorithm. 
The YIN algorithm works by detecting the lowest frequency fundamental of the signal. First of all, the first step describes the autocorrelation function where the initial signal is
compared to itself but from a delayed time period. This way the algorithm can find the highest peak. 
On the other hand, the cumulative mean normalised difference function helps with improving the imperfect periodicity that may occur due to zero-lag dip problem. 
Then an absolute threshold is applied so we filter out the frequencies we dont want. 
The paper suggests a threshold of 0.1 for less errors however I had to use a threshold of 0.3 otherwise it wouldnt detect lower notes. 
Next we need to calculate the fundamental frequency of the signal in the correct position, avoiding errors due to noise and disimilar sampling period. 
This can be done using parabolic interpolation which fits a parabola within a certain range from the peak estimate, resulting in a smoother pitch.

The program has multiple tunings pre-included as well as automatic tuning. Manual tuning is way more accurate than automatic.

My end goal is to also add the option to add a custom tuning, fine tune last details and add extensive description and comments.

Manual tuning:

![Screenshot_20230725_115342_StringSync](https://github.com/konLiogka/guitarTuner/assets/78957746/674e736e-ed05-416b-86ac-649433c5053a)


Automatic Tuning:

![Screenshot_20230725_115353_StringSync](https://github.com/konLiogka/guitarTuner/assets/78957746/210374bc-6a7b-4e60-87ba-0f3a48e919a9)
