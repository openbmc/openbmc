#!/bin/bash

vrm-control.sh vdna=0.9 vdnb=0.9

i2cset -y 4 0x71 0x21 0xDD 0x00 i  # CPU0 VDDR 1.35V
i2cset -y 4 0x72 0x21 0xDD 0x00 i
i2cset -y 5 0x71 0x21 0xDD 0x00 i  # CPU1 VDDR 1.35V
i2cset -y 5 0x72 0x21 0xDD 0x00 i
