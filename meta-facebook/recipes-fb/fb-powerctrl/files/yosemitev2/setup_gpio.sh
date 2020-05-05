#!/bin/bash

. /usr/sbin/ast-functions

# SLOT1_PRSNT_N, GPIOAA0 (208)
gpio_export AA0

# SLOT2_PRSNT_N, GPIOAA1 (209)
gpio_export AA1

# SLOT3_PRSNT_N, GPIOAA2 (210)
gpio_export AA2

# SLOT4_PRSNT_N, GPIOAA3 (211)
gpio_export AA3


# PWR_SLOT1_BTN_N, 1S Server power out, on GPIO D1(25)
gpio_set D1 1

# PWR_SLOT2_BTN_N, 1S Server power out, on GPIO D3(27)
# Make sure the Power Control Pin is Set properly
gpio_set D3 1

# PWR_SLOT3_BTN_N, 1S Server power out, on GPIO D5(29)
gpio_set D5 1

# PWR_SLOT4_BTN_N, 1S Server power out, on GPIO D7(31)
gpio_set D7 1



# Power LED for Slot#2:GPIOM0 (96)
gpio_set M0 0

# Power LED for Slot#1: GPIOM1 (97)
gpio_set M1 0

# Power LED for Slot#4: GPIOM2 (98)
gpio_set M2 0

# Power LED for Slot#3: GPIOM3 (99)
gpio_set M3 0


# SLOT1_POWER_EN: GPIOI0 (64)
gpio_export I0

# SLOT2_POWER_EN: GPIOI1 (65)
gpio_export I1

# SLOT3_POWER_EN: GPIOI2 (66)
gpio_export I2

# SLOT4_POWER_EN: GPIOI3 (67)
gpio_export I3

# DEBUG UART Controls
# 4 signals: DEBUG_UART_SEL_0/1/2 and DEBUG_UART_RX_SEL_N
# GPIOE0 (32), GPIOE1 (33), GPIOE2 (34) and GPIOE3 (35)

gpio_export E0

gpio_export E1

gpio_export E2

gpio_export E3
