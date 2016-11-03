#!/bin/sh

gpioutil -p D0 -d out -v 1   # enable fsi link
putcfam pu 2810 15 1 0       # Unfence PLL controls
putcfam pu 281A  1 1 f       # Assert Perv chiplet endpoint reset, just in case
putcfam pu 281A 31 1 f       # Enable Nest PLL
