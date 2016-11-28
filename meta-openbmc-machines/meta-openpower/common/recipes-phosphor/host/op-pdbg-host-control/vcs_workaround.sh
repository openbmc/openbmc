#!/bin/sh

putcfam pu 0900 31 1 1 -ib   # put the CFAM/FSI slave into async mode
putcfam pu 2810 15 1 0       # Unfence PLL controls
putcfam pu 281A  1 1 f       # Assert Perv chiplet endpoint reset, just in case
putcfam pu 281A 31 1 f       # Enable Nest PLL
