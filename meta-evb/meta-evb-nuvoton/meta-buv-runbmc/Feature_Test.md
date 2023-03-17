# NPCM750 RunBMC BUV
This is the Nuvoton RunBMC BUV layer.
The NPCM750 is an ARM based SoC with external DDR RAM and
supports a large set of peripherals made by Nuvoton.
More information about the NPCM7XX can be found
[here](http://www.nuvoton.com/hq/products/cloud-computing/ibmc/?__locale=en).

- Working with [openbmc master branch](https://github.com/openbmc/openbmc/tree/master "openbmc master branch")

# Dependencies
![](https://cdn.rawgit.com/maxdog988/icons/61485d57/label_openbmc_ver_master.svg)

This layer depends on:

```
  URI: github.com/NNuvoton-Israel/openbmc.git
  branch: npcm-master
```

# Contacts for Patches

Please submit any patches against the meta-buv-runbmc layer to the maintainer of nuvoton:
* Brian Ma, <CHMA0@nuvoton.com>
* Jim Liu, <JJLIU0@nuvoton.com>
* Joseph Liu, <kwliu@nuvoton.com>

# Quick Setup
- [Quick Setup Guide](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/README.md)


# Rework for BUV Board
If you want to test secure boot, you need to rework the RunBMC card.
- Please look at [Rework Guide](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/REWORK.md)

# Table of Contents

- [Dependencies](#dependencies)
- [Contacts for Patches](#contacts-for-patches)
- [Quick Setup](#quick-setup)
- [Rework for BUV Board](#rework-for-buv-board)
- [Features of NPCM750 RunBMC BUV](#features-of-npcm750-runbmc-buv)
  * [WebUI](#webui)
    + [iKVM](#ikvm)
    + [Serial Over Lan](#serial-over-lan)
    + [Virtual Media](#virtual-media)
    + [BMC Firmware Update](#bmc-firmware-update)
  * [System](#system)
    + [Sensor](#sensor)
    + [LED](#led)
    + [BIOS POST Code](#bios-post-code)
    + [FRU](#fru)
    + [Fan PID Control](#fan-pid-control)
    + [NTP Server](#ntp-server)
    + [IPMI SOL](#ipmi-sol)
  * [JTAG Master](#jtag-master)
    + [CPLD Programming](#cpld-programming)
- [Image Size](#image-size)
- [Modifications](#modifications)

# Features of NPCM750 RunBMC BUV

## WebUI

### iKVM
<img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/ipkvm.PNG">

This is a Virtual Network Computing (VNC) server program using [LibVNCServer](https://github.com/LibVNC/libvncserver).
1. Support Video Capture and Differentiation(VCD), compares frame by hardware.
2. Support Encoding Compression Engine(ECE), 16-bit hextile compression hardware encoding.
3. Support USB HID, support Keyboard and Mouse.

**Source URL**

* [https://github.com/Nuvoton-Israel/obmc-ikvm](https://github.com/Nuvoton-Israel/obmc-ikvm)

**How to use**

1. Prepare a motherboard with a PCI-E slot at least.
2. Plug BUV board into motherboard with PCI-E connection.
3. Connect a micro usb cable from your workstation to J2009 header of BUV Board.
4. Launch a browser in your workstation and you will see the entry page.
    ```
    /* BMCWeb Server */
    https://<poelg ip>
    ```
5. Login to OpenBMC home page
    ```
    Username: root
    Password: 0penBmc
    ```
6. Navigate to OpenBMC WebUI viewer
    ```
    Server control -> KVM
    ```
**Performance**

* Host OS: Windows Server 2012 R2

|Playing video: [AQUAMAN](https://www.youtube.com/watch?v=2wcj6SrX4zw)|[Real VNC viewer](https://www.realvnc.com/en/connect/download/viewer/) | noVNC viewer
:-------------|:--------|:-----------|
Host Resolution    | FPS    | FPS |
1024x768  |  25    | 8 |
1280x1024   |  20  | 4 |
1600x1200   |  14   | 3 |

|Scrolling bar: [Demo video](https://drive.google.com/file/d/1H71_H6yjO8NU4Qu_ZL4F59FQ0PQmEo2n/view)|[Real VNC viewer](https://www.realvnc.com/en/connect/download/viewer/) | noVNC viewer
:-------------|:--------|:-----------|
Host Resolution    | FPS    | FPS |
1024x768  |  31    | 15 |
1280x1024   |  24  | 12 |
1600x1200   |  20   | 7 |

**The preferred settings of RealVNC Viewer**
```
Picture quality: Custom
ColorLevel: rgb565
PreferredEncoding: Hextile
```

**Maintainer**

* Jim Liu & Brian Ma

### Serial Over Lan
<img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/SOL.PNG">

The Serial over LAN (SoL) console redirects the output of the server’s serial port to a browser window on your workstation.

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/console](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/console)

**How to use**

1. Prepare a Supermicro MBD-X9SCL-F-0 motherboard and a LPC cable.

    > _The UEFI firmware version in Supermicro MBD-X9SCL-F-0 for verification is 2.15.1234._

2. Connect pins of the **JTPM** header on **Supermicro MBD-X9SCL-F-0** to the **J706** header on **RunBMC BUV** with the LPC cable:

3. Steps to copy UEFI SOL related drivers to a USB drive.  

    * Format the USB drive in FAT or FAT32.  
    * Download PolegSerialDxe.efi and TerminalDxe.efi from  [https://github.com/Nuvoton-Israel/openbmc-uefi-util/tree/npcm7xx_v2.1/sol_binary](https://github.com/Nuvoton-Israel/openbmc-uefi-util/tree/npcm7xx_v2.1/sol_binary) and copy them to the USB drive.

4. Power up the RunBMC BUV and steps to prepare a working terminal for Poleg:

    * Download and install the USB-to-UART driver from: [http://www.ftdichip.com/Drivers/VCP.htm](http://www.ftdichip.com/Drivers/VCP.htm) according to the host OS in your workstation.  
    * Connect a micro usb cable from your workstation to J2 header of RunBMC BUV.  
    * Wait for the FTDI driver to be installed automatically. The COM port number is assigned automatically.  
    * Open a terminal (e.g., Tera Term version 4.87) and set the correct COM port number assigned by the FTDI driver (in previous step).  
    * The COM port should be configured as follows: **115200, 8 bit, 1 stop-bit, no parity, no flow control**.  
    * Press and release the **BMC RESET** button to issue a BMC reset.  It's expected to see messages output by Poleg on the terminal. Use the following login name/password to login into Poleg.
        * Login name: **root**  
        * Login password: **0penBmc**  

6. Steps to configure Supermicro MBD-X9SCL-F-0 UEFI setting for SOL:

    * Do not plug any bootable device into Supermicro MBD-X9SCL-F-0.  
    * Power up Supermicro MBD-X9SCL-F-0 and boot into UEFI setting.  
    * Navigate to `Super IO Concifugration` in `Advanced` menu option and enter into `Super IO Concifugration`.  
    * Configure serial port 1 to **IO=3E8h; IRQ=5**, and then disable it.  
    * Go back to the main UEFI setting.  
    * Navigate to `Boot` menu option and select `UEFI: Built-in EFI Shell` as Boot Option #1.  

      + Make sure that the rest boot options are set to **Disabled**.  
    * Navigate to `Exit` menu option and select `Save changes and Reset`.  
    * Press `Yes` in the prompt window and it will reboot then.  
    * Wait for Supermicro MBD-X9SCL-F-0 to boot into UEFI shell.  
    * Plug the USB drive prepared in step-4 into Supermicro MBD-X9SCL-F-0's usb slot.  
    * Input the following command at UEFI shell prompt, press enter key and it will route to UEFI shell again.  
      ```
      exit  
      ```
    * Check the device mapping table of the USB drive in UEFI shell. It is **fs0:** here for example.  
    * Input the following command at UEFI shell prompt, press enter key and the prompt will show **fs0:\>** from now.  
      ```
      fs0:  
      ```
    * Input the following command at UEFI shell prompt and press the enter key.  
      ```
      load PolegSerialDxe.efi  
      ```
    * Input the following command at UEFI shell prompt and press the enter key.  
      ```
      load TerminalDxe.efi  
      ```
    * Unplug the usb drive.  
    * Input the following command at UEFI shell prompt, press the enter key and it will route to the UEFI setting. 
      ```
      exit  
      ```

6. Configure the ethernet communication between your workstation and RunBMC BUV:

    * Connect an ethernet cable between your workstation and RunBMC BUV.  
    * Configure your workstation's ip address to 192.168.0.1 and the netmask to 255.255.255.0 as an example here.  
    * Configure RunBMC BUV ip address to 192.168.0.2 and the netmask to 255.255.255.0. For example, input the following command in the terminal connected to RunBMC BUVon your workstation and press enter key.  
      ```
      ifconfig eth0 192.168.0.2 netmask 255.255.255.0
      ```

8. Run SOL:

    * Launch a browser in your workstation and navigate to https://${BMC_IP}
    * Bypass the secure warning and continue to the website.
    * Enter the BMC Username and Password (defaults: **root/0penBmc**).
    * You will see the OpenBMC management screen.
    * Click `Server control` at the left side of the OpenBMC management screen.
    * A `Serial over LAN console` menu item prompts then and click it.
    * Power on host server.
    * A specific area will display the host ttyS1 that user can operate host OS.

**Maintainer**

* Jim Liu & Brian Ma

### Virtual Media
<img align="right" width="20%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/3e65e7a/openbmc/vm_app_win.png">
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/cab7306/openbmc/vm.png">

Virtual Media (VM) is to emulate an USB drive on remote host PC via Network Block Device(NBD) and Mass Storage(MSTG).

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-phosphor/nuvoton-layer/recipes-connectivity/jsnbd](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-phosphor/nuvoton-layer/recipes-connectivity/jsnbd)
* [https://github.com/Nuvoton-Israel/openbmc-util/tree/master/virtual_media_openbmc2.6](https://github.com/Nuvoton-Israel/openbmc-util/tree/master/virtual_media_openbmc2.6)

**How to use**

1. Clone a physical USB drive to an image file
    * For Linux - use tool like **dd**
      ```
      dd if=/dev/sda of=usb.img bs=1M count=100
      ```
      > _**bs** here is block size and **count** is block count._
      >
      > _For example, if the size of your USB drive is 1GB, then you could set "bs=1M" and "count=1024"_

    * For Windows - use tool like **Win32DiskImager.exe**

      > _NOTICE : A simple *.iso file cannot work for this._

    * You can prepare an ISO file instead

2. Enable Virtual Media

    2.1 VM-WEB
    1. Login and switch to webpage of VM on your browser
        ```
        https://XXX.XXX.XXX.XXX/#/server-control/virtual-media
        ```
    2. Operations of Virtual Media
        * After `Choose File`, click `Start` to start VM network service
        * After clicking `Start`, you will see a new USB device on HOST OS
        * If you want to stop this service, just click `Stop` to stop VM network service.

    2.2 VM standalone application
    * Download [application source code](https://github.com/Nuvoton-Israel/openbmc-util/tree/master/virtual_media_openbmc2.6)
    * Follow the [readme](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/virtual_media_openbmc2.6/NBDServerWSWindows/README) instructions install QT and Openssl
    * Start QT creator, open project **VirtualMedia.pro**, then build all
    * Launch windows/linux application
        
        > _NOTICE : use `sudo` to launch app in linux and install `nmap` first_
    *  Operations
        + After `Chose an Image File` or `Select an USB Drive`, click `Search` to check which BMCs are on line
        + Select any on line BMC and key in `Account/Password`, choose the `Export Type` to Image, and click `Start VM` to start VM network service (still not hook USB disk to host platform)
        + After `Start VM`, click `Mount USB` to hook the emulated USB disk to host platform, or click `Stop VM` to stop VM network service.
        + After `Mount USB`, click `UnMount USB` to emulate unplugging the USB disk from host platform
        + After `UnMount USB`, click `Stop VM` to stop VM network service, or click `Mount USB` to hook USB disk to host platform.

3. Performance

|Client |MountType |Encryption |Speed KB/s
:---------------------|:----------------------------------------------|:---|:---------|
Client WebUI(HTML5)  | CentOS-8.2.2004-x86_64-dvd1.iso (8GB Size)    |Yes| ~7800KB/s |

**Maintainer**
* Jim Liu & Brian Ma

### BMC Firmware Update
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/cab7306/openbmc/firmware-update.png">

This is a secure flash update mechanism to update BMC firmware via WebUI.

**Source URL**

* [https://github.com/Nuvoton-Israel/phosphor-bmc-code-mgmt](https://github.com/Nuvoton-Israel/phosphor-bmc-code-mgmt)

**How to use**

* Update firmware via WebUI
    1. Upload update package from web UI, then you will see
        ```
        Activate
        ```
        > If you select activate, then you will see activation dialog at item 2

        ```
        Delete
        ```
        > If you select delete, then the package will be deleted right now

    2. Confirm BMC firmware file activation
        ```
        ACTIVATE FIRMWARE FILE WITHOUT REBOOTING BMC
        ```
        > If you select this, you need to reboot BMC manually, and shutdown application will run update script to flash image into SPI flash

        ```
        ACTIVATE FIRMWARE FILE AND AUTOMATICALLY REBOOT BMC
        ```
        > if you select this, BMC will shutdown right now, and shutdown application will run update script to flash image into SPI flash

* Update firmware via Redfish

    We can update BMC firmware via REST API provided by Redfish. The firmware will apply immediately after uploaded without any confirmation by default.
    The following command shows how to using curl command upload BMC firmware.
    ```
    curl -X POST -H "x-auth-token: ${token}" --data-binary obmc-phosphor-image-buv-runbmc-20200814010351.static.mtd.tar https://${BMC_IP}/redfish/v1/UpdateService
    ```
    >_${token} is the token value come from login API, read more information from [REST README](https://github.com/openbmc/docs/blob/master/REST-cheatsheet.md)_

**Maintainer**

* Jim Liu & Brian Ma


## System

### Sensor
[phosphor-hwmon](https://github.com/openbmc/phosphor-hwmon) daemon will periodically check the sensor reading to see if it exceeds lower bound or upper bound . If alarm condition is hit, the [phosphor-sel-logger](https://github.com/openbmc/phosphor-sel-logger) handles all sensor events to add new IPMI SEL records to the journal, [phosphor-host-ipmid](https://github.com/Nuvoton-Israel/phosphor-host-ipmid) will convert the journal SEL records to IPMI SEL record format and reply to host.

**Source URL**
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/configuration](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/configuration)
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/ipmi](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/ipmi)
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/sensors](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/sensors)


**How to use**

* **Configure sensor**
  
  * Add Sensor Configuration File
  
    Each sensor **temperature**, **adc**, **fan**, **peci** and **power** has a [hwmon config file](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/sensors/phosphor-hwmon/obmc/hwmon/ahb/apb) and [ipmi sdr config file](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/configuration/buv-runbmc-yaml-config/buv-runbmc-ipmi-sensors.yaml) that defines the sensor name and its warning or critical thresholds.  

    Below is hwmon config for a LM75 sensor on BMC. The sensor type is **temperature** and its name is **bmc_card**. It has warning and critical thresholds for **upper** and **lower** bound.
      ```
      LABEL_temp1=BMC_Temp
      WARNLO_temp1=10000
      WARNHI_temp1=40000
      CRITHI_temp1=70000
      CRITLO_temp1=0
      EVENT_temp1=WARNHI,WARNLO
      ```
    Below is ipmi sdr config for a temperature sensor on BMC.
      ```
        1: &temperature
        entityID: 0x07
        entityInstance: 1
        sensorType: 0x01
        path: /xyz/openbmc_project/sensors/temperature/BMC_Temp
        sensorReadingType: 0x01
        multiplierM: 1
        offsetB: 0
        bExp: 0
        rExp: 0
        unit: xyz.openbmc_project.Sensor.Value.Unit.DegreesC
        mutability: Mutability::Write|Mutability::Read
        serviceInterface: org.freedesktop.DBus.Properties
        readingType: readingData
        sensorNamePattern: nameLeaf
        interfaces:
            xyz.openbmc_project.Sensor.Value:
            Value:
                Offsets:
                0xFF:
                    type: double
      ```

* **Monitor sensor and events**

  * Using WebUI  

    In `Sensors` page of **WebUI**, the sensors reading will show as below.

    <img align="bottomleft" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/sensor_reading.PNG">  


    In `System log` page of **WebUI**, the sensors event will show as below.
    
    <img align="bottomleft" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/sensor_event.PNG">  


  * Using IPMI

    Use IPMI utilities like **ipmitool** to send command for getting SDR records.  
    ```
    root@buv-runbmc:~# ipmitool sdr elist
    BMC_Temp         | 01h | ok  |  7.1 | 35 degrees C
    BUV_Temp      | 02h | ok  |  7.2 | 22 degrees C
    ADC1             | 03h | ok  |  7.3 | 1.92 Volts
    ADC2             | 04h | ok  |  7.4 | 1.68 Volts
    ADC3             | 05h | ok  |  7.5 | 1.12 Volts
    ADC4             | 06h | ok  |  7.6 | 0.80 Volts
    ADC5             | 07h | ok  |  7.7 | 1.76 Volts
    ADC6             | 08h | ok  |  7.8 | 1.04 Volts
    ADC7             | 09h | ok  |  7.9 | 0.88 Volts
    ADC8             | 0Ah | ok  |  7.10 | 1.12 Volts
    MB_P12V_INA219_O | 0Bh | ok  |  7.11 | 0.16 Volts
    MB_P3V3_INA219_O | 0Ch | ok  |  7.12 | 3.36 Volts
    MB_P12V_INA219_O | 0Dh | ok  |  7.13 | 0.01 Amps
    MB_P3V3_INA219_O | 0Eh | ok  |  7.14 | 0.13 Amps
    MB_P12V_INA219_O | 0Fh | ok  |  7.15 | 0 Watts
    MB_P12V_INA219_O | 10h | ok  |  7.16 | 0 Watts
    Fan1             | 11h | ok  | 29.1 | 0 RPM
    Fan2             | 12h | ok  | 29.2 | 0 RPM
    Fan3             | 13h | ok  | 29.3 | 0 RPM
    Fan4             | 14h | ok  | 29.4 | 0 RPM
    ```
    Use IPMI utilities like **ipmitool** to send command for getting SEL records.  
    ```
    ipmitool sel list
    1 |  Pre-Init  |0000000049| Voltage #0x14 | Upper Critical going high | Asserted
    ```

**Maintainer**

* Jim Liu & Brian Ma

### LED
BUV have eight free led can control by customer

Led gpio range is from 488 to 495

**How to use**
* use export command to turn on/off the led
  ```
  echo 488 > /sys/class/gpio/export
  echo out > /sys/class/gpio/gpio488/direction
  echo 0 > /sys/class/gpio/gpio488/value

  echo 489 > /sys/class/gpio/export
  echo out > /sys/class/gpio/gpio489/direction
  echo 0 > /sys/class/gpio/gpio489/value
  ```

**Maintainer**

* Jim Liu & Brian Ma

### BIOS POST Code
In NPCM750, we support a FIFO for monitoring BIOS POST Code. Typically, this feature is used by the BMC to "watch" host boot progress via port 0x80 writes made by the BIOS during the boot process.

**Source URL**

This is a patch for enabling BIOS POST Code feature in [phosphor-host-postd](https://github.com/openbmc/phosphor-host-postd) on Nuvoton's NPCM750.

* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/host/phosphor-host-postd_%25.bbappend](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/host/phosphor-host-postd_%25.bbappend)
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-kernel/linux/linux-nuvoton/0004-add-seven_seg_gpio.patch](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-kernel/linux/linux-nuvoton/0004-add-seven_seg_gpio.patch)
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-kernel/linux/linux-nuvoton/0005-misc-Character-device-driver.patch](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-kernel/linux/linux-nuvoton/0005-misc-Character-device-driver.patch)

**How to use**

* Modify DTS settings of LED for BUV.
  For example about DTS **nuvoton-npcm750-buv-runbmc.dts**:
  ```
  seven-seg-gpio {
	compatible = "seven-seg-gpio-dev";
	refresh-interval-ms = <500>;
	seven-gpios = <&gpio4 14 0>,
				<&gpio4 12 0>,
				<&gpio7 7 0>,
				<&gpio0 9 0>,
				<&gpio4 15 0>,
				<&gpio2 25 0>,
				<&gpio4 11 0>,
				<&gpio4 10 0>,
				<&gpio0 25 0>,
				<&gpio4 9 0>,
				<&gpio0 24 0>,
				<&gpio0 11 0>,
				<&gpio2 23 0>,
				<&gpio4 13 0>,
				<&gpio2 24 0>,
				<&gpio2 26 0>;
  };
  ```

* Server Power on

  Press the host `Power on` button after bmc boot up

* Seven segment display daemon to show  BIOS POST Code

  The Seven segment display daemon can show the POST Code number to  Seven segment display

**Maintainer**
* Jim Liu & Brian Ma

### FRU
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/cab7306/openbmc/fru.png">

Field Replaceable Unit. The FRU Information is used to primarily to provide “inventory” information about the boards that the FRU Information Device is located on. In NPCM750, we connect EEPROM component as FRU Information Device to support this feature. Typically, this feature is used by the BMC to "monitor" host server health about H/W component's status.

**Source URL**

This is a patch for enabling FRU feature in [phosphor-impi-fru](https://github.com/openbmc/ipmi-fru-parser) on Nuvoton's NPCM750.

* [https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/ipmi/phosphor-ipmi-fru_%25.bbappend](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/ipmi/phosphor-ipmi-fru_%25.bbappend)

**How to use**
* Modify DTS settings of EEPROM for FRU.
  For example about DTS **nuvoton-npcm750-buv-runbmc.dts**:
  ```
  i2c13: i2c@8d000 {
         #address-cells = <1>;
         #size-cells = <0>;
         bus-frequency = <100000>;
         status = "okay";
         m24128_fru@51 {
         	compatible = "atmel,24c128";
        	 reg = <0x51>;
        	 pagesize = <64>;
        	 status = "okay";
        	 };
         };
  ```

  According DTS modification, you also need to remember modify your EEPROM file description content about **SYSFS_PATH** and **FRUID**. Below is example for our EEPROM file description **[BMC](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/ipmi/phosphor-ipmi-fru/obmc/eeproms/system/chassis/bmc)**:
  ```
  SYSFS_PATH=/sys/bus/i2c/devices/13-0051/eeprom
  FRUID=1
  ```
  **SYSFS_PATH** is the path according your DTS setting and **FRUID** is arbitrary number but need to match **Fruid** in **[config.yaml](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/configuration/buv-runbmc-yaml-config/buv-runbmc-ipmi-fru.yaml)** file. Below is example for when **Fruid** set as 1:
  ```
    1:
    /system/chassis/motherboard/management_card/bmc:
        entityID: 7
        entityInstance: 1
        interfaces:
        xyz.openbmc_project.Inventory.Decorator.Asset:
            BuildDate:
            IPMIFruProperty: Mfg Date
            IPMIFruSection: Board
            SerialNumber:
            IPMIFruProperty: Serial Number
            IPMIFruSection: Board
            PartNumber:
            IPMIFruProperty: Part Number
            IPMIFruSection: Board
            Manufacturer:
            IPMIFruProperty: Manufacturer
            IPMIFruSection: Board
        xyz.openbmc_project.Inventory.Item:
            PrettyName:
            IPMIFruProperty: Name
            IPMIFruSection: Board
        xyz.openbmc_project.Inventory.Decorator.Revision:
            Version:
            IPMIFruProperty: FRU File ID
            IPMIFruSection: Board
        xyz.openbmc_project.Common.UUID:
            UUID:
            IPMIFruProperty: Custom Field 1
            IPMIFruSection: Board
            IPMIFruValueDelimiter: 58
        xyz.openbmc_project.Inventory.Item.Board:
        xyz.openbmc_project.Inventory.Item.Bmc:
        xyz.openbmc_project.Inventory.Item.NetworkInterface:
            MACAddress:
            IPMIFruProperty: Custom Field 2
            IPMIFruSection: Board
        xyz.openbmc_project.Inventory.Item.Ethernet:

  ```

* Server health

  Select `Server health` -> `Hardware status` on **Web-UI** will show FRU Board Info/Chassis Info/Product Info area.

**Maintainer**
* Jim Liu & Brian Ma

### Fan PID Control
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/e12e9dd/openbmc/fan_stepwise_pwm.png">
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/8fc19a1/openbmc/fan_rpms.png">

In NPCM750, we have two PWM modules and support eight PWM signals to control fans for dynamic adjustment according temperature variation.

**Source URL**

* [https://github.com/openbmc/phosphor-pid-control](https://github.com/openbmc/phosphor-pid-control)
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/fans/phosphor-pid-control](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/fans/phosphor-pid-control)

**How to use**

In order to automatically apply accurate and responsive correction to a fan control function, we use the `swampd` to handle output PWM signal. For enable this daemon, basically we need configuring the swampd configuration file and deploy a system service for start this daemon as below steps.

>_NOTICE: In current solution, we only use stepwise mechanism to control fans. But the swampd also can controls fan with PID by tuning parameters according to customer platform._

* The swampd(PID control daemon) is a Margin-based daemon running within the OpenBMC environment. It uses a well-defined [configuration file](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/fans/phosphor-pid-control/config-buv-nuvoton.json) to control the temperature of the tray components to keep them within operating conditions.

    Here is a configuration example shows how to using one PWM control more than one fans on system.
    ```
    "sensors" : [
         {
            "name": "Fan1",
            "type": "fan",
            "readPath": "/xyz/openbmc_project/sensors/fan_tach/Fan1",
            "writePath": "/sys/devices/platform/ahb/ahb:apb/f0103000.pwm-fan-controller/hwmon/**/pwm1",
            "min": 0,
            "max": 255
        },
        {
            "name": "Fan2",
            "type": "fan",
            "readPath": "/xyz/openbmc_project/sensors/fan_tach/Fan2",
            "writePath": "/sys/devices/platform/ahb/ahb:apb/f0103000.pwm-fan-controller/hwmon/**/pwm2",
            "min": 0,
            "max": 255
        },
        {
            "name": "BMC_Temp",
            "type": "temp",
            "readPath": "/xyz/openbmc_project/sensors/temperature/BMC_Temp",
            "writePath": "",
            "min": 0,
            "max": 0,
            "timeout": 0
        },
    ],
   "zones" : [
        {
            "id": 0,
            "minThermalOutput": 0.0,
            "failsafePercent": 100.0,
            "pids": [
                {
                    "name": "Fan1",
                    "type": "fan",
                    "inputs": ["Fan1", "Fan2"],
                    "setpoint": 40.0,
                    "pid": {
                        "samplePeriod": 1.0,
                        "proportionalCoeff": 0.0,
                        "integralCoeff": 0.0,
                        "feedFwdOffsetCoeff": 0.0,
                        "feedFwdGainCoeff": 1.0,
                        "integralLimit_min": 0.0,
                        "integralLimit_max": 0.0,
                        "outLim_min": 3.0,
                        "outLim_max": 100.0,
                        "slewNeg": 0.0,
                        "slewPos": 0.0
                    }
                },
                {
                    "name": "BMC_Temp",
                    "type": "stepwise",
                    "inputs": ["BMC_Temp"],
                    "setpoint": 30.0,
                    "pid": {
                        "samplePeriod": 1.0,
                        "positiveHysteresis": 0.0,
                        "negativeHysteresis": 0.0,
                        "isCeiling": false,
                        "reading": {
                            "0": 25,
                            "1": 28,
                            "2": 31,
                            "3": 34,
                            "4": 37,
                            "5": 40,
                            "6": 43,
                            "7": 46,
                            "8": 49,
                            "9": 52,
                            "10": 55,
                            "11": 58,
                            "12": 61,
                            "13": 64,
                            "14": 67,
                            "15": 70
                        },
                        "output": {
                            "0": 10,
                            "1": 10,
                            "2": 20,
                            "3": 20,
                            "4": 20,
                            "5": 30,
                            "6": 30,
                            "7": 30,
                            "8": 40,
                            "9": 50,
                            "10": 60,
                            "11": 70,
                            "12": 80,
                            "13": 90,
                            "14": 100,
                            "15": 100
                        }
                    }
                }
            ]
        },
    ]
    ```
    The [PID README](https://github.com/openbmc/phosphor-pid-control/blob/master/configure.md) provide more detail about the meaning for each parameter.

    Roughly speaking, there are two main section in configuration file: **sensor** and **zones**.
    **Sensors** defined the all component which are involved PID like temperature sensors, or fans.
    **Zones** defined the mechanism how the swampd control each fans by setting parameters.

    The most important in **sensors** section is the settings of `readPath` and `writePath`.
    Sensors like temperature sensor or margin sensor must only set readPath, and fill up empty string to writePath.
    Fans could set the D-Bus path to readPath, also set the pwm system path to writePath.
    More detail about readPath and writePath please refer README that mentioned above.

    There are four PID controller types user can use: `fan`, `temp`, `margin`, and `stepwise` in **zones**.
    User can tune the PID parameters following the [tuning README](https://github.com/openbmc/phosphor-pid-control/blob/master/tuning.md). 

    In above example case, the fan PID controller has a lot of PID parameters. And we only use the stepwise controller to control whole zone, so the PID parameters in fan controller like `integralCoeff` or `outLim_max` would not work.
    And the parameter `inputs` for stepwise controller must be thermal sensor.
    Please note the parameter `setpoint` is no meaning for type `fan` and `stepwise` currently, and cannot be left out.

    If user want to control whole zone by stepwise controller like example configuration, the key point is set reading and output.
    The `stepwise` PID use the mapping of reading and output value instead of set RPM setpoint.
    The reading and output value should be a pair, and user can set 20 pairs in maximum, one more pairs at least.
    And the `stepwise` will return output setpoint if temperature **is larger** than reading value.
    For example, assume here are pairs of `stepwise` reading and output:
    ```
    {
      "reading": {25, 26, 27},
      "output": {10, 20, 30}
    }
    ```

    If the temperature reading is 25.5°C, the return value will be 10.
    And if the reading value is 26.5°C, the stepwise controller will set 20% RPM to fan(s).


* OpenBMC will run swampd through [phosphor-pid-control.service](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/fans/phosphor-pid-control/phosphor-pid-control.service) that controls the fans by pre-defined zones. Here is a example service.
    ```
    [Service]
    Type=simple
    ExecStart=/usr/bin/swampd
    Restart=always
    RestartSec=5
    StartLimitInterval=0
    ExecStopPost=/usr/bin/fan-default-speed.sh
    ```
    + **ExecStopPost** that means an additional commands that are executed after the service is stopped.


**Maintainer**
* Jim Liu & Brian Ma

### NTP Server
  Network Time Protocol (NTP) is a networking protocol for clock synchronization between computer systems over packet-switched, variable-latency data networks.

  **systemd-timesyncd** is a daemon that has been added for synchronizing the system clock across the network. It implements an **SNTP (Simple NTP)** client. This daemon runs with minimal privileges, and has been hooked up with **systemd-networkd** to only operate when network connectivity is available.
        
  The modification time of the file **/var/lib/systemd/timesync/clock** indicates the timestamp of the last successful synchronization (or at least the **systemd build date**, in case synchronization was not possible).
    
  **Source URL**
  * [https://github.com/openbmc/phosphor-time-manager](https://github.com/openbmc/phosphor-time-manager)
  * [https://github.com/systemd/systemd/tree/master/src/timesync](https://github.com/systemd/systemd/tree/master/src/timesync)
    

  **How to use**
  <img align="right" width="30%" src="https://raw.githubusercontent.com/NTC-CCBG/snapshots/master/openbmc/Time.PNG">
  * Enable NTP by **Web-UI** `Server configuration`
      ->`Date and time settings`
  * Enable NTP by command
      ```
      timedatectl set-ntp true  
      ```
      >_timedatectl result will show **systemd-timesyncd.service active: yes**_ 
    
      If NTP is Enabled and network is Connected (Using **eth1** connect to router), we will see the item **systemd-timesyncd.service active** is **yes** and **System clock synchronized** is **yes**. Thus, system time will sync from NTP server to get current time.
  * Get NTP status  
      ```
      timedatectl  
      ```
      >_Local time: Mon 2018-08-27 09:24:51 UTC  
      >Universal time: Mon 2018-08-27 09:24:51 UTC  
      >RTC time: n/a  
      >Time zone: n/a (UTC, +0000)  
      >**System clock synchronized: yes**  
      >**systemd-timesyncd.service active: yes**  
      >RTC in local TZ: no_  
      
  * Disable NTP  
      ```
      timedatectl set-ntp false  
      ```
      >_timedatectl result will show **systemd-timesyncd.service active: no**_  
      
  * Using Local NTP server Configuration  
    When starting, systemd-timesyncd will read the configuration file from **/etc/systemd/timesyncd.conf**, which looks like as below: 
        >_[Time]  
        >\#NTP=  
        >\#FallbackNTP=time1.google.com time2.google.com time3.google.com time4.google.com_  
    
    	By default, systemd-timesyncd uses the Google Public NTP servers **time[1-4].google.com**, if no other NTP configuration is available. To add time servers or change the provided ones, **uncomment** the relevant line and list their host name or IP separated by a space. For example, we setup NB windows 10 system as NTP server with IP **192.168.1.128**  
    	>_[Time]  
    	>**NTP=192.168.1.128**  
    	>\#FallbackNTP=time1.google.com time2.google.com time3.google.com time4.google.com_
    
  * BMC connect to local NTP server of windows 10 system  
      Connect to NB through **eth1** EMAC interface, and set static IP **192.168.1.15**  
        
      ```
      ifconfig eth1 up
      ifconfig eth1 192.168.1.15
      ```
      >_Note: Before that you need to setup your NTP server (192.168.1.128) on Windows 10 system first_  
      
      Modify **/etc/systemd/timesyncd.conf** file on BMC as we mentioned
        >_[Time]  
        >**NTP=192.168.1.128**_  
      
      Re-start NTP to make effect about our configuration change  
      ```
      systemctl restart systemd-timesyncd.service
      ```
      Check status of NTP that show already synced to our local time server 
      ```
      systemctl status systemd-timesyncd.service -l --no-pager
      ```
      >_Status: "Synchronized to time server 192.168.1.128:123 (192.168.1.128)."_  
      
      Verify **Web-UI** `Server overview`->`BMC time` whether sync from NTP server as same as **timedatectl**. (Note: timedatectl time zone default is UTC, thus you will find the BMC time is UTC+8)
      ```
      timedatectl  
      ```
      >_Local time: Thu 2018-09-06 07:24:16 UTC  
      >Universal time: Thu 2018-09-06 07:24:16 UTC  
      >RTC time: n/a  
      >Time zone: n/a (UTC, +0000)  
      >System clock synchronized: yes  
      >systemd-timesyncd.service active: yes  
      >RTC in local TZ: no_

  * TimeZone  
      According OpenBMC current design that only support UTC TimeZone now, we can use below command to get current support TimeZone on BMC
      ```
      timedatectl list-timezones
      ```
  * Maintainer  
    
    * Jim Liu & Brian Ma

### IPMI SOL
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/8afa8a2/openbmc/sol_ipmi_win10.PNG">

The Serial over LAN (SoL) via IPMI redirects the output of the server’s serial port to a command/terminal window on your workstation.

The user uses the ipmi tool like [ipmiutil](http://ipmiutil.sourceforge.net/) to interact with SOL via IPMI. Here the ipmiutil is used as an example.

The patch integrates [phosphor-net-ipmid](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/ipmi/phosphor-ipmi-net_%25.bbappend) into Nuvoton's NPCM750 solution for OpenBMC.

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/ipmi/phosphor-ipmi-net_%25.bbappend](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/ipmi/phosphor-ipmi-net_%25.bbappend)

**How to use**

1. Download the [ipmiutil](http://ipmiutil.sourceforge.net/) according to the host OS in your workstation.

   > _Here it's assumed that the host OS is Windows 10 and ipmiutil for Windows is downloaded and used accordingly._

2. Run SOL:

    * Extract or install the ipmiutil package to a folder in your workstation in advance.
    * Launch a command window and navigate to that folder.
    * Input the following command in the command window.
      ```
      ipmiutil sol -N 192.168.0.2 -U root -P 0penBmc -J 3 -V 4 -a
      ```
    * (Optional) Configure the `Properties` of the command window to see the entire output of SOL.
      > _Screen Buffer Size Width: 200_
        _Screen Buffer Size Height: 400_
        _Window Size Width: 100_
        _Window Size Height: 40_

4. End SOL session:

    * Press the "~" key (using the shift key + "`" key) and "." key at the same time in the command window.
    * Input the following command in the command window.
      ```
      ipmiutil sol -N 192.168.0.2 -U root -P 0penBmc -J 3 -V 4 -d
      ```

**Maintainer**

* Jim Liu & Brian Ma


## JTAG Master
JTAG master is implemented on BMC to facilitate programming CPLD device.  

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc-util/tree/master/loadsvf](https://github.com/Nuvoton-Israel/openbmc-util/tree/master/loadsvf)

### CPLD Programming
BMC can load svf file to program CPLD via JTAG.  

BUV can use J705 pin5 , pin7 , pin9 , pin13 to connect CPLD.


**How to use**

run loadsvf on RunBMC to program CPLD. Specify the svf file name with -s.
```
loadsvf -d /dev/jtag0 -s firmware.svf
```

**Maintainer**
* Jim Liu & Brian Ma

# Image Size
Type          | Size    | Note                                                                                                     |
:-------------|:------- |:-------------------------------------------------------------------------------------------------------- |
image-uboot   |  540 KB | u-boot 2019.01 + bootblock for NPCM750 only                                                                       |
image-kernel  |  4.4 MB   | linux 5.4.16 version                                                                                       |
image-rofs    |  23.0 MB  | bottom layer of the overlayfs, read only                                                                 |
image-rwfs    |  0 MB  | middle layer of the overlayfs, rw files in this partition will be created at runtime,<br /> with a maximum capacity of 3 MB|

# Modifications

* 2020.08.19 First release ReadME.md
