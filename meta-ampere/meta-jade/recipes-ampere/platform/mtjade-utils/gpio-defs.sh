#!/bin/sh

# shellcheck disable=SC2034
# Index of GPIO device in gpioget/gpioset
GPIO_CHIP0_IDX=0
GPIO_CHIP1_IDX=1

# Base of GPIO chip in /sys/class/gpio
GPIO_CHIP0_BASE=792
GPIO_CHIP1_BASE=780

### Power control configuration
# Power control gpios
S0_SHD_REQ_L=49
S0_SHD_ACK_L=50
S0_REBOOT_ACK_L=75
S0_SYSRESET_L=91


### Table 1: GPIO Assignments
S0_CPU_FW_BOOT_OK=48
CPU_BMC_OVERTEMP_L=51
CPU_BMC_HIGHTEMP_L=72
CPU_FAULT_ALERT=73
S1_CPU_FW_BOOT_OK=202

### Table 2: Alert and Additional Miscellaneous Signals
S0_SCP_AUTH_FAILURE=74
S1_SCP_AUTH_FAILURE=205
BMC_OK=228
SLAVE_PRESENT_L=230

### Common GPIOs
SYS_PSON_L=42
BMC_READY=229

### OCP power selection
OCP_AUX_PWREN=139
OCP_MAIN_PWREN=140

### SPI0 Mode  selection
SPI0_PROGRAM_SEL=226
SPI0_BACKUP_SEL=227
