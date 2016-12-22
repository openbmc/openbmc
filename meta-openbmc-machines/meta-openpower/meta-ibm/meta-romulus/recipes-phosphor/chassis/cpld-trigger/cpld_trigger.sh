#!/bin/sh

# Trigger CPLD to give pgood signal
gpioutil -p S7 -d out -v 0
