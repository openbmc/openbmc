#!/bin/sh

gpioutil -p D0 -d out -v 1   # enable fsi link

#put the CFAM/FSI slave into async mode
putcfam 0x900 0x1
putcfam 0x2801 0x80C00000

putcfam pu 2810 15 1 0       # Unfence PLL controls
putcfam pu 281A  1 1 f       # Assert Perv chiplet endpoint reset, just in case
putcfam pu 281A 31 1 f       # Enable Nest PLL
