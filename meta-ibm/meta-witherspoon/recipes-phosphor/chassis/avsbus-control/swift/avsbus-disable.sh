#!/bin/sh
# For each AVSBus attached rail go back to using VOUT_COMMAND (PMBus voltage)
# instead of AVSBus and do *not* update VOUT_COMMAND with last voltage sent via
# AVSBus. This should be run after power-down. This is platform specific
# settings  that must be updated/removed if not Swift.

i2cset -y 9 0x70 0x00 0x00 b    # VDD 0  - PAGE set
i2cset -y 9 0x70 0x01 0x80 b    # VDD 0
i2cset -y 9 0x71 0x00 0x01 b    # VCS 0  - PAGE set
i2cset -y 9 0x71 0x01 0x80 b    # VCS 0
i2cset -y 9 0x71 0x00 0x00 b    # VDN 0  - PAGE set
i2cset -y 9 0x71 0x01 0x80 b    # VDN 0
i2cset -y 10 0x70 0x00 0x00 b    # VDD 1  - PAGE set
i2cset -y 10 0x70 0x01 0x80 b    # VDD 1
i2cset -y 10 0x71 0x00 0x01 b    # VCS 1  - PAGE set
i2cset -y 10 0x71 0x01 0x80 b    # VCS 1
i2cset -y 10 0x71 0x00 0x00 b    # VDN 1  - PAGE set
i2cset -y 10 0x71 0x01 0x80 b    # VDN 1
