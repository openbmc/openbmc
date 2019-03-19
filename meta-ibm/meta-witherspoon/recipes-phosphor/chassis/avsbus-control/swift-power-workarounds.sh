#!/bin/sh
# This should be run before power-on and used to patch/update power specific
# hardware settings. This is platform specific settings that must be
# updated/removed if not Swift.

########## Program TPS53915 FSW to desired frequency #########
# FREQUENCY_CONFIG D3h, bits 2:0

# AVDD, bus 3 addr 1D, set to 600khz
# 600khz = 1,0,0
i2cset -y 3 0x1D 0xD3 0x04 b

# 3.3VA, bus 3 addr 1C, set to 600khz
i2cset -y 3 0x1C 0xD3 0x04 b

# 3.3VB, bus 3 addr 1B, set to 600khz
i2cset -y 3 0x1B 0xD3 0x04 b

# 5.0V, bus 3 addr 1A, set to 850khz
# 850khz = 1,1,0
i2cset -y 3 0x1A 0xD3 0x06 b
########## END Program TPS53915 FSW to desired frequency #########

