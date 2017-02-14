#!/bin/sh

# Reset CPLD
gpioutil -p S7 -d out -v 1
