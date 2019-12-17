#!/bin/sh
# For each AVSBus attached power rail set the default boot voltage and then
# program the OPERATION register to switch to AVSBus mode and update default
# start voltage to what was last programmed in VOUT_COMMAND. This should be run
# before power-on. This is platform specific settings that must be
# updated/removed if not Swift.

i2cset -y 9 0x70 0x00 0x00 b    # VDD 0  - PAGE set
i2cset -y 9 0x70 0x01 0xB0 b    # VDD 0
i2cset -y 9 0x71 0x00 0x01 b    # VCS 0  - PAGE set
i2cset -y 9 0x71 0x01 0xB0 b    # VCS 0
i2cset -y 9 0x71 0x00 0x00 b    # VDN 0  - PAGE set
i2cset -y 9 0x71 0x01 0xB0 b    # VDN 0

i2cset -y 10 0x70 0x00 0x00 b    # VDD 1  - PAGE set
i2cset -y 10 0x70 0x01 0xB0 b    # VDD 1
i2cset -y 10 0x71 0x00 0x01 b    # VCS 1  - PAGE set
i2cset -y 10 0x71 0x01 0xB0 b    # VCS 1
i2cset -y 10 0x71 0x00 0x00 b    # VDN 1  - PAGE set
i2cset -y 10 0x71 0x01 0xB0 b    # VDN 1
