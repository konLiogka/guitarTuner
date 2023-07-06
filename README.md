# guitarTuner
Simple Guitar tuner app with JAVA (still WIP)

For now it only shows you the frequency and what note you play within a threshold of 3Hz. There are some issues with octave jumping which I'm trying to fix.

I mainly followed this source:
http://audition.ens.fr/adc/pdf/2002_JASA_YIN.pdf
I'm not very familiar with all these terms so I used chatgpt as well.

I use the YIN algorithm which utilizes autocorrelation to find the dominant pitch frequency. This works by taking the signal two times but from a different time 
"lag" and comparing the two. Using the algorithm alone from the paper still produces some errors with octave jumping mainly.

In order to battle that, it is possible to iterate the parabolic interpolation multiple times as well as use Octave-based thresholding.

Once I finish the project I'll add an extensive description.






 

