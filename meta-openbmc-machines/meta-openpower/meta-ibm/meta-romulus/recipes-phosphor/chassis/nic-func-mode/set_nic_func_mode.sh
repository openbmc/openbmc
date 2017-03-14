#!/bin/sh

# Pull down GPIOD3/D4 to enable BCM5719 NIC_FUNC_MODE
gpioutil -p D3 -d out -v 0
gpioutil -p D4 -d out -v 0
