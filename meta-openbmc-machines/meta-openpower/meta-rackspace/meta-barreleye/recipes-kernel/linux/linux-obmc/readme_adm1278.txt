README for adm1278 hwmon driver
==================================
Yi Li <shliyi@cn.ibm.com>


This is a temporary kernel patch to enable hwmon driver for adm1278 chip on Barreleye.
When this patch is merged into linux kernel, this patch will be removed from openbmc.

The adm1278 driver is created according to datasheet:
http://www.analog.com/media/en/technical-documentation/data-sheets/ADM1278.pdf

The patch heavily re-used adm1278 enabling code from: https://github.com/facebook/openbmc/blob/master/meta-aspeed/recipes-kernel/linux/files/patch-2.6.28.9/0000-linux-openbmc.patch.

This patch has been tested on barreleye, by following these steps:

1) There are 3 adm1278 devices on Barreleye

I2C5: P12v_a for CPU0
I2C6: P12v_b for CPU1
I2C7: P12v_c for HDD and IO Board

2) adm1278 driver is based on adm1275.c, which depends on pmbus. This patch builds
adm1275 and pmbus into kernel.

3) When kernel booted, initialize the adm1278 devices:

root@barreleye:~# echo adm1278 0x10 > /sys/class/i2c-adapter/i2c-4/new_device
root@barreleye:~# echo adm1278 0x10 > /sys/class/i2c-adapter/i2c-5/new_device
root@barreleye:~# echo adm1278 0x10 > /sys/class/i2c-adapter/i2c-6/new_device

There will be three new hwmon sysfs entries created:

root@barreleye:~# ls /sys/class/hwmon/hwmon3/
curr1_highest         in1_highest           in1_reset_history     in2_min_alarm         power1_label          temp1_input
curr1_input           in1_input             in2_highest           in2_reset_history     power1_max            temp1_max
curr1_label           in1_label             in2_input             name                  power1_reset_history  temp1_max_alarm
curr1_max             in1_max               in2_label             power/                subsystem/            temp1_reset_history
curr1_max_alarm       in1_max_alarm         in2_max               power1_alarm          temp1_crit            uevent
curr1_reset_history   in1_min               in2_max_alarm         power1_input          temp1_crit_alarm
device/               in1_min_alarm         in2_min               power1_input_highest  temp1_highest
root@barreleye:~# ls /sys/class/hwmon/hwmon4/
curr1_highest         in1_highest           in1_reset_history     in2_min_alarm         power1_label          temp1_input
curr1_input           in1_input             in2_highest           in2_reset_history     power1_max            temp1_max
curr1_label           in1_label             in2_input             name                  power1_reset_history  temp1_max_alarm
curr1_max             in1_max               in2_label             power/                subsystem/            temp1_reset_history
curr1_max_alarm       in1_max_alarm         in2_max               power1_alarm          temp1_crit            uevent
curr1_reset_history   in1_min               in2_max_alarm         power1_input          temp1_crit_alarm
device/               in1_min_alarm         in2_min               power1_input_highest  temp1_highest
root@barreleye:~# ls /sys/class/hwmon/hwmon5/
curr1_highest         in1_highest           in1_reset_history     in2_min_alarm         power1_label          temp1_input
curr1_input           in1_input             in2_highest           in2_reset_history     power1_max            temp1_max
curr1_label           in1_label             in2_input             name                  power1_reset_history  temp1_max_alarm
curr1_max             in1_max               in2_label             power/                subsystem/            temp1_reset_history
curr1_max_alarm       in1_max_alarm         in2_max               power1_alarm          temp1_crit            uevent
curr1_reset_history   in1_min               in2_max_alarm         power1_input          temp1_crit_alarm
device/               in1_min_alarm         in2_min               power1_input_highest  temp1_highest

4) For details of what each hwmon sysfs attributes mean, please refer to:
https://www.kernel.org/doc/Documentation/hwmon/pmbus
For short, 'curr1_*' refers to 'IOUT', 'in1_*' refers to 'vin', 'in2_*' refers to 'vout', 'power1_*' refers to 'input power',
'temp1_*' for 'temperature'.

5) Remaining issue:

5.1) Currently, i2c_aspeed driver does not handle "i2c_smbus_read_block_data()" correctly. So this patch has to bypass some detection code.
We need to fix this issue when the patch is merged to kernel.
5.2) According to adm1278 datasheet, there is a sense resistor used to measure power and current. The resistor will affect conversion between
adm1278 register value to real-world value for current and power. I am not very sure about the resistor value. So using 1 mili-ohms (or 1000 micro-ohms) as default value. When build the adm1275 driver as kernel module, we can set this resistor value by:

# insmod adm1275.ko r_sense=500

This will set the 'sense resistor' to 500 micro-ohms.
5.3) Some of the sensor value, e.g, 'temp1_input' seems not reasonable, e.g:

root@barreleye:~# cat /sys/class/hwmon/hwmon4/temp1_input
-270952

Need further check on that.
