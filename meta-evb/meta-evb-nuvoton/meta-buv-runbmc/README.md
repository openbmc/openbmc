# Quick Setup Guide - HSBUV Board + Nuvoton NPCM7mnx RunBMC Module  

# Table of Contents
- [Module Overview](#module-overview)
- [Quick Setup](#quick-setup)
  * [Power-On and Reset](#power-on-and-reset)
  * [USB-to-UART5 for BMC debug console](#usb-to-uart5-for-bmc-debug-console)
  * [Terminal](#terminal)
  * [VGA Display](#vga-display)
- [Build OpenBMC](#build-openbmc)
- [How to Flash Image in U-boot](#how-to-flash-image-in-u-boot)
- [Boot from eMMC](#boot-from-emmc)
- [OpenBMC WebUI](#openbmc-webui)
- [FUP Mode for Emergency Firmware Update](#fup-mode-for-emergency-firmware-update)

# Module Overview

<img align="center" width="100%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_module_overview.png">

# Quick Setup
## Power-On and Reset

1. Connect the 12V power supply to power jack (J301). The power supply should be 12V and at least 2A; the jack should be 2.5 x 5.5 x 9.5mm in diameter.
2. Press and release Power-On-Reset (SW1801) push-button.

## USB-to-UART5 for BMC debug console

1. Download and install the USB-to-UART driver according to the host OS ([http://www.ftdichip.com/Drivers/VCP.htm](http://www.ftdichip.com/Drivers/VCP.htm)).
2. Connect a mini-USB cable between the PC host and HSBUV Micro USB UART5 (J2001, which connects to BMC serial interface 2, uboot and kernel terminal messages are sent through this port).
3. Wait for FTDI driver to be installed automatically and the COM port number will be assigned once installation done.
4. Check and verify D2008 LED light is ON.

## Terminal

1. Open a terminal (e.g., Tera Term) and set the correct COM port number assigned by the FTDI driver, then configure the COM port to 115200 Kbps, 8 data bits, 1 stop bit, no parity and no flow control.

    <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_teraterm.png">

2. Press and release Power-On-Reset (SW1801) push-button.
3. Hit any key to stop autoboot and enter into uboot environment.

    <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_uboot.png">

## VGA Display

Insert HSBUV board into MB PCIe slot through PCIE Gen2 (x4) interface for VGA display mailbox function.

  <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_vga.png">

  Note:
  - Only x1 lane is used.
  - RunBMC can be used as a secondary video card since RunBMC card doesn't include on-board VGA BIOS and MB dosn't include Matrox VGA BIOS.

# Build OpenBMC

### Source ###
- [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master)

### How to Build Image ###
1. Target buv runbmc
Source the setup script as follows:
```ruby
. setup buv-runbmc
```

2. Choose the distro

* [Inventory manager distro](https://github.com/openbmc/phosphor-inventory-manager)
```
bitbake obmc-phosphor-image
```

* [Entity manager distro](https://github.com/openbmc/entity-manager)
```
 DISTRO=buv-entity bitbake obmc-phosphor-image
```
### Output Images
After build completed, image will be put in openbmc/build/buv-runbmc/tmp/deploy/images/buv-runbmc/

<img align="center" width="25%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_images.png">

### How to Flash Image in U-boot ###
(over uboot TFTP)
1. Connect NB and HSBUV through RGMII MAC1 (J1101) and setup local network IP    
    * NB IP: 192.168.1.128
    * HSBUV IP: 192.168.1.15

    <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_network.png">

2. Put image-bmc to TFTP server

  <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_tftp.png">


3. Start Program

* Set BMC ip and tftp server ip in uboot env
```ruby
setenv gatewayip            192.168.0.254
setenv serverip             192.168.0.128
setenv ipaddr               192.168.0.12
setenv netmask              255.255.255.0
```

* Save uboot env to flash
```ruby
saveenv
```

* Flash full openbmc image
```ruby
tftp 10000000 image-bmc
sf probe 0:0
sf update 0x10000000 0x0 ${filesize}

```

* Flash linux kernel
```ruby
tftp 10000000 image-kernel
sf probe 0:0
sf update 0x10000000 0x200000 ${filesize}
```

* Flash bootloader
```ruby
setenv ethact gmac2
tftp 10000000 image-u-boot
sf probe 0:0
sf update 0x10000000 0x0 ${filesize}
```


4. If flash completed, it will reboot and run into OpenBMC environment.
    * login username: root
    * password: 0penBmc

    <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_login.png">


## Boot from eMMC
Openbmc system can be loaded from the onboard eMMC storage.

* build eMMC image, the image contains fitimage and rofs.
```ruby
#Inventory Manager
DISTRO=buv-runbmc-emmc bitbake obmc-phosphor-image
```
```ruby
#Entity Manager
DISTRO=buv-runbmc-emmc-entity bitbake obmc-phosphor-image
```
image-emmc.gz is generated in the image deploy folder.

* u-boot must enable the following configs.
```
CONFIG_CMD_UNZIP=y
CONFIG_CMD_EXT4=y
CONFIG_PARTITION_UUIDS=y
CONFIG_EFI_PARTITION=y
```

*  flash eMMC image in u-boot.
```
tftp 10000000 image-emmc.gz
gzwrite mmc 0 10000000 ${filesize}
```

* boot Openbmc
```ruby
setenv bootargs earlycon=uart8250,mmio32,0xf0000000 console=ttyS0,115200n8
setenv setmmcargs 'setenv bootargs ${bootargs} rootwait root=PARTLABEL=${rootfs}'
setenv loadaddr 0x10000000
setenv mmcboot 'setenv bootpart 2; setenv rootfs rofs-a; run setmmcargs; ext4load mmc 0:${bootpart} ${loadaddr} fitImage && bootm ${loadaddr}; echo Error loading kernel FIT image'
run mmcboot
```

# OpenBMC WebUI

[https://<BMC_IP>](https://<BMC_IP>)
- Username: root
- Password: 0penBmc

  <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_webui_login.png">
  <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_webui.png">

# FUP Mode for Emergency Firmware Update

1. Remove HSBUV 12V power supply (J301) and Micro USB UART5 (J2001).

2. Connect pin4 and pin8 of J1701 header.

  <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_j1701_pin4_8.png">

3. Connect USB cable to Micro USB UART2 (J2006) for BMC FUP mode.

  <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_usb_for_fup.png">

4. Download IGPS from [https://github.com/Nuvoton-Israel/igps](https://github.com/Nuvoton-Israel/igps).

  <img align="center" width="35%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_igps.png">

5. Execute the following scripts in order to flash boot block and uboot.
    * UpdateInputsBinaries_RunBMC.bat

    <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_update_inputs_binary.png">

    * GenerateAll.bat

    <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_generate_all.png">
    
    * ProgramAll_Basic.bat

    <img align="center" width="50%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/BuvQuickSetup_program_all.png">
