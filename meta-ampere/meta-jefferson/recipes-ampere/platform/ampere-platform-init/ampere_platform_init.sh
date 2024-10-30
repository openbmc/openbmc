#!/bin/bash

# shellcheck disable=SC2154
# shellcheck disable=SC2046

# Setting default value for device sel and mux
bootstatus=$(cat /sys/class/watchdog/watchdog0/bootstatus)
if [ "$bootstatus" == '32' ]; then
    echo "Initialize output GPIOs after AC power"
    gpioset $(gpiofind spi0-backup-sel)=1  # Select primary CPU's SPI
    gpioset $(gpiofind spi0-program-sel)=0 # Switch SPI-NOR to Host
    gpioset $(gpiofind led-fp-sta-gr)=0    # System ready LED input
fi

# Setting default value for others gpio pins
echo "Initialize output GPIOs"
gpioset $(gpiofind host0-pmin-n)=1         # Disable PMIN
gpioset $(gpiofind ext-high-temp-n)=1      # Notify CPU0/1 HIGHTEMP event
gpioset $(gpiofind wd-disable-n)=1         # Watchdog disable
gpioset $(gpiofind hpm-stby-rst-n)=1       # HPM Standby Reset input from BMC
gpioset $(gpiofind jtag-sel-s0)=1          # Control JTAG MUX between CPU & FPGA
gpioset $(gpiofind user-mode)=1            # Enable User Mode
gpioset $(gpiofind jtag-srst-n)=1          # BMC JTAG soft reset input
gpioset $(gpiofind host0-shd-req-n)=1      # Request shutdown from BMC to CPLD
gpioset $(gpiofind vrd-prg-en-n)=1         # Enable VRD Programing Mode input
gpioset $(gpiofind rtc-battery-voltage-read-enable)=0 # Enable for sense Battery voltage
gpioset $(gpiofind s0-rtc-lock)=0
gpioset $(gpiofind hpm-fw-recovery)=0      # Force HPM FPGA recovery
gpioset $(gpiofind spi-nor-access)=0       # Deassert BMC access SPI-NOR pin
gpioset $(gpiofind host0-special-boot)=0   # Deassert SPECIAL_BOOT GPIO pin
gpioset $(gpiofind cpu-bios-recover)=0     # BIOS recovery enable from BMC
gpioset $(gpiofind nmi-n)=0                # Per HW design, default state should be Low
gpioset $(gpiofind uart1-mode0)=0          # Set UART Mux to BMC
gpioset $(gpiofind uart2-mode0)=0
gpioset $(gpiofind uart1-mode1)=1
gpioset $(gpiofind uart2-mode1)=1

# When BMC is rebooted, because PSON_L has pull up to P3V3_STB, it changes its
# value to HIGH. Add code to check P3V3_STB and recover PSON_L to correct state
# before setting BMC_RDY.
pgood=$(gpioget $(gpiofind power-chassis-good))

if [ "$pgood" == '1' ]; then
    echo "PSU is on. Setting PSON to 0"
    gpioset $(gpiofind power-chassis-control)=0
else
    echo "PSU is off. Setting PSON to 1"
    gpioset $(gpiofind power-chassis-control)=1
fi

gpioset $(gpiofind host0-sysreset-n)=1

# Bind RTC if /dev/rtc0 is not available
if [[ ! -e /dev/rtc0 ]]; then
    echo "Bind rtc driver"
    echo 6-0051 > /sys/bus/i2c/drivers/rtc-pcf8563/bind
fi

exit 0
