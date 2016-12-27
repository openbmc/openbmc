#!/bin/sh

i2cset -y 4 0x70 0x01 0xB0 b    # VDD 0
i2cset -y 4 0x70 0x00 0x01 b    # VCS 0  - PAGE set
i2cset -y 4 0x70 0x01 0xB0 b    # VCS 0
i2cset -y 4 0x70 0x00 0x00 b    # VCS 0  - PAGE reset
i2cset -y 4 0x73 0x01 0xB0 b    # VDN 0
i2cset -y 5 0x70 0x01 0xB0 b    # VDD 1
i2cset -y 5 0x70 0x00 0x01 b    # VCS 1  - PAGE set
i2cset -y 5 0x70 0x01 0xB0 b    # VCS 1
i2cset -y 5 0x70 0x00 0x00 b    # VCS 1  - PAGE reset
i2cset -y 5 0x73 0x01 0xB0 b    # VDN 1
