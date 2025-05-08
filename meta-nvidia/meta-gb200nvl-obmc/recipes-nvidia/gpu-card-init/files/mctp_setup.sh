#!/bin/bash

# Assign static EIDs:
# 12 (MCU - MCTP over USB bridge)
# 13 (GPU - device behind the MCU)
busctl call au.com.codeconstruct.MCTP1 /au/com/codeconstruct/mctp1/interfaces/mctpusb0 au.com.codeconstruct.MCTP.BusOwner1 AssignEndpointStatic ayyy 0 12 13
