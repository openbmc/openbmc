Nuvoton NPCM845 Evaluation Board
================

This is the Nuvoton NPCM845 evaluation board layer.
The NPCM845 is an ARM based SoC with external DDR RAM and 
supports a large set of peripherals made by Nuvoton. 

# Dependencies
This layer depends on:

```
  URI: github.com/Nuvoton-Israel/openbmc.git
  branch: npcm-master
```

# Contacts for Patches

Please submit any patches against the meta-evb-npcm845 layer to the maintainer of nuvoton:
* Joseph Liu, <KWLIU@nuvoton.com>
* Stanley Chu, <YSCHU@nuvoton.com>
* Tyrone Ting, <KFTING@nuvoton.com>
* Tim Lee, <CHLI30@nuvoton.com>
* Jim Liu, <JJLIU0@nuvoton.com>
* Marvin Lin, <KFLIN@nuvoton.com>

# Table of Contents

- [Dependencies](#dependencies)
- [Contacts for Patches](#contacts-for-patches)
- [Getting Started](#getting-started)
  * [Setting up EVB](#setting-up-evb)
  * [Building your OpenBMC project](#building-your-openbmc-project)
    + [Enable ECC](#enable-ecc)
    + [Configuration](#configuration)
    + [Build](#build)
    + [Output Images](#output-images)
  * [Flash Programing Tool](#flash-programing-tool)
    + [IGPS](#igps)
    + [ISP](#ISP)
    + [U-BOOT](#u-boot)
  * [Boot from eMMC](#boot-from-emmc)
- [BMC Modules](#bmc-modules)
  * [GPIO](#gpio)
  * [UART](#uart)
  * [FIU](#fiu)
  * [Network](#network)
  * [I3C](#i3c)
  * [JTAG Master](#jtag-master)
  * [SMBus](#smbus)
  * [ESPI](#espi)
  * [SIOX](#siox)
  * [SPIX](#spix)
  * [VGA](#vga)
  * [USB](#usb)
  * [ADC](#adc)
  * [FAN](#fan)
  * [TMPS](#tmps)
  * [PCIE RC](#pcie-rc)
  * [EMMC](#emmc)
  * [BIOS POST Code](#bios-post-code)
  * [AES](#aes)
  * [SHA](#sha)
  * [RNG](#rng)
  * [OTP](#otp)
  * [PSPI](#pspi)
  * [EDAC](#edac)
  * [Host Serial Port](#host-serial-port)
  * [PECI](#peci)
  * [FLM](#flm)
- [Troubleshooting](#troubleshooting)
  * [Failed to probe SPI0 CS0 in u-boot](#failed-to-probe-SPI0-CS0-in-u-boot)

# Getting Started

## Setting up EVB

### 1) Strap settings
* By default, only turn on strap 5 of the SW_STRAP1_8 dip switch.
* The other straps remain off.

### 2) Power Source selector
* JP_5V_SEL set to 1-2, If On-Board VR(12V->5V) is used to power the EVB (<span style="color: green">Recommended)
* JP_5V_SEL set to 2-3, If USB VBUS is used to power the EVB

### 3) BMC Console

* Connect a Mini-USB cable to J_USB_TO_UART
* You will get 4 serial port options from your terminal settings.
* Please select second serial port and set baud rate to 115200.
* After EVB is powered on, you will get BMC logs from the terminal.

### 4) Secure boot status
* When you see the following BMC message, it means that secure boot is enabled.
```ruby
Nuvoton Technologies: BMC NPCM8XX
.....
.....
TipROM 0x104 ** Secure boot is enabled
.....
.....
```

## Building your OpenBMC project

### Enable ECC

To enable memory ECC function, please enable [MC_CAPABILITY_ECC_EN](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-nuvoton/recipes-bsp/images/npcm8xx-igps/BootBlockAndHeader_EB.xml#L181)
```ruby
<BinField>
	<!-- MC_CONFIG. 
		Bit 0: MC_CAPABILITY_ECC_EN (0x01)
		 -->
	<name>MC_CONFIG</name>          
	<config>
		<offset>0x134</offset>       
		<size>0x1</size> 
	</config>
	<content format='32bit'>0x01</content>  
</BinField>
```

### Configuration

- If you are using Red EVB board, please enable the [dts patch](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm845/recipes-kernel/linux/linux-nuvoton_%25.bbappend#L5)

### Build
1. Target EVB NPCM845
Source the setup script as follows:
```ruby
. setup evb-npcm845
```

2. Choose the distro

* [Inventory manager distro](https://github.com/openbmc/phosphor-inventory-manager)
```
bitbake obmc-phosphor-image
```

* [Entity manager distro](https://github.com/openbmc/entity-manager)
```
DISTRO=arbel-evb-entity bitbake obmc-phosphor-image
```

### Output Images
* You will find images in path build/evb-npcm845/tmp/deploy/images/evb-npcm845

Type          | Description                                                                                                     |
:-------------|:-------------------------------------------------------------------------------------------------------- |
image-bmc   |  includes image-u-boot and image-kernel and image-rofs                                                                     |
image-uboot   |  tipfw + bootlock + u-boot                                                                     |
image-kernel  |  Fit Image(Linux kernel + dtb+ initramfs)                                                                                     |
image-rofs    |  OpenBMC Root Filesystem                                                          |

## Flash Programing Tool

### IGPS

#### Image Generation before flashing through IGPS
Currently, Arbel support A1/Z1 devices and EB/SVB boards. Default configuration is using A1 device and EB board.<br/>
You need to make sure which one is your device and board. Then execute correct batch file as below to generate image.

* UpdateInputsBinaries_A1_EB.bat
* UpdateInputsBinaries_A1_SVB.bat
* UpdateInputsBinaries_Z1_EB.bat
* UpdateInputsBinaries_Z1_SVB.bat

Note: UpdateInputBinaries*.bat resets all the images and xml files inside **py_scripts/ImageGeneration/inputs**<br/>
After that users can override the existing files in inputs folder, or use as is.

In OpenBMC, there is one variable for configure Arbel A1/Z1 device in file [evb-npcm845.conf](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm845/conf/machine/evb-npcm845.conf )<br/>
If you are using Z1 device, please modify variable **DEVICE_GEN = "Z1"**, before bitbake obmc-phosphor-image.<br/>


#### Flashing through IGPS
Python 2.7 is required.<br/>
Note: FUP is means using internal UART of the Arbel. This feature is only supported in Z1. For A1 device, please use ISP.<br/>

1. BMC enter to FUP Mode :
* Connect a Mini-USB cable to J_USB_TO_UART 
* STRAP9 on
* Quit terminal app 
* Issue PORST_N (Power-On-Reset). 


2. Image programming:
* Non secure boot
```
python ./ProgramAll_Basic.py
```

* Secure boot is enabled
```
python ./ProgramAll_Secure.py
```

### ISP

#### In-system-programing using FTDI

1. BMC enter to tri-state
* Connect a Mini-USB cable to J_USB_TO_UART 
* STRAP7 on(BMC pins are at Hi-Z) and STRAP5 on (Rout BSP signals via Host SI2 pins).
* Quit terminal app 
* Issue PORST_N (Power-On-Reset). 

2. Programming bootloader
```
Arbel_EVB_FlashProg.exe -open-desc "NPCM8mnx_Evaluation_Board B" -verify-on -prog-file "image-u-boot" 0 0 -1 -reset 
```

3. Programming full image
```
Arbel_EVB_FlashProg.exe -open-desc "NPCM8mnx_Evaluation_Board B" -verify-on -prog-file "image-bmc" 0 0 -1 -reset 
```

### U-BOOT

#### Flash in U-BOOT

* User can program images by u-boot command.
* If you are using Red EVB board:
  - The flash 0 size is 4MB, you should program openbmc image to flash 1.
* If you are using Blue/Green EVB board:
  - The flash 0 size is 128MB, you can leave all images at flash 0.

1. Setting up:
* Power on your EVB and stop BMC at u-boot stage.
* Prepare an ethernet cable and connect to J_RGMII

* Set BMC ip and tftp server ip in uboot env
```ruby
setenv gatewayip            192.168.0.254
setenv serverip             192.168.0.128
setenv ipaddr               192.168.0.12
setenv netmask              255.255.255.0
```
* Set bootargs in uboot env
```ruby
setenv autoload  no
setenv autostart no
setenv baudrate 115200
setenv bootcmd 'run romboot'
setenv bootdelay 2
setenv common_bootargs 'setenv bootargs earlycon=${earlycon} root=/dev/ram0 console=${console} mem=${mem}'
setenv console 'ttyS0,115200n8'
setenv earlycon 'uart8250,mmio32,0xf0000000'
setenv mem 880M
setenv romboot 'run common_bootargs; echo Booting Kernel from flash; echo +++ uimage at 0x${uimage_flash_addr}; echo Using bootargs: ${bootargs};bootm ${uimage_flash_addr}'
setenv stderr serial
setenv stdin serial
setenv stdout serial
```
* Blue/Green EVB, boot from flash 0
```ruby
setenv uimage_flash_addr 0x80200000
```
* Red EVB, boot from flash 1
```ruby
setenv bootcmd 'mw fb000000 030111BC; run romboot'
setenv uimage_flash_addr 0x88200000
```

* Save uboot env to flash
```ruby
saveenv
```

2. Image programming:

* Flash full openbmc image
```ruby
setenv ethact gmac2
tftp 10000000 image-bmc
/* Blue/Green EVB */
sf probe 0:0
sf update 0x10000000 0x0 ${filesize}
/* Red EVB */
sf probe 0:1
sf update 0x10000000 0x0 ${filesize}
```

* Flash linux kernel
```ruby
setenv ethact gmac2
tftp 10000000 image-kernel
/* Blue/Green EVB */
sf probe 0:0
sf update 0x10000000 0x200000 ${filesize}
/* Red EVB */
sf probe 0:1
sf update 0x10000000 0x200000 ${filesize}
```

* Flash bootloader
```ruby
setenv ethact gmac2
tftp 10000000 image-u-boot
sf probe 0:0
sf update 0x10000000 0x0 ${filesize}
```

3. Booting to OpenBMC:

* Enter boot command
```ruby
boot
```

4. OpenBMC Login Prompts.

* User: root
* Password: 0penBmc
```ruby
[  OK  ] Reached target Login Prompts.

Phosphor OpenBMC (Phosphor OpenBMC Project Reference Distro) 0.1.0 evb-npcm845 ttyS0

evb-npcm845 login:
```

## Boot from eMMC
Openbmc system can be loaded from the onboard eMMC storage.

* build eMMC image, the image contains fitimage and rofs.
```
DISTRO=arbel-evb-emmc bitbake obmc-phosphor-image
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

# BMC Modules

## GPIO

The NPCM8XX has eight GPIO modules with total 256 pins (each GPIO module contains a port of 32 GPIO pins).
Most of them are multiplexed with other system functions.
You can program MFSEL registers to configure a pin as GPIO.

- Connect pin GPIO0 (J4.3) to pin GPIO1 (J4.4) on GPIOs header J4 for the following tests.

### Linux Test

- Edit nuvoton-common-npcm8xx.dtsi.
```
    gpio0: gpio@f0010000 {
        gpio-controller;
        #gpio-cells = <2>;
        reg = <0x0 0xB0>;
        interrupts = <GIC_SPI 116 IRQ_TYPE_LEVEL_HIGH>;
        gpio-ranges = <&pinctrl 0 0 32>;
    };
    gpio1: gpio@f0011000 {
        gpio-controller;
        #gpio-cells = <2>;
        reg = <0x1000 0xB0>;
        interrupts = <GIC_SPI 117 IRQ_TYPE_LEVEL_HIGH>;
        gpio-ranges = <&pinctrl 0 32 32>;
    };
    gpio2: gpio@f0012000 {
        gpio-controller;
        #gpio-cells = <2>;
        reg = <0x2000 0xB0>;
        interrupts = <GIC_SPI 118 IRQ_TYPE_LEVEL_HIGH>;
        gpio-ranges = <&pinctrl 0 64 32>;
    };
    gpio3: gpio@f0013000 {
        gpio-controller;
        #gpio-cells = <2>;
        reg = <0x3000 0xB0>;
        interrupts = <GIC_SPI 119 IRQ_TYPE_LEVEL_HIGH>;
        gpio-ranges = <&pinctrl 0 96 32>;
    };
    gpio4: gpio@f0014000 {
        gpio-controller;
        #gpio-cells = <2>;
        reg = <0x4000 0xB0>;
        interrupts = <GIC_SPI 120 IRQ_TYPE_LEVEL_HIGH>;
        gpio-ranges = <&pinctrl 0 128 32>;
    };
    gpio5: gpio@f0015000 {
        gpio-controller;
        #gpio-cells = <2>;
        reg = <0x5000 0xB0>;
        interrupts = <GIC_SPI 121 IRQ_TYPE_LEVEL_HIGH>;
        gpio-ranges = <&pinctrl 0 160 32>;
    };
    gpio6: gpio@f0016000 {
        gpio-controller;
        #gpio-cells = <2>;
        reg = <0x6000 0xB0>;
        interrupts = <GIC_SPI 122 IRQ_TYPE_LEVEL_HIGH>;
        gpio-ranges = <&pinctrl 0 192 32>;
    };
    gpio7: gpio@f0017000 {
        gpio-controller;
        #gpio-cells = <2>;
        reg = <0x7000 0xB0>;
        interrupts = <GIC_SPI 123 IRQ_TYPE_LEVEL_HIGH>;
        gpio-ranges = <&pinctrl 0 224 32>;
    };
```
- Enable Kernel config
```
CONFIG_GPIOLIB=y
CONFIG_GPIOLIB_FASTPATH_LIMIT=512
CONFIG_OF_GPIO=y
CONFIG_GPIOLIB_IRQCHIP=y
CONFIG_GPIO_SYSFS=y
CONFIG_GPIO_CDEV=y
CONFIG_GPIO_CDEV_V1=y
CONFIG_GPIO_GENERIC=y
CONFIG_GPIO_GENERIC_PLATFORM=y
```
- Boot to Openbmc, there is a sysfs interface that allows to operate GPIOs
```
echo 0 > /sys/class/gpio/export
echo out > /sys/class/gpio/gpio0/direction
echo 1 > /sys/class/gpio/gpio0/value
echo 1 > /sys/class/gpio/export
echo in > /sys/class/gpio/gpio1/direction
cat /sys/class/gpio/gpio1/value
```
- Example for verifying GPIO status via GPIO Registers
```
First, you need to check spec for GPIO Port Registers.

Example: Get GPIO0 pin direction and value.
GPIO0:   GPIO0_BA = 0xf0010000
GPnOE:   offset   = 0x10
GPnDOUT: offset   = 0x0c
GPnDIN:  offset   = 0x04

Get GPIO0 pin direction by GPnOE register:
devmem 0xf0010010 32
0x30009101
(Bit 0 represents for pin GPIO0, and the value 1 means pin direction is Output)

Get GPIO0 Output pin value by GPnDOUT register:
devmem 0xf001000c 32
0x30008101
(Bit 0 represents for pin GPIO0, and the value 1 means pin value is 1)

Get GPIO1 Input pin value by GPnDIN register:
devmem 0xf0010004 32
0x00006002
(Bit 1 represents for pin GPIO1, and the value 1 means pin value is 1)
```

### U-boot test
- DTS
```
    gpio0: gpio0@f0010000 {
        compatible = "nuvoton,npcm845-gpio";
        reg = <0x0 0xf0010000 0x0 0x1000>;
        #gpio-cells = <2>;
        gpio-controller;
        gpio-bank-name = "gpio0";
        gpio-count = <32>;
        gpio-port = <0>;
    };

    gpio1: gpio1@f0011000 {
        compatible = "nuvoton,npcm845-gpio";
        reg = <0x0 0xf0011000 0x0 0x1000>;
        #gpio-cells = <2>;
        gpio-controller;
        gpio-bank-name = "gpio1";
        gpio-count = <32>;
        gpio-port = <1>;
    };

    gpio2: gpio2@f0012000 {
        compatible = "nuvoton,npcm845-gpio";
        reg = <0x0 0xf0012000 0x0 0x1000>;
        #gpio-cells = <2>;
        gpio-controller;
        gpio-bank-name = "gpio2";
        gpio-count = <32>;
        gpio-port = <2>;
    };

    gpio3: gpio3@f0013000 {
        compatible = "nuvoton,npcm845-gpio";
        reg = <0x0 0xf0013000 0x0 0x1000>;
        #gpio-cells = <2>;
        gpio-controller;
        gpio-bank-name = "gpio3";
        gpio-count = <32>;
        gpio-port = <3>;
    };

    gpio4: gpio4@f0014000 {
        compatible = "nuvoton,npcm845-gpio";
        reg = <0x0 0xf0014000 0x0 0x1000>;
        #gpio-cells = <2>;
        gpio-controller;
        gpio-bank-name = "gpio4";
        gpio-count = <32>;
        gpio-port = <4>;
    };

    gpio5: gpio5@f0015000 {
        compatible = "nuvoton,npcm845-gpio";
        reg = <0x0 0xf0015000 0x0 0x1000>;
        #gpio-cells = <2>;
        gpio-controller;
        gpio-bank-name = "gpio5";
        gpio-count = <32>;
        gpio-port = <5>;
    };

    gpio6: gpio6@f0016000 {
        compatible = "nuvoton,npcm845-gpio";
        reg = <0x0 0xf0016000 0x0 0x1000>;
        #gpio-cells = <2>;
        gpio-controller;
        gpio-bank-name = "gpio6";
        gpio-count = <32>;
        gpio-port = <6>;
    };

    gpio7: gpio7@f0017000 {
        compatible = "nuvoton,npcm845-gpio";
        reg = <0x0 0xf0017000 0x0 0x1000>;
        #gpio-cells = <2>;
        gpio-controller;
        gpio-bank-name = "gpio7";
        gpio-count = <32>;
        gpio-port = <7>;
    };

```

- Enable u-boot config
```
CONFIG_DM_GPIO=y
CONFIG_CMD_GPIO=y
CONFIG_NPCM_GPIO=y
```

- Use gpio command to operate GPIOs.
```
U-Boot>gpio set 0
gpio: pin 0 (gpio 0) value is 1
U-Boot>gpio input 1
gpio: pin 1 (gpio 1) value is 1
U-Boot>gpio toggle 0
gpio: pin 0 (gpio 0) value is 0
U-Boot>gpio input 1
gpio: pin 1 (gpio 1) value is 0
```

## UART

The EVB has FTDI J_USB_TO_UART and J_SI2_BU0 UART headers, the user can select the UART route through the DIP switch.

1. Strap Settings

    - Strap 5 of the SW_STRAP1_8 DIP switch
      * Turn on strap 5 to connect BMC debug port to SI2 interface.

    - Strap 7 of the SW1 DIP switch
      * Turn on strap 7 to isolate USB FTDI so that J_SI2_BU0 UART headers can be used.

2. FTDI J_USB_TO_UART

    - Connects a Mini-USB cable to J_USB_TO_UART
      * You will get 4 serial port options from your terminal settings, select the second one and set baud rate to 115200.

3. J_SI2_BU0 UART Headers

    - Connect a USB FTDI cable to J_SI2_BU0
      * Turn on strap 7 of the SW1 DIP switch and set baud rate to 115200.

## FIU

The Arbel EVB has mounted 4 NOR Flash:
- FIU0 - SPI0CS0 and SPI0CS2
- FIU1 - SPI1CS0
- FIU3 - SPI3CS0

### Linux test

**FIU3 test**

1. Probe flash
- In nuvoton-npcm845-evb.dts, we have enabled FIU3 and defined the partition.
```ruby
# kernel message
spi-nor spi3.0: w25q256 (32768 Kbytes)
1 fixed-partitions partitions found on MTD device spi3.0
Creating 1 MTD partitions on "spi3.0":
0x000000000000-0x000002000000 : "spi3-system1
```
```ruby
# MTD number for FIU3 is mtd7
root@evb-npcm845:~# cat /proc/mtd
dev:    size   erasesize  name
mtd0: 02000000 00001000 "bmc"
mtd1: 000c0000 00001000 "u-boot"
mtd2: 00040000 00001000 "u-boot-env"
mtd3: 00800000 00001000 "kernel"
mtd4: 01500000 00001000 "rofs"
mtd5: 00100000 00001000 "rwfs"
mtd6: 00400000 00001000 "spi1-system1"
mtd7: 02000000 00001000 "spi3-system1
```

2. Erase/Write/Read flash
```ruby
root@evb-npcm845:/tmp# tftp -g -r image-bmc 192.168.0.128
root@evb-npcm845:/tmp# flashcp -v image-bmc /dev/mtd7
Erasing block: 8192/8192 (100%)
Writing kb: 32768/32768 (100%)
Verifying kb: 32768/32768 (100%)
```

**U-boot test**

**FIU3 test**
1. Probe flash
```ruby
U-Boot>sf probe 3:0
SF: Detected w25q256 with page size 256 Bytes, erase size 4 KiB, total 32 MiB
```

2. Erase flash
```ruby
U-Boot>sf erase 0 0x1000
SF: 4096 bytes @ 0x0 Erased: OK
```

3. Write flash
```ruby
U-Boot>sf write 0x8000 0 0x1000
device 0 offset 0x0, size 0x1000
SF: 4096 bytes @ 0x0 Written: OK
```

4. Read flash

- The SPI3 CS0 is mapped to A000_0000h ~ A7FF_FFFFh, you can use `md` command to direct read flash.
- Or you can use `sf` command to read flash.

```ruby
U-Boot>md 0xa0000000
a0000000: 1400000a d503201f 00008000 00000000    ..... ..........
a0000010: 00092548 00000000 00092548 00000000    H%......H%......
a0000020: 000986d8 00000000 14000042 10003ea0    ........B....>..
a0000030: d5384241 f100303f 540000a0 f100203f    AB8.?0.....T? ..
a0000040: 54000120 f100103f 54000160 d51ec000     ..T?...`..T....
a0000050: d53e1100 b2400c00 d51e1100 d51e115f    ..>...@....._...
a0000060: 14000008 d51cc000 d2867fe0 d51c1140    ............@...
a0000070: 14000004 d518c000 d2a00600 d5181040    ............@...
a0000080: d5033fdf 94000003 9400086e 940005cd    .?......n.......
a0000090: aa1e03fd d5380000 d344fc00 92402c00    ......8...D..,@.
a00000a0: f1340c1f 54000100 d5380000 d344fc00    ..4....T..8...D.
a00000b0: 92402c00 f1341c1f 54000080 aa1d03fe    .,@...4....T....
a00000c0: d65f03c0 17fffffe 17fffffd aa1e03fd    .._.............
a00000d0: 58000340 94000611 58000300 58000321    @..X.......X!..X
a00000e0: 9400061f aa1d03fe d65f03c0 58000260    .........._.`..X
a00000f0: 14000624 d65f03c0 10003840 d5384241    $....._.@8..AB8.
```

## Network

The EVB has 3 RJ45 headers and 1 NCSI header

- J_SGMII: 1000/100/10Mbps SGMII, eth0
- J_RGMII: 1000/100/10Mbps RGMII, eth1
- J_RMII:  100/10Mbps RMII, eth3
- J_EMC: NCSI header, eth2

### Linux Test

**SGMII test**

1. Connect a network cable with J_SGMII 
```ruby
# make sure the link is up
stmmaceth f0802000.eth eth0: Link is Up - 1Gbps/Full - flow control off
```
2.  Configure static IP or get from DHCP server
```ruby
eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq qlen 1000
    link/ether 00:00:f7:a0:00:fc brd ff:ff:ff:ff:ff:ff
    inet 192.168.0.12/24 brd 192.168.0.255 scope global dynamic eth0
       valid_lft 86191sec preferred_lft 86191sec
```

3. iperf3 test

```
root@evb-npcm845:~# iperf3 -c 192.168.0.128 -R
Connecting to host 192.168.0.128, port 5201
Reverse mode, remote host 192.168.0.128 is sending
[  5] local 192.168.0.12 port 45320 connected to 192.168.0.128 port 5201
[ ID] Interval           Transfer     Bitrate
[  5]   0.00-1.00   sec   108 MBytes   909 Mbits/sec
[  5]   1.00-2.00   sec   112 MBytes   938 Mbits/sec
[  5]   2.00-3.00   sec   109 MBytes   916 Mbits/sec
[  5]   3.00-4.00   sec   112 MBytes   940 Mbits/sec
[  5]   4.00-5.00   sec   112 MBytes   938 Mbits/sec
[  5]   5.00-6.00   sec   112 MBytes   941 Mbits/sec
[  5]   6.00-7.00   sec   112 MBytes   936 Mbits/sec
[  5]   7.00-8.00   sec   112 MBytes   941 Mbits/sec
[  5]   8.00-9.00   sec   112 MBytes   941 Mbits/sec
[  5]   9.00-10.00  sec   112 MBytes   940 Mbits/sec
- - - - - - - - - - - - - - - - - - - - - - - - -
[ ID] Interval           Transfer     Bitrate
[  5]   0.00-10.00  sec  1.09 GBytes   934 Mbits/sec                  sender
[  5]   0.00-10.00  sec  1.09 GBytes   934 Mbits/sec                  receiver
 
```

### U-boot Test

**RGMII test**

```
# u-boot message
Found phy_id=0x600d8595 addr=0x00 eth0: eth@f0802000
Found phy_id=0x600d84a2 addr=0x00 , eth1: eth@f0804000
Found phy_id=0x004061e4 addr=0x00 , eth3: eth@f0808000
```
1. Connect a network cable with J_SGMII   

2. Make sure the MAC address has been assigned
```
setenv ethact gmac2
setenv eth1addr 00:00:F7:A0:00:FD
setenv eth2addr 00:00:F7:A0:00:FE
setenv eth3addr 00:00:F7:A0:00:FF
setenv ethaddr 00:00:F7:A0:00:FC
```

2. configure IP address
```
setenv gatewayip            192.168.0.254
setenv serverip             192.168.0.128
setenv ipaddr               192.168.0.12
setenv netmask              255.255.255.0
```

3. tftp test
```
Using gmac device
TFTP from server 192.168.0.128; our IP address is 192.168.0.12
Filename 'image-bmc'.
Load address: 0x10000000
Loading: #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         ###########
         11.9 MiB/s
done
Bytes transferred = 33554432 (2000000 hex)
```

## I3C

The EVB has I3C0~I3C5 interfaces on the J_I3C header.

### Linux Test

**SPD5118 device**
- Connect a Renesas SPD5118 module to EVB I3C2 interface
  * connect J_I3C.5 to device SCL
  * connect J_I3C.6 to device SDA
  * connect J3.1 (VCC_3.3V) to device 3V3
  * Wire GND between EVB and DIMM
- Edit nuvoton-npcm845-evb.dts. (The slave static address of SPD Hub depends on HSA pin of DIMM)
```
    i3c2: i3c@fff12000 {
        status = "okay";
        i2c-scl-hz = <400000>;
        i3c-scl-hz = <4000000>;
        jedec,jesd403;
        hub@0x57 {
            reg = <0x57 0x4CC 0x51180000>;
        };
        ts0@0x17 {
            reg = <0x17 0x4CC 0x51110000>;
        }
        ts1@0x37 {
            reg = <0x37 0x4CC 0x51110001>;
        }
        pmic@0x4f {
            reg = <0x4F 0x4CC 0x89000000>;
        }
    };
```
- Enable Kernel config
```
CONFIG_I3C=y
CONFIG_I3CDEV=y
CONFIG_SVC_I3C_MASTER=y
```
- There are 4 I3C device nodes on Bus 2.
```
# ls /dev/i3c-2-*
/dev/i3c-2-4cc51110000  /dev/i3c-2-4cc51180000
/dev/i3c-2-4cc51110001  /dev/i3c-2-4cc89000000

HUB: /dev/i3c-2-4cc51180000
Temperature Sensor 0: /dev/i3c-2-4cc51110000
Temperature Sensor 1: /dev/i3c-2-4cc51110001
PMIC: /dev/i3c-2-4cc89000000
```
- Use [i3ctransfer](https://github.com/vitor-soares-snps/i3c-tools) tool to test
- Read HUB device type
```
root@evb-npcm845:~# i3ctransfer -d /dev/i3c-2-4cc51180000 -w "0x00,0x00" -r 2
Success on message 0
  received data:
    0x51
    0x18
```
- Read 10 bytes from SPD5118 NVM addr 0x0
```
i3ctransfer -d /dev/i3c-2-4cc51180000 -w "0x80,0x00"
i3ctransfer -d /dev/i3c-2-4cc51180000 -r 10
```
- Read TS0 device type
```
root@evb-npcm845:~# i3ctransfer -d /dev/i3c-2-4cc51110000 -w "0x00" -r 2
Success on message 0
  received data:
    0x51
    0x11
```
- Read TS0 temperature data
```
root@evb-npcm845:~# i3ctransfer -d /dev/i3c-2-4cc51110000 -w "0x31" -r 2
Success on message 0
  received data:
    0xb4
    0x01
```
- Read PMIC register. The following example is to read register 0x32.
```
# i3ctransfer -d /dev/i3c-2-4cc89000000 -w "0x32" -r 1
Success on message 0
  received data:
    0x40
```
## JTAG Master

The EVB has JTAG Master 1 interface on the J_JTAGM header.

### Linux Test

**Onboard CPLD**
- Route JTAG Master 1 interface to onboard CPLD.
```
echo 70 > /sys/class/gpio/export
echo out > /sys/class/gpio/gpio70/direction
echo 1 > /sys/class/gpio/gpio70/value
```
- Read CPLD ID, no error outputed if ID is correct.
```
# loadsvf -d /dev/jtag0 -s arbelevb_readid.svf
loading time is 0 ms
```
- Program CPLD, arbelevb_cpld.svf is the firmware file.
```
loadsvf -d /dev/jtag0 -s arbelevb_cpld.svf
```
- After CPLD is programmed, three LEDs (blue/yellow/red, near to SW1) are turned on.

- The CPLD SVF can be downloaded from here:
[arbelevb_cpld.svf](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm845/recipes-evb-npcm845/loadsvf/files/arbelevb_cpld.svf ), [arbelevb_readid.svf](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm845/recipes-evb-npcm845/loadsvf/files/arbelevb_readid.svf )

## SMBus

The EVB has 27 SMB interface modules on J3 and J4 headers.

There is a TMP100 sensor (0x48) connected to SMB module 6.

### Linux test

**TMP100 sensor**
- The following example in EVB debug console is to detect TMP100.
```
i2cdetect -y -q 6
```
- Or one can use the linux dts and driver configurations below.  
> _Edit nuvoton-npcm845-evb.dts_  
```
  &i2c6 {
    status = "okay";
    tmp100@48 {
      compatible = "tmp100";
      reg = <0x48>;
      status = "okay";
    };
  };
```
- Enable kernel configuration
```
CONFIG_REGMAP_I2C=y
CONFIG_I2C=y
CONFIG_I2C_BOARDINFO=y
CONFIG_I2C_COMPAT=y
CONFIG_I2C_CHARDEV=y
CONFIG_I2C_HELPER_AUTO=y
CONFIG_I2C_NPCM7XX=y
CONFIG_SENSORS_LM75=y
```
- Boot EVB to Openbmc, there is a sysfs path that shows a hwmon interface.
```
/sys/class/hwmon/hwmon0/
```
**SMB acts as a slave emulated EEPROM**

The SMB module's slave functionality could be tested by the following
procedure.

Wire the SMB0 module and SMB1 module. The SMB1 module acts as a slave eeprom.

- Enable kernel configuration
```
CONFIG_REGMAP_I2C=y
CONFIG_I2C=y
CONFIG_I2C_BOARDINFO=y
CONFIG_I2C_COMPAT=y
CONFIG_I2C_CHARDEV=y
CONFIG_I2C_HELPER_AUTO=y
CONFIG_I2C_NPCM7XX=y
CONFIG_I2C_SLAVE=y
CONFIG_I2C_SLAVE_EEPROM=y
```
- Build-time (linux dts) configuration or 
> _Edit nuvoton-npcm845-evb.dts_  
```
  &i2c1 {
    status = "okay";
    slave_eeprom:slave_eeprom@40000064 {
      compatible = "slave-24c02";
      reg = <0x40000064>;
      status = "okay";
    };
  };
```
- Runtime configuration  
Input the following command in the EVB debug console.  
```
echo slave-24c02 0x1064 > /sys/bus/i2c/devices/i2c-1/new_device
```
- The emulated eeprom device (0x64) is detected by the following command in the
EVB debug console.
```
i2cdetect -y -q 0
```
- The following commands could be used to validate the access to the emulated
eeprom.
```
i2ctransfer -f -y 0 w2@0x64 0 0 r2
i2ctransfer -f -y 0 w4@0x64 0 0 1 3 r0
i2ctransfer -f -y 0 w2@0x64 0 0 r2
```

### U-boot test

A sensor (0x48) is connected to SMB module 6.  
Wire between SMB module 6 and each SMB target module (0-5, 7-26) before inputting commands in uboot.
- The following commands could be used to validate the access to the sensor.
```
I2c dev 0
I2c probe 0x48
```
> 0 is an example. Replace it with 0-5, 7-26.

## ESPI

The EVB has the J_eSPI header to support ESPI transactions.

### U-boot test
1. Wiring
- Connected to the host ESPI interface.
  * eSPI_ALERT_N (optional): ESPI alert pin.  
  * eSPI_RST_N: ESPI reset pin.  
  * eSPI_IO0: ESPI IO[0] pin.  
  * eSPI_IO1: ESPI IO[1] pin.  
  * eSPI_IO2: ESPI IO[2] pin.  
  * eSPI_IO3: ESPI IO[3] pin.  
  * eSPI_CLK: ESPI clock pin.  
  * eSPI_CS_N: ESPI chip select pin.  

> _If _eSPI_ALERT_N is connected, please configure the alert mode accordingly on the host side._  
> _To connect the external power 1.8V or 1.0V, please short the pin 2 only on the JP_ESPI_PWR header on EVB._  

2. ESPI channel support declaration in u-boot configuration
- Enable u-boot configuration
> _Edit nuvoton-npcm845-evb.dts in u-boot_  
```
  config {
    espi-channel-support = <0xf>;
  };
```
> _The configuration above claims that all channels would be supported._  
- Rebuild and flash the u-boot binary.  

3. Validate ESPI
- Boot EVB into u-boot first and then the host device.  
- Check if values of the following registers are configured properly.  
  * Bits **24~27** of **ESPICFG** register are set to **1** to support all four
    channels.  
  * The value of **ESPIHINDP** register is expected to be **0x0001111f**.
  * Bit **8** of **MFSEL4** register is set to **1**.  
- Issue ESPI request packets from the host.

## SIOX

The EVB has two SIOX modules connecting to CPLD. You could control LED_CPLD_7 and do loopback test.

### Linux test
- Please follow JTAG Master section to program CPLD first
- The current nuvoton-npcm845-evb.dts is defined to support 64 input and 64 output of the second siox module, and the ninth output pin is for green LED
```
sgpio2: sgpio@102000 {
	status = "okay";
	bus-frequency = <16000000>;
	nin_gpios = <64>;
	nout_gpios = <64>;
	gpio-line-names =
		"POWER_OUT","RESET_OUT","","","","","","NMI_OUT",
		"g_led","","","","","","","";
		"","","","","","","","",
		"","","","","","","","",
		"","","","","","","","",
		"","","","","","","","",
		"","","","","","","","",
		"","","","","","","","",
		"","","PS_PWROK","POST_COMPLETE","POWER_BUTTON","RESET_BUTTON","NMI_BUTTON","",
		"","","","","","","","",
		"","","","","","","","",
		"","","","","","","","",
		"","","","","","","","",
		"","","","","","","","",
		"","","","","","","","",
		"","","","","","","","";
};	
```
- Enable Kernel config
```
CONFIG_GPIO_NPCM_SGPIO=y
```
- Boot EVB to Openbmc, you can check gpiochip8 information
```
root@evb-npcm845:~# gpioinfo 8
gpiochip8 - 128 lines:
        line   0:  "POWER_OUT"       unused  output  active-high
        line   1:  "RESET_OUT"       unused  output  active-high
        line   2:      unnamed       unused  output  active-high
        line   3:      unnamed       unused  output  active-high
        line   4:      unnamed       unused  output  active-high
        line   5:      unnamed       unused  output  active-high
        line   6:      unnamed       unused  output  active-high
        line   7:      "NMI_OUT" "power-control" output active-high [used]
        line   8:      "g_led"       unused  output  active-high
        line   9:      unnamed       unused  output  active-high
        line  10:      unnamed       unused  output  active-high
        line  11:      unnamed       unused  output  active-high
        line  12:      unnamed       unused  output  active-high
        line  13:      unnamed       unused  output  active-high
        line  14:      unnamed       unused  output  active-high
        line  15:      unnamed       unused  output  active-high
        line  16:      unnamed       unused  output  active-high
        line  17:      unnamed       unused  output  active-high
        line  18:      unnamed       unused  output  active-high
        line  19:      unnamed       unused  output  active-high
        line  20:      unnamed       unused  output  active-high
        line  21:      unnamed       unused  output  active-high
        line  22:      unnamed       unused  output  active-high
        line  23:      unnamed       unused  output  active-high
        line  24:      unnamed       unused  output  active-high
        line  25:      unnamed       unused  output  active-high
        line  26:      unnamed       unused  output  active-high
        line  27:      unnamed       unused  output  active-high
        line  28:      unnamed       unused  output  active-high
        line  29:      unnamed       unused  output  active-high
        line  30:      unnamed       unused  output  active-high
        line  31:      unnamed       unused  output  active-high
        line  32:      unnamed       unused  output  active-high
        line  33:      unnamed       unused  output  active-high
        line  34:      unnamed       unused  output  active-high
        line  35:      unnamed       unused  output  active-high
        line  36:      unnamed       unused  output  active-high
        line  37:      unnamed       unused  output  active-high
        line  38:      unnamed       unused  output  active-high
        line  39:      unnamed       unused  output  active-high
        line  40:      unnamed       unused  output  active-high
        line  41:      unnamed       unused  output  active-high
        line  42:      unnamed       unused  output  active-high
        line  43:      unnamed       unused  output  active-high
        line  44:      unnamed       unused  output  active-high
        line  45:      unnamed       unused  output  active-high
        line  46:      unnamed       unused  output  active-high
        line  47:      unnamed       unused  output  active-high
        line  48:      unnamed       unused  output  active-high
        line  49:      unnamed       unused  output  active-high
        line  50:      unnamed       unused  output  active-high
        line  51:      unnamed       unused  output  active-high
        line  52:      unnamed       unused  output  active-high
        line  53:      unnamed       unused  output  active-high
        line  54:      unnamed       unused  output  active-high
        line  55:      unnamed       unused  output  active-high
        line  56:      unnamed       unused  output  active-high
        line  57:      unnamed       unused  output  active-high
        line  58:      unnamed       unused  output  active-high
        line  59:      unnamed       unused  output  active-high
        line  60:      unnamed       unused  output  active-high
        line  61:      unnamed       unused  output  active-high
        line  62:      unnamed       unused  output  active-high
        line  63:      unnamed       unused  output  active-high
        line  64:      unnamed       unused   input  active-high
        line  65:      unnamed       unused   input  active-high
        line  66:   "PS_PWROK" "power-control" input active-high [used]
        line  67: "POST_COMPLETE" "power-control" input active-high [used]
        line  68: "POWER_BUTTON" "power-control" input active-high [used]
        line  69: "RESET_BUTTON" "power-control" input active-high [used]
        line  70: "NMI_BUTTON" "power-control" input active-high [used]
        line  71:      unnamed       unused   input  active-high
        line  72:      unnamed       unused   input  active-high
        line  73:      unnamed       unused   input  active-high
        line  74:      unnamed       unused   input  active-high
        line  75:      unnamed       unused   input  active-high
        line  76:      unnamed       unused   input  active-high
        line  77:      unnamed       unused   input  active-high
        line  78:      unnamed       unused   input  active-high
        line  79:      unnamed       unused   input  active-high
        line  80:      unnamed       unused   input  active-high
        line  81:      unnamed       unused   input  active-high
        line  82:      unnamed       unused   input  active-high
        line  83:      unnamed       unused   input  active-high
        line  84:      unnamed       unused   input  active-high
        line  85:      unnamed       unused   input  active-high
        line  86:      unnamed       unused   input  active-high
        line  87:      unnamed       unused   input  active-high
        line  88:      unnamed       unused   input  active-high
        line  89:      unnamed       unused   input  active-high
        line  90:      unnamed       unused   input  active-high
        line  91:      unnamed       unused   input  active-high
        line  92:      unnamed       unused   input  active-high
        line  93:      unnamed       unused   input  active-high
        line  94:      unnamed       unused   input  active-high
        line  95:      unnamed       unused   input  active-high
        line  96:      unnamed       unused   input  active-high
        line  97:      unnamed       unused   input  active-high
        line  98:      unnamed       unused   input  active-high
        line  99:      unnamed       unused   input  active-high
        line 100:      unnamed       unused   input  active-high
        line 101:      unnamed       unused   input  active-high
        line 102:      unnamed       unused   input  active-high
        line 103:      unnamed       unused   input  active-high
        line 104:      unnamed       unused   input  active-high
        line 105:      unnamed       unused   input  active-high
        line 106:      unnamed       unused   input  active-high
        line 107:      unnamed       unused   input  active-high
        line 108:      unnamed       unused   input  active-high
        line 109:      unnamed       unused   input  active-high
        line 110:      unnamed       unused   input  active-high
        line 111:      unnamed       unused   input  active-high
        line 112:      unnamed       unused   input  active-high
        line 113:      unnamed       unused   input  active-high
        line 114:      unnamed       unused   input  active-high
        line 115:      unnamed       unused   input  active-high
        line 116:      unnamed       unused   input  active-high
        line 117:      unnamed       unused   input  active-high
        line 118:      unnamed       unused   input  active-high
        line 119:      unnamed       unused   input  active-high
        line 120:      unnamed       unused   input  active-high
        line 121:      unnamed       unused   input  active-high
        line 122:      unnamed       unused   input  active-high
        line 123:      unnamed       unused   input  active-high
        line 124:      unnamed       unused   input  active-high
        line 125:      unnamed       unused   input  active-high
        line 126:      unnamed       unused   input  active-high
        line 127:      unnamed       unused   input  active-high

```
- Now, you can turn on/off LED_CPLD_7
```
root@evb-npcm845:~# gpioset 8 8=0
root@evb-npcm845:~# gpioset 8 8=1
```
- GPIO interrupt loopback test
```
root@evb-npcm845:~# gpiomon 8 64 &
root@evb-npcm845:~# gpioset 8 0=1
event:  RISING EDGE offset: 64 timestamp: [   83882.867414528]
root@evb-npcm845:~# gpioset 8 0=0
event: FALLING EDGE offset: 64 timestamp: [   83884.267443984]
```

### Interrupt Stress Test
- Monitor pin 64 ~ 79 with gpiomon
```
root@evb-npcm845:~# gpiomon 8 64 65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 &
```
- Prepare a script to toggle pin 0 ~ 15 which are loopback to 64 ~ 79 repeatedly
```
root@evb-npcm845:~# gpioset 8 0=1 1=1 2=1 3=1 4=1 5=1 6=1 7=1 8=1 9=1 10=1 11=1 12=1 13=1 14=1 15=1
root@evb-npcm845:~# gpioset 8 0=0 1=0 2=0 3=0 4=0 5=0 6=0 7=0 8=0 9=0 10=0 11=0 12=0 13=0 14=0 15=0
```
- Monitor pin 96 ~ 103 from another console (login via SSH) 
```
root@evb-npcm845:~# gpiomon 8 96 97 98 99 100 101 102 103
```
- Connect and disconnect pin 96 ~ 103 to 3.3V on J_CPLD header repeatedly.
```
J_CPLD.1  (pin 96)
J_CPLD.3  (pin 97)
J_CPLD.5  (pin 98)
J_CPLD.7  (pin 99)
J_CPLD.9  (pin 100)
J_CPLD.10 (pin 101)
J_CPLD.11 (pin 102)
J_CPLD.12 (pin 103)
```
- Result
<img align="right" width="15%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/ARBEL_EVB_SIOX_INT_TEST.png">

```
From the left consloe, you could see interrupt 64 ~ 79 repeatedly.
From the right consloe, you could see gpiomon did get every interrupt from 96 to 103.
```

## SPIX

The EVB has one SPIX module connecting to CPLD.
You could controll LED_CPLD_5, LED_CPLD_6 and LED_CPLD_8 to do loopback test.

### U-boot test
1. Please follow JTAG Master section to program CPLD first.
2. SPIX Configuration:
```
mw.l 0xF080026C 0x19a08002
mw.l 0xF0800274 0x00000000

md.l 0xF0801008 1
f0801008: 04139add

mw.l 0xF0801058 0x000c9bc8
mw.l 0xFB001000 0x00002300
mw.l 0xFB001004 0x03000002
```
3. Read SRAM:
```
md.b 0xF8000200 80

f8000200: f0 f0 f0 f0 f0 f0 f0 f0 f0 f0 f0 f0 f0 f0 f0 f0    ................
f8000210: a5 a5 a5 a5 a5 a5 a5 a5 a5 a5 a5 a5 a5 a5 a5 a5    ................
f8000220: 0f 0f 0f 0f 0f 0f 0f 0f 0f 0f 0f 0f 0f 0f 0f 0f    ................
f8000230: 5a 5a 5a 5a 5a 5a 5a 5a 5a 5a 5a 5a 5a 5a 5a 5a    ZZZZZZZZZZZZZZZZ
f8000240: 4e 50 43 4d 38 6d 6e 78 20 41 72 62 65 6c 20 45    NPCM8mnx Arbel E
f8000250: 56 42 00 00 00 00 00 00 00 00 00 00 00 00 00 00    VB..............
f8000260: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00    ................
f8000270: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00    ................
```
4. Turn ON/OFF CPLD LED:
```
Turn On Red LED
mw.b 0xF8000000 0x01

Turn Off Red LED
mw.b 0xF8000000 0x00

Turn On Yellow LED
mw.b 0xF8000001 0x00

Turn Off Yellow LED
mw.b 0xF8000001 0x01

Turn On Blue LED
mw.b 0xF8000001 0x0

Turn Off Blue LED
mw.b 0xF8000001 0x2
```

## VGA

The EVB has a VGA output port.

**How to use**
1. Install Arbel EVB on a host PC via PCIE socket
2. Power on Arbel EVB
3. Waiting for arbel left bootblock
4. Power on PC host
5. Once PC run into OS, you should get OS screen from EVB's VGA port.
6. If you didn't see the OS screen, please contact the developer.

### Linux test
- KVM
1. Prepare a motherboard and connect Arbel EVB through PCI-E.
2. Connect a USB cable from motherboard to J_USB1_DEV header of EVB.
3. Make sure your workstation and EVB are in the same network.
4. Launch a browser in your workstation and enter below URL, you will see the OpenBMC home page.
    ```
    https://<Arbel_EVB_IP>
    ```
5. Use below username/password to login and navigate to the `KVM` page.
    ```
    Username: root
    Password: 0penBmc
    ```

    > _NOTE: You can use [Real VNC Viewer](https://www.realvnc.com/en/connect/download/viewer/) with below preferences instead of OpenBMC Web._

    ```
    /* Preference expert settings of Real VNC Viewer */
    Quality: Custom
    PreferredEncoding: Hextile
    ColorLevel: rgb565
    PointerEventInterval: 30
    ```

6. Power up the motherboard and the video output will show on the WebUI (or Real VNC Viewer).

**Performance**

* Host OS: Windows Server 2016

|Playing video: [AQUAMAN](https://www.youtube.com/watch?v=2wcj6SrX4zw)|[Real VNC Viewer](https://www.realvnc.com/en/connect/download/viewer/) | WebUI (noVNC Viewer)
:---------------|:------------|:-----------|
Host Resolution | Average FPS | Average FPS|
1024 x 768      |  41         |  26        |
1280 x 1024     |  40         |  17        |
1600 x 1200     |  21         |  11        |

## USB

The EVB has 2 x USB device ports and 1 x USB host port.
- J_USB1_DEV: USB Port 1 - Device, Mini-USB Type B
- J_USB2_HOST: USB Port 2 - Host, USB Type A
- J_USB3_HOST_DEV: USB Port 3 - Host/Device, Micro-USB Type AB

**UDC Connectivity**
- The UDC0~7 are used for USB port 1,
- The UDC8 is used for USB port 3 if USB device mode
- The UDC9 is used for USB port 2 if USB device mode

### Linux test
**Virtual Media test in OpenBMC**
1. Connects J_USB1_DEV and host PC with a USB cable

2. Clone a physical USB drive to an image file
    * For Linux - use tool like **dd**
      ```
      dd if=/dev/sda of=usb.img bs=1M count=100
      ```
      > _**bs** here is block size and **count** is block count._
      >
      > _For example, if the size of your USB drive is 1GB, then you could set "bs=1M" and "count=1024"_

    * For Windows - use tool like **Win32DiskImager.exe**

      > _NOTICE : A simple *.iso file cannot work for this._

2. Enable Virtual Media

    1. Login and navigate to webpage of VM on your browser
        ```
        https://<Arbel_EVB_IP>/#/control/virtual-media
        ```
    2. Operations of Virtual Media
        * After `Choose File`, click `Start` to start VM network service
        * After clicking `Start`, you will see a new USB device on HOST OS
        * If you want to stop this service, just click `Stop` to stop VM network service.

### U-boot test

**Host test**

Use a usb disk formatted in FAT32 and store some contents in it.  
Insert it into J_USB2_HOST first and then input the following commands.  
The contents are expected to be displayed.
```
usb reset
usb tree
fatls usb 0
```

**Device test**

Connect a mini usb cable between J_USB1_DEV and the host computer.  
Input the following commands.
```
ums 0 mmc 0
```
A prompt window will show on the host computer to format the USB drive.  
You can format it in FAT32 to proceed the EMMC test.

## ADC
The EVB contains an Analog-to-Digital Converter (ADC) input interface.

VCC source is 1.2v and voltage is divided to:
- ADCI0: 54 mV
- ADCI1: 110 mV
- ADCI2: 200 mV
- ADCI3: 384 mV 
- ADCI4: 816 mV
- ADCI5: 1000 mV
- ADCI6: 1090 mV
- ADCI7: 1146 mV

### Linux test
- Read ADC value from hwmon path
```
cat /sys/class/hwmon/hwmon4/in1_input
cat /sys/class/hwmon/hwmon4/in2_input
cat /sys/class/hwmon/hwmon4/in3_input
cat /sys/class/hwmon/hwmon4/in4_input
cat /sys/class/hwmon/hwmon4/in5_input
cat /sys/class/hwmon/hwmon4/in6_input
cat /sys/class/hwmon/hwmon4/in7_input
cat /sys/class/hwmon/hwmon4/in8_input
```

## FAN

The EVB has 4 x FAN connectors
- FAN0: PWM0/FANIN0
- FAN1: PWM1/FANIN1
- FAN2: PWM2/FANIN2
- FAN3: PWM3/FANIN3 

### Linux test
1. Test FAN RPMS by command
```
echo 25 > /sys/class/hwmon/hwmon1/pwm1
echo 50 > /sys/class/hwmon/hwmon1/pwm2
echo 100 > /sys/class/hwmon/hwmon1/pwm3
echo 255 > /sys/class/hwmon/hwmon1/pwm4
```
2. Read FAN RPMS by command
```
cat /sys/class/hwmon/hwmon1/fan1_input
cat /sys/class/hwmon/hwmon1/fan2_input
cat /sys/class/hwmon/hwmon1/fan3_input
cat /sys/class/hwmon/hwmon1/fan4_input
```
### U-boot test
1. PWM Initial:
```
mw.l 0xf0800264 0x00ff000f 1
mw.l 0xf0103000 0x00000909 1
mw.l 0xf0103004 0x00004444 1
mw.l 0xf0103008 0x00099909 1

PWM0:
mw.l 0xf010300c 0x000000ff 1
mw.l 0xf0103010 0x0 1
mw.l 0xf0103010 0xff 1

PWM1:
mw.l 0xf0103018 0x000000ff 1
mw.l 0xf010301c 0x0 1
mw.l 0xf010301c 0xff 1

PWM2:
mw.l 0xf0103024 0x000000ff 1
mw.l 0xf0103028 0x0 1
mw.l 0xf0103028 0xff 1

PWM3:
mw.l 0xf0103030 0x000000ff 1
mw.l 0xf0103034 0x0 1
mw.l 0xf0103034 0xff 1
```
2. Read FAN RPMS
```
MFT0 initial:
mw.b 0xf0180008 0xff 1
mw.b 0xf018000a 0x09 1
mw.b 0xf018000c 0x64 1
mw.w 0xf0180014 0xafff 1
mw.w 0xf0180016 0xafff 1
mw.b 0xf0180018 0x44 1
mw.w 0xf018001a 0x0 1
mw.w 0xf018001c 0x0 1

Read FANIN0 value:
md.w 0xf0180002
f0180002: f88c
```
### Convert the raw reading to RPM
1. Converting formula
```
RPM = ((input_clk_freq * 60) / (fan_cnt * fan_pls_per_rev));

Here, suppose that input_clk_freq = 122070 Hz and fan_pls_per_rev = 2
(fan_pls_per_rev that means Fan Pulse Per Revolution)

And fan_cnt = 0xFFFF – (raw reading from TnCRA/TnCRB register)
Thus, we can simplify calculate RPM = 3662100 / fan_cnt
```
2. For example
```
Calculate RPM 1485:
fan_cap = 0xf65d (TnCRA value get from uboot: md.w 0xf0180002 1)
fan_cnt = 0x9a2
fan1 = 3662100 / 0x9a2 = 1485 rpm

Calculate RPM 1209:
fan_cap = 0xf42a (TnCRA value get from uboot: md.w 0xf0180002 1)
fan_cnt = 0xbd5
fan1 = 3662100 / 0xbd5 = 1209 rpm
```

## TMPS

BMC Temperature Sensor (TMPS)

### Linux test
- Read BMC Temperature from sys node
```
cat /sys/class/thermal/thermal_zone0/temp
cat /sys/class/thermal/thermal_zone1/temp
```

## PCIE RC

The PCIE RC is used by the BMC CPU to control external PCIe devices connected to it.

The EVB has one J_PCIE_RC header.

### Linux test

- Insert a Nuvoton EVB into J_PCIE_RC header.  
Here a Poleg EVB is used.  

- Power up the Poleg EVB and then the Arbel EVB.

- Locate the Poleg EVB device by inputting the **lspci** command on the openbmc debug console. Here is an example result.  
```
00:00.0 PCI bridge: PLDA Device 1111 (rev 01)
01:00.0 PCI bridge: PLDA PCI Express Bridge (rev 02)
02:00.0 VGA compatible controller: Matrox Electronics Systems Ltd. Integrated Matrox G200eW3 Graphics Controller (rev 04)
02:01.0 Unassigned class [ff00]: Winbond Electronics Corp Device 0750 (rev 04)
```
- The **02:01.0** is the target and the following commands on the openbmc debug console show more information.  
```
cd /sys/bus/pci/devices/0000\:02\:01.0
echo 1 > enable
lspci -v -s 02:01.0 -xxx
```
The example result is:
```
02:01.0 Unassigned class [ff00]: Winbond Electronics Corp Device 0750 (rev 04)
        Subsystem: Winbond Electronics Corp Device 0750
        Flags: slow devsel
        Memory at eb810000 (32-bit, non-prefetchable) [size=32K]
        Capabilities: [78] Power Management version 1
lspci: Unable to load libkmod resources: error -2
00: 50 10 50 07 02 00 90 84 04 00 00 ff 00 00 00 00
10: 00 00 81 eb 00 00 00 00 00 00 00 00 00 00 00 00
20: 00 00 00 00 00 00 00 00 00 00 00 00 50 10 50 07
30: 00 00 00 00 78 00 00 00 00 00 00 00 00 01 10 20
40: 00 00 00 00 40 61 22 46 00 00 00 00 00 00 00 00
50: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
60: 50 27 a9 00 00 00 00 00 00 00 00 00 00 00 00 00
70: 00 00 00 00 00 00 00 00 01 00 21 00 00 00 00 00
80: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
90: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
a0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
b0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
c0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
d0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
e0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
f0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
```
- Display PCIE device memory information  
The memory region is 0xeb810000 in the example result and the following command on the openbmc debug console can show memory information in the PCIE device.
```
devmem 0xeb810000 32
```
The example result is:
```
0x171BDE9B
```

## EMMC

The EVB has an 8G EMMC

### Linux test
1. Use as internal storage
```ruby
mkfs.ext4 /dev/mmcblk0
mkdir tmp
mount /dev/mmcblk0 tmp
```
	
2. Export by Mass Storage
- You need to setup configfs for mass storage first, then export EMMC device node to below path.
```ruby
echo "/dev/mmcblk0" > /sys/kernel/config/usb_gadget/mmc-storage/functions/mass_storage.usb0/lun.0/file
```

### U-boot test

Follow the test steps in usb device test scenario and format the emmc in FAT32.  
It's a USB drive attached to the host computer.

- Create a txt file "test.txt" with some content in the USB drive and unplug the mini usb cable from the host computer.  
- Input the following commands.
```ruby
fatls mmc 0
fatload mmc 0 0x12000000 test.txt
md.l 0x12000000
```
You can check if the content at 0x12000000 is the same as what's stored in test.txt.

## BIOS POST Code

- The EVB implements a FIFO for monitoring BIOS POST Code.
- Typically, this feature is used by the BMC to watch host boot progress via port 0x80 writes made by the BIOS during the boot process.

### Linux test
```ruby
# OpenBMC BIOS post code app
snoopd -b 1 -d /dev/npcm7xx-lpc-bpc0
```

### U-boot test
```ruby
# BIOS POST Code Initial for Port 80:
mw.b 0xf0007046 0x98
mw.b 0xf000704c 0x28
mw.b 0xf0007050 0x80

# Read BIOS POST Code from Port 80:
md.b 0xf000704a 1
```

## AES

### Linux test
```
Please add to the kernel configuration:
CONFIG_CRYPTO_TEST=m
And copy tcrypt.ko to npcm845 EVB

root@evb-npcm845:~# insmod ./tcrypt.ko mode=500
tcrypt:
testing speed of async ecb(aes) (Nuvoton-ecb-aes) encryption
tcrypt: test 0 (128 bit key, 16 byte blocks): 1 operation in 6948 cycles (16 bytes)
tcrypt: test 1 (128 bit key, 64 byte blocks): 1 operation in 7169 cycles (64 bytes)
tcrypt: test 2 (128 bit key, 128 byte blocks): 1 operation in 7187 cycles (128 bytes)
tcrypt: test 3 (128 bit key, 256 byte blocks): 1 operation in 7845 cycles (256 bytes)
tcrypt: test 4 (128 bit key, 1024 byte blocks): 1 operation in 11456 cycles (1024 bytes)
tcrypt: test 5 (128 bit key, 1424 byte blocks): 1 operation in 13156 cycles (1424 bytes)
tcrypt: test 6 (128 bit key, 4096 byte blocks): 1 operation in 31718 cycles (4096 bytes)
tcrypt: test 7 (192 bit key, 16 byte blocks): 1 operation in 6887 cycles (16 bytes)
tcrypt: test 8 (192 bit key, 64 byte blocks): 1 operation in 6967 cycles (64 bytes)
tcrypt: test 9 (192 bit key, 128 byte blocks): 1 operation in 7360 cycles (128 bytes)
tcrypt: test 10 (192 bit key, 256 byte blocks): 1 operation in 7999 cycles (256 bytes)
tcrypt: test 11 (192 bit key, 1024 byte blocks): 1 operation in 12020 cycles (1024 bytes)
tcrypt: test 12 (192 bit key, 1424 byte blocks): 1 operation in 14139 cycles (1424 bytes)
tcrypt: test 13 (192 bit key, 4096 byte blocks): 1 operation in 41733 cycles (4096 bytes)
tcrypt: test 14 (256 bit key, 16 byte blocks): 1 operation in 6768 cycles (16 bytes)
tcrypt: test 15 (256 bit key, 64 byte blocks): 1 operation in 6971 cycles (64 bytes)
tcrypt: test 16 (256 bit key, 128 byte blocks): 1 operation in 7317 cycles (128 bytes)
tcrypt: test 17 (256 bit key, 256 byte blocks): 1 operation in 8060 cycles (256 bytes)
tcrypt: test 18 (256 bit key, 1024 byte blocks): 1 operation in 12331 cycles (1024 bytes)
tcrypt: test 19 (256 bit key, 1424 byte blocks): 1 operation in 14455 cycles (1424 bytes)
tcrypt: test 20 (256 bit key, 4096 byte blocks): 1 operation in 35507 cycles (4096 bytes)
tcrypt:
testing speed of async ecb(aes) (Nuvoton-ecb-aes) decryption
tcrypt: test 0 (128 bit key, 16 byte blocks): 1 operation in 6700 cycles (16 bytes)
tcrypt: test 1 (128 bit key, 64 byte blocks): 1 operation in 6967 cycles (64 bytes)
tcrypt: test 2 (128 bit key, 128 byte blocks): 1 operation in 7307 cycles (128 bytes)
tcrypt: test 3 (128 bit key, 256 byte blocks): 1 operation in 8042 cycles (256 bytes)
tcrypt: test 4 (128 bit key, 1024 byte blocks): 1 operation in 12257 cycles (1024 bytes)
tcrypt: test 5 (128 bit key, 1424 byte blocks): 1 operation in 14411 cycles (1424 bytes)
tcrypt: test 6 (128 bit key, 4096 byte blocks): 1 operation in 36638 cycles (4096 bytes)
tcrypt: test 7 (192 bit key, 16 byte blocks): 1 operation in 6747 cycles (16 bytes)
tcrypt: test 8 (192 bit key, 64 byte blocks): 1 operation in 7799 cycles (64 bytes)
tcrypt: test 9 (192 bit key, 128 byte blocks): 1 operation in 7300 cycles (128 bytes)
tcrypt: test 10 (192 bit key, 256 byte blocks): 1 operation in 8146 cycles (256 bytes)
tcrypt: test 11 (192 bit key, 1024 byte blocks): 1 operation in 13644 cycles (1024 bytes)
tcrypt: test 12 (192 bit key, 1424 byte blocks): 1 operation in 15090 cycles (1424 bytes)
tcrypt: test 13 (192 bit key, 4096 byte blocks): 1 operation in 43614 cycles (4096 bytes)
tcrypt: test 14 (256 bit key, 16 byte blocks): 1 operation in 6737 cycles (16 bytes)
tcrypt: test 15 (256 bit key, 64 byte blocks): 1 operation in 7038 cycles (64 bytes)
tcrypt: test 16 (256 bit key, 128 byte blocks): 1 operation in 7404 cycles (128 bytes)
tcrypt: test 17 (256 bit key, 256 byte blocks): 1 operation in 8098 cycles (256 bytes)
tcrypt: test 18 (256 bit key, 1024 byte blocks): 1 operation in 12318 cycles (1024 bytes)
tcrypt: test 19 (256 bit key, 1424 byte blocks): 1 operation in 14505 cycles (1424 bytes)
tcrypt: test 20 (256 bit key, 4096 byte blocks): 1 operation in 35526 cycles (4096 bytes)
tcrypt:
testing speed of async cbc(aes) (Nuvoton-cbc-aes) encryption
tcrypt: test 0 (128 bit key, 16 byte blocks): 1 operation in 7930 cycles (16 bytes)
tcrypt: test 1 (128 bit key, 64 byte blocks): 1 operation in 6935 cycles (64 bytes)
tcrypt: test 2 (128 bit key, 128 byte blocks): 1 operation in 7241 cycles (128 bytes)
tcrypt: test 3 (128 bit key, 256 byte blocks): 1 operation in 8009 cycles (256 bytes)
tcrypt: test 4 (128 bit key, 1024 byte blocks): 1 operation in 12280 cycles (1024 bytes)
tcrypt: test 5 (128 bit key, 1424 byte blocks): 1 operation in 15350 cycles (1424 bytes)
tcrypt: test 6 (128 bit key, 4096 byte blocks): 1 operation in 35391 cycles (4096 bytes)
tcrypt: test 7 (192 bit key, 16 byte blocks): 1 operation in 6746 cycles (16 bytes)
tcrypt: test 8 (192 bit key, 64 byte blocks): 1 operation in 6953 cycles (64 bytes)
tcrypt: test 9 (192 bit key, 128 byte blocks): 1 operation in 7330 cycles (128 bytes)
tcrypt: test 10 (192 bit key, 256 byte blocks): 1 operation in 8084 cycles (256 bytes)
tcrypt: test 11 (192 bit key, 1024 byte blocks): 1 operation in 12670 cycles (1024 bytes)
tcrypt: test 12 (192 bit key, 1424 byte blocks): 1 operation in 15068 cycles (1424 bytes)
tcrypt: test 13 (192 bit key, 4096 byte blocks): 1 operation in 44312 cycles (4096 bytes)
tcrypt: test 14 (256 bit key, 16 byte blocks): 1 operation in 6702 cycles (16 bytes)
tcrypt: test 15 (256 bit key, 64 byte blocks): 1 operation in 6954 cycles (64 bytes)
tcrypt: test 16 (256 bit key, 128 byte blocks): 1 operation in 7315 cycles (128 bytes)
tcrypt: test 17 (256 bit key, 256 byte blocks): 1 operation in 8040 cycles (256 bytes)
tcrypt: test 18 (256 bit key, 1024 byte blocks): 1 operation in 12308 cycles (1024 bytes)
tcrypt: test 19 (256 bit key, 1424 byte blocks): 1 operation in 14426 cycles (1424 bytes)
tcrypt: test 20 (256 bit key, 4096 byte blocks): 1 operation in 35657 cycles (4096 bytes)
tcrypt:
testing speed of async cbc(aes) (Nuvoton-cbc-aes) decryption
tcrypt: test 0 (128 bit key, 16 byte blocks): 1 operation in 6765 cycles (16 bytes)
tcrypt: test 1 (128 bit key, 64 byte blocks): 1 operation in 6973 cycles (64 bytes)
tcrypt: test 2 (128 bit key, 128 byte blocks): 1 operation in 7297 cycles (128 bytes)
tcrypt: test 3 (128 bit key, 256 byte blocks): 1 operation in 8032 cycles (256 bytes)
tcrypt: test 4 (128 bit key, 1024 byte blocks): 1 operation in 12273 cycles (1024 bytes)
tcrypt: test 5 (128 bit key, 1424 byte blocks): 1 operation in 14414 cycles (1424 bytes)
tcrypt: test 6 (128 bit key, 4096 byte blocks): 1 operation in 35394 cycles (4096 bytes)
tcrypt: test 7 (192 bit key, 16 byte blocks): 1 operation in 6714 cycles (16 bytes)
tcrypt: test 8 (192 bit key, 64 byte blocks): 1 operation in 6961 cycles (64 bytes)
tcrypt: test 9 (192 bit key, 128 byte blocks): 1 operation in 7346 cycles (128 bytes)
tcrypt: test 10 (192 bit key, 256 byte blocks): 1 operation in 8136 cycles (256 bytes)
tcrypt: test 11 (192 bit key, 1024 byte blocks): 1 operation in 12661 cycles (1024 bytes)
tcrypt: test 12 (192 bit key, 1424 byte blocks): 1 operation in 16046 cycles (1424 bytes)
tcrypt: test 13 (192 bit key, 4096 byte blocks): 1 operation in 43601 cycles (4096 bytes)
tcrypt: test 14 (256 bit key, 16 byte blocks): 1 operation in 6740 cycles (16 bytes)
tcrypt: test 15 (256 bit key, 64 byte blocks): 1 operation in 6980 cycles (64 bytes)
tcrypt: test 16 (256 bit key, 128 byte blocks): 1 operation in 7322 cycles (128 bytes)
tcrypt: test 17 (256 bit key, 256 byte blocks): 1 operation in 8102 cycles (256 bytes)
tcrypt: test 18 (256 bit key, 1024 byte blocks): 1 operation in 12311 cycles (1024 bytes)
tcrypt: test 19 (256 bit key, 1424 byte blocks): 1 operation in 14503 cycles (1424 bytes)
tcrypt: test 20 (256 bit key, 4096 byte blocks): 1 operation in 35615 cycles (4096 bytes)
```

### U-boot test
```
mw.b 600000 11 20
mw.b 600020 22 10
mw.b 600030 33 10
mw.b 600040 00 10
aes_otp enc 0 600020 600030 600040 10
md.b 600040 10
mw.b 600050 00 10
aes_otp dec 0 600020 600040 600050 10
md.b 600050 10
```

## SHA


### Linux test
```
Please add to the kernel configuration:
CONFIG_CRYPTO_TEST=m
And copy tcrypt.ko to npcm845 EVB
test mode: 403(sha1), 404(sha256), 405(sha384), 406(sha512)

root@evb-npcm845:~# insmod ./tcrypt.ko mode=403

testing speed of async sha1 (nuvoton_sha)
tcrypt: test  0 (   16 byte blocks,   16 bytes per update,   1 updates):    782 cycles/operation,   48 cycles/byte
tcrypt: test  1 (   64 byte blocks,   16 bytes per update,   4 updates):   1855 cycles/operation,   28 cycles/byte
tcrypt: test  2 (   64 byte blocks,   64 bytes per update,   1 updates):   1422 cycles/operation,   22 cycles/byte
tcrypt: test  3 (  256 byte blocks,   16 bytes per update,  16 updates):   5309 cycles/operation,   20 cycles/byte
tcrypt: test  4 (  256 byte blocks,   64 bytes per update,   4 updates):   3796 cycles/operation,   14 cycles/byte
tcrypt: test  5 (  256 byte blocks,  256 bytes per update,   1 updates):   2567 cycles/operation,   10 cycles/byte
tcrypt: test  6 ( 1024 byte blocks,   16 bytes per update,  64 updates):  19218 cycles/operation,   18 cycles/byte
tcrypt: test  7 ( 1024 byte blocks,  256 bytes per update,   4 updates):   8368 cycles/operation,    8 cycles/byte
tcrypt: test  8 ( 1024 byte blocks, 1024 bytes per update,   1 updates):   7151 cycles/operation,    6 cycles/byte
tcrypt: test  9 ( 2048 byte blocks,   16 bytes per update, 128 updates):  37773 cycles/operation,   18 cycles/byte
tcrypt: test 10 ( 2048 byte blocks,  256 bytes per update,   8 updates):  16093 cycles/operation,    7 cycles/byte
tcrypt: test 11 ( 2048 byte blocks, 1024 bytes per update,   2 updates):  13659 cycles/operation,    6 cycles/byte
tcrypt: test 12 ( 2048 byte blocks, 2048 bytes per update,   1 updates):  13226 cycles/operation,    6 cycles/byte
tcrypt: test 13 ( 4096 byte blocks,   16 bytes per update, 256 updates):  76011 cycles/operation,   18 cycles/byte
tcrypt: test 14 ( 4096 byte blocks,  256 bytes per update,  16 updates):  31500 cycles/operation,    7 cycles/byte
tcrypt: test 15 ( 4096 byte blocks, 1024 bytes per update,   4 updates):  26636 cycles/operation,    6 cycles/byte
tcrypt: test 16 ( 4096 byte blocks, 4096 bytes per update,   1 updates):  25436 cycles/operation,    6 cycles/byte
tcrypt: test 17 ( 8192 byte blocks,   16 bytes per update, 512 updates): 275240 cycles/operation,   33 cycles/byte
tcrypt: test 18 ( 8192 byte blocks,  256 bytes per update,  32 updates):  62349 cycles/operation,    7 cycles/byte
tcrypt: test 19 ( 8192 byte blocks, 1024 bytes per update,   8 updates):  52646 cycles/operation,    6 cycles/byte
tcrypt: test 20 ( 8192 byte blocks, 4096 bytes per update,   2 updates):  50230 cycles/operation,    6 cycles/byte
tcrypt: test 21 ( 8192 byte blocks, 8192 bytes per update,   1 updates):  50190 cycles/operation,    6 cycles/byte

```

### U-boot test
```
hash sha256 600040 10
```

## RNG
### Linux test
```
dd if=/dev/hwrng of=./rng.img bs=1 count=8
```

### U-boot test
```
rng
```

## OTP

### U-boot test
```
fuse read 0 0 64
```

## PSPI

The EVB has one Peripheral SPI interface on J4 header.

- Physical connection

Connect an external spi flash to PSPI bus.
```
J4.29 (SPI CK)
J4.31 (SPI MOSI)
J4.33 (SPI MISO)
J4.36 (CS, GPIO15)
J4.1 (VCC_3.3V)
J4.2 (GND)
```

### Linux test
- dts setup of spi flash on PSPI bus
```
@ arch/arm64/boot/dts/nuvoton/nuvoton-npcm845-evb.dts
            spi1: spi@201000 {
                cs-gpios = <&gpio0 15 GPIO_ACTIVE_LOW>;
                status = "okay";
                Flash@0 {
                    compatible = "jedec,spi-nor";
                    reg = <0x0>;
                    #address-cells = <1>;
                    #size-cells = <1>;
                    spi-max-frequency = <1000000>;
                    partition@0 {
                        label = "spi1_spare0";
                        reg = <0x0 0x0>;
                    };
                };
            };
```
- set high slew rate for PSPI pins
```
@ arch/arm64/boot/dts/nuvoton/nuvoton-npcm845-evb.dts
   pinctrl: pinctrl@f0800000 {
        pinctrl-names = "default";
        pinctrl-0 = <
                &pin17_slew
                &pin18_slew
                &pin19_slew;
    };

@ arch/arm64/boot/dts/nuvoton/nuvoton-npcm845-pincfg-evb.dtsi
# add the followings to pinctrl configurations.

        pin17_slew: pin17_slew {
            pins = "GPIO17/PSPI_DI/CP1_GPIO5";
            slew-rate = <1>;
        };
        pin18_slew: pin18_slew {
            pins = "GPIO18/PSPI_D0/SMB4B_SDA";
            slew-rate = <1>;
        };
        pin19_slew: pin19_slew {
            pins = "GPIO19/PSPI_CK/SMB4B_SCL";
            slew-rate = <1>;
        };

```

- There will be one mtd device called 'spi1_spare0' after booting to Linux
```
root@evb-npcm845:~# cat /proc/mtd
dev:    size   erasesize  name
mtd8: 08000000 00001000 "spi1_spare0"
```

- use flashcp to test the spi flash (get the device node from /proc/mtd)
```
dd if=/dev/random of=/tmp/tmp.bin bs=32 count=1
flashcp /tmp/tmp.bin /dev/mtd8
```

### U-boot test
- dts
```
    pspi: pspi@f0201000 {
        status = "okay";
        #address-cells = <1>;
        #size-cells = <0>;
        spi-max-frequency = <1000000>;
        cs-gpios = <&gpio0 15 GPIO_ACTIVE_HIGH>;
    };

```

- use sspi command to read the JEDEC ID, it should return the correct ID (0xEF4018 in this example).
```
U-Boot>sspi 5:0@10000000 32 9f
FFEF4018
```

## EDAC

Arbel EVB has a memory controller that supports single bit error correction and double bit error detection.

### Linux Test

- Edit nuvoton-npcm845-evb.dts.
```
    mc: memory-controller@f0824000 {
        compatible = "nuvoton,npcm845-memory-controller";
        reg = <0x0 0xf0824000 0x0 0x1000>;
        interrupts = <GIC_SPI 25 IRQ_TYPE_LEVEL_HIGH>;
        status = "okay";
    };
```
- Enable Kernel config
```
CONFIG_EDAC=y
CONFIG_EDAC_SUPPORT=y
CONFIG_EDAC_NPCM=y
CONFIG_EDAC_DEBUG=y  // Support error injection
```

- Inject correctable error (CE) and uncorrectable error (UE)
```
/*
 * debugfs nodes under /sys/kernel/debug/edac/npcm-edac/
 *
 * error_type - 0: CE, 1: UE
 * location - 0: data, 1: checkcode
 * bit - 0 ~ 63 for data and 0 ~ 7 for checkcode
 * force_ecc_error - trigger
 */

 // Example 1: injecting CE at checkcode bit 7
 echo 0 > /sys/kernel/debug/edac/npcm-edac/error_type
 echo 1 > /sys/kernel/debug/edac/npcm-edac/location
 echo 7 > /sys/kernel/debug/edac/npcm-edac/bit
 echo 1 > /sys/kernel/debug/edac/npcm-edac/force_ecc_error

 // Example 2: injecting UE
 echo 1 > /sys/kernel/debug/edac/npcm-edac/error_type
 echo 1 > /sys/kernel/debug/edac/npcm-edac/force_ecc_error
```

## Host Serial Port

- To test the host serial port, please make sure that the espi/lpc between Arbel EVB and Host is properly connected.
- Please work with the BIOS team to check the host serial is from SP1 or SP2 or BOTH.

### U-Boot test

**Connect Host SP1 to SI2 on Arbel EVB**
 1. Make sure only strap5 of the SW_STRAP1_8 dip switch is turned off, then issue power on reset.
 2. Now the BMC logs will output from BSPTXD / BSPRXD
 3. Update the BMC regs to achieve the behavior
  ```r
  mw.l 0xf0800038 0x00000020  // set GCR bit 0-2 to 0 for UART mode 1
  mw.l 0xf0800260 0x00216a08  // set MFSEL1 bit 9 to 1 for selecting BSPTXD / BSPRXD
  mw.l 0xf080026C 0x19a00100  // set MFSEL4 bit 1 to 0 for selecting SI2_TXD / SI2_RXD
  ```
4. Power on the host, you will get host message from SI2_TXD / SI2_RXD

**Connect Host SP1 to SI1 on Arbel EVB**
 1. Make sure only strap5 of the SW_STRAP1_8 dip switch is turned on, then issue power on reset.
 2. Now the BMC logs will output from SI2_TXD / SI2_RXD
 3. Update the BMC regs to achieve the behavior
  ```r
  mw.l 0xf0800038 0x00000026  // set GCR bit 0-2 to 6 for UART mode 7
  mw.l 0xf0800268 0x00000e00 // set MFSEL3 bit 24 to 0 for selecting GPIO63/SI1_TXD/STRAP4, GPIO43/SI1_RXD
  mw.l 0xf0800260 0x00216408 // set MFSEL1 bit 10 to 1 for selecting SI1_TXD / SI1_RXD
  ```
4. Power on the host, you will get host message from SI1_TXD / SI2_RXD

## PECI

Arbel EVB provides a J_PECI header for validation.

- Flash an OpenBMC image which contains the **libpeci** package.
- Connect Arbel EVB to a PECI client device via the J_PECI header.

### Linux test

Some PECI 3.1 raw command examples are provided below.  
Output values might vary according to the client device.  
The client address under test is 0x30.

- Ping
  ```
  peci_cmds raw 0x30 0 0
  ```  
  > _If the client address is invalid (for example, 0x34), the following error message is returned._
  ```
  ERROR 3: command failed
  ```
- GetDIB
  ```
  root@evb-npcm845:~# peci_cmds raw 0x30 0x1 0x8 0xf7
   0x04 0x31 0x00 0x00 0x00 0x00 0x00 0x00
  ```
- GetTemp
  ```
  root@evb-npcm845:~# peci_cmds raw 0x30 0x1 0x2 0x01
   0x00 0x30
  ```
- RdPkgConfig
  ```
  root@evb-npcm845:~# peci_cmds raw 0x30 0x5 0x5 0xa1
   0x40 0x50 0x06 0x00 0x00
  ```
- RdIAMSR
  ```
  root@evb-npcm845:~# peci_cmds raw 0x30 0x5 0x9 0xb1
   0x40 0x65 0x7a 0xc4 0x3f 0x65 0x7a 0xc4 0x3f
  ```

## FLM

Arbel SOC provides four SPI flash monitoring(FLM).

- The FLM is connected in parallel to a flash interface, with only the chip-select in series connection. The flash controller is connected to the flash device via six signals and supports up to quad data bus.

- Arbel evb can enable FLM mode and test.
  ```
  SPI3.CS0 master via FM1 to XU_SPI3 & SPI3.CS1 master to XU_SPI1 (SPI1 Hi-Z or FM).
  ```


### uboot test

Select SW1.6 to on to enable FLM feature.

- Compare command only: using FLM_CMB
  ```
  mw f0800274 20000      //Set PinMux for FLM1: set MFSEL6.17
  mw f0211014 4000a901   //enable FLM1
  md a0000000            //incorrect data
  mw f0211014 a901       //disable FLM1
  mw F0211280 B          //Configure FLM_CMB0 , accept FastRead command
  mw f0211064 1          //enable CMB0
  mw f0211014 4000a901   //enable FLM1
  md a0000000            //correct data
  mw f0211064 0          //disable CMB0
  mw f0211014 4000a903   //apply the dynamic parameter change
  md a0000000            //incorrect data
  ```
- Compare command  and address: using FLM_CMD
  ```
  mw f0800274 20000      //Set PinMux for FLM1: set MFSEL6.17
  mw f0211014 4000a901   //enable FLM1
  md a0000000            //incorrect data
  mw f0211014 a901       //disable FLM1
  mw F0211080 0114000B   //Configure FLM_CMD0
  mw f0211060 1          //enable CMD0
  mw f0211014 4000a901   //enable FLM1
  md a0000000            //correct data
  md a0001000            //incorrect data
  mw f0211014 a901       //disable FLM1
  mw F0211084 0214000B   //configure FLM_CMD1
  mw f0211024 10001      //range: 1000~1fffh
  mw f0211060 3          //Set corresponding bit in FLM_CMDEN, enable CMD0 and CMD1
  mw f0211014 4000a901   //enable FLM1
  md a0000000            //correct data
  md a0001000            //correct data
  ```

# Troubleshooting

## Failed to probe SPI0 CS0 in u-boot

The original u-boot of the evb board may not correctly detect flash 0.

**Symptom**
```
U-Boot>sf probe 0:0 20000000 0
Failed to initialize SPI flash at 0:0 (error -22)
```

**Solution**

Find a image-u-boot in build/evb-npcm845/tmp/deploy/images/

### Use [IGPS](#igps) tool
1. BMC enter to FUP mode
2. Rename image-u-boot to Kmt_TipFw_BootBlock_uboot.bin.
3. Copy to igps/py_scripts\ImageGeneration\output_binaries\Secure
4. Run ProgramAll_Secure.bat

### Use [ISP](#isp) tool
1. Please follow the [ISP](#isp) section to update bootloader.

### Use a workable u-boot binary

1. Find a u-boot.bin in build/evb-npcm845/tmp/deploy/images/
2. load this u-boot.bin to 0x8000
```
U-Boot>setenv gatewayip            192.168.0.254
U-Boot>setenv serverip             192.168.0.128
U-Boot>setenv ipaddr               192.168.0.12
U-Boot>setenv netmask              255.255.255.0
U-Boot>setenv ethact gmac2
U-Boot>tftp 0x8000 u-boot.bin
DMAMAC_SRST wait
DMAMAC_SRST wait done
Speed: 1000, full duplex
Using gmac2 device
TFTP from server 192.168.0.128; our IP address is 192.168.0.12
Filename 'u-boot.bin'.
Load address: 0x8000
Loading: *#############################################
	 9.1 MiB/s
done
Bytes transferred = 658135 (a0ad7 hex)
```

3. BMC jump to 0x8000
```
U-Boot>go 0x8000
## Starting application at 0x00008000 ...

U-Boot 2021.04-25882-ge79478e0eb (Nov 30 2021 - 03:27:28 +0000)

CPU-0: NPCM845 Z1 @ Model: Nuvoton npcm845 EVB Development Board
Board: Nuvoton npcm845 EVB Development Board
DRAM:  848 MiB
OTP: NPCM OTP module bind OK
RNG: NPCM RNG module bind OK
AES: NPCM AES module bind OK
SHA: NPCM SHA module bind OK
SGMII PCS PHY reset wait 
SGMII PCS PHY reset done and clear Auto Negotiation 
NPCM845 EVB PCB version ID 0x2 -> version X01 
MMC:   
Loading Environment from SPIFlash... SF: Detected mx66l1g45g with page size 256 Bytes, erase size 4 KiB, total 128 MiB
OK
In:    serial0@f0000000
Out:   serial0@f0000000
Err:   serial0@f0000000
Net:   
Found phy_id=0x600d8595 addr=0x00 eth0: gmac1
Found phy_id=0x600d84a2 addr=0x00 , eth1: gmac2
Found phy_id=0x004061e4 addr=0x00 , eth3: gmac4
Hit any key to stop autoboot:  2  0 
U-Boot>
```

3. Programmming image-u-boot
```
U-Boot>setenv gatewayip            192.168.0.254
U-Boot>setenv serverip             192.168.0.128
U-Boot>setenv ipaddr               192.168.0.12
U-Boot>setenv netmask              255.255.255.0
U-Boot>setenv ethact gmac2
U-Boot>tftp 10000000 image-u-boot
DMAMAC_SRST wait
DMAMAC_SRST wait done
Speed: 1000, full duplex
Using gmac2 device
TFTP from server 192.168.0.128; our IP address is 192.168.0.12
Filename 'image-u-boot'.
Load address: 0x10000000
Loading: *######################################################
	 10.4 MiB/s
done
Bytes transferred = 786432 (c0000 hex)
U-Boot>sf probe 0:0
SF: Detected mx66l1g45g with page size 256 Bytes, erase size 4 KiB, total 128 MiB
U-Boot>sf update 0x10000000 0x0 ${filesize}
device 0 offset 0x0, size 0xc0000
   Updating, 29% 2261634 B/s   Updating, 58% 2239676 B/s   Updating, 79% 2016492 B/s4096 bytes written, 782336 bytes skipped in 0.392s, speed 2033601 B/s
U-Boot>
```

4. Issue power on reset

