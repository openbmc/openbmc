# NPCM750 RunBMC BUV Hardware Rework

# Table of Contents
- [Rework for RunBMC BUV](#rework-for-runbmc-buv)
  * [7 segment display](#7-segment-display)
  * [Secure boot](#secure-boot)
  * [NIST Secure Feature](#nist-secure-feature)

# Rework for RunBMC BUV

## 7 segment display

1. Add R722 66ohm on BUV Board for E2

<img align="center" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/runbmc_buv_led.png">

2. Remove R206 0k ohm on RunBMC Card to avoid BMC keep reset
3. Short R422 on RunBMC Card for control GPIO09 to set 7 segment display

<img align="left" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/runbmc_buv_p1.png">
<img align="center" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/runbmc_buv_p2.png">
<img align="center" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/runbmc_buv_p3.png">

## Secure boot
The NPCM7xx is a BMC that authenticates it's code before it runs and so can become the Root of Trust for th BMC subsystem.
NPCM750 RunBMC can support normal mode and secure mode
1. Make a jumper to Connect 2.2k ohm on R108 when enable secure boot

<img align="left" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/runbmc_buv_secure1.png">
<img align="center" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/runbmc_buv_secure2.png">
<img align="center" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/runbmc_buv_secure3.png">

## NIST Secure Feature
RunBMC can support Nist feature
1. remove runbmc R175
2. mount 0k ohm on R186
3. make a jumper wire to connect R186(runbmc) and BUV J705 pin 7
  
<img align="left" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/runbmc_buv_nist.png">
<img align="center" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/runbmc_buv_nist2.png"> 
