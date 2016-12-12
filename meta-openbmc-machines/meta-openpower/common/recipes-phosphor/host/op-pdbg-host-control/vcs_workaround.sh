#!/bin/sh

# ################################################
# P9 workaround before powering VCS rails

# put the CFAM/FSI slave into async mode
pdbg -b fsi putcfam 0x900 0x1
# Unfence PLL controls
pdbg -b fsi putcfam 0x2810 0xFFFEFFFF 0
# Assert Perv chiplet endpoint reset, just in case
pdbg -b fsi putcfam 0x281A 0xFFFFFFFF 0x40000000
# Enable Nest PLL
pdbg -b fsi putcfam 0x281A 0xFFFFFFFF 1

