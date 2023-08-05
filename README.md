# Application

This is a guitar tuner app. Download the application from [here](https://github.com/konLiogka/guitarTuner/blob/main/app-debug.apk).
It utilizes the YIN algorithm for pitch detection, made thanks to this [paper](http://audition.ens.fr/adc/pdf/2002_JASA_YIN.pdf).
> [!NOTE]
> While the app is finished, there are still some things that I need to change, there may be some small bugs. Also I am thinking of adding microtonal notes and tunings as well as different types of guitars
like guitar with more than 6 strings or basses. Coming soon in future versions.


# Description
This is my first serious programming project. I chose this type of application because its not something common while I was also looking to replace my tuner apps with something free, open source and easy to use. This app was made for guitars, however I found out that it can work on pianos too. Still, it's tuned specifically for guitars so I wouldn't recommend using it on any other instruments. I have also added the option
of creating a custom tuning.


# How it works
The application was made solely using Java in android studio.
The pitch detection algorithm, while not completely handmade, does not use any external libraries.

* First of all windowing is applied to the signal with a buffer size of 4096. Increasing the buffer size does provide a better window however it also makes the program heavier.
* After computing the windowed signal, it is sent to the YIN algorithm. It basically works by detecting the lowest frequency fundamental of the signal. 
* The first step describes the autocorrelation function where the initial signal is compared to itself but from a delayed time period. This way the algorithm can find the highest peak which is going to indicate where the period starts/ends. 
* On the other hand, the cumulative mean normalised difference function helps with improving the imperfect periodicity that may occur due to zero-lag dip problem. 
* Then an absolute threshold is applied so we filter out the frequencies we dont want. 
  The paper suggests a threshold of 0.1 for less errors however I had to use a threshold of 0.3 otherwise it wouldnt detect lower notes. 
  As a note is let to ring, it will be cut at a specific amplitude depending on the threshold.
* Next we need to calculate the fundamental frequency of the signal in the correct position, avoiding errors due to noise and disimilar sampling period. 
  This can be done using parabolic interpolation which fits a parabola within a certain range from the peak estimate, resulting in a smoother pitch. It is a very commonly used function in pitch detection algorithms
  and that's because it produces reliable estimates by smoothing the selected frequency spectrum which helps the alignment of the peak.

The program has multiple tunings pre-included as well as automatic tuning. Manual tuning is way more accurate than automatic.

> [!IMPORTANT]
> Due to the high threshold value, errors such as octave jumping and detecting wrong frequencies occur with automatic tuning.
> This is not a problem with manual tuning because I have set a specific value of cents that the note can range based on the selected string.

 
## Manual Tuning

 ![standard](https://github.com/konLiogka/guitarTuner/assets/78957746/5513d7a5-05aa-44d5-a252-7ee9bd64b0cd)


## Automatic Tuning
 
![auto](https://github.com/konLiogka/guitarTuner/assets/78957746/e9e8a400-777c-43aa-9ba4-7a9251356732)  


Known bugs: απορροφητήρες 
