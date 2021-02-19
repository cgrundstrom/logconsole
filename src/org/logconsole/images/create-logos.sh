#!/bin/bash
for s in 16 20 24 32 48 64 100 128
do
  echo $s
  cp logo-560.png logo-${s}.png
  mogrify -geometry ${s}x${s} logo-${s}.png
done

cp logo-560.png logoActive.png
mogrify -geometry 48x48 logoActive.png

cp logo-560Rollover.png logoRollover.png
mogrify -geometry 48x48 logoRollover.png
