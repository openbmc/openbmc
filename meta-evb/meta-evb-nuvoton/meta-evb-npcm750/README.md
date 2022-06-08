# Nuvoton NPCM750 Evaluation Board
This is the Nuvoton NPCM750 evaluation board layer.
The NPCM750 is an ARM based SoC with external DDR RAM and
supports a large set of peripherals made by Nuvoton.
More information about the NPCM7XX can be found
[here](http://www.nuvoton.com/hq/products/cloud-computing/ibmc/?__locale=en).

# Dependencies
This layer depends on:

```
  URI: git@github.com:Nuvoton-Israel/openbmc
  branch: npcm-master
```

# Contacts for Patches

Please submit any patches against the NPCM750 evaluation board layer to the maintainers of nuvoton:

* Marvin Lin, <KFLIN@nuvoton.com>
* Joseph Liu, <KWLIU@nuvoton.com>

# Table of Contents

- [Dependencies](#dependencies)
- [Contacts for Patches](#contacts-for-patches)
- [Features of NPCM750 Evaluation Board](#features-of-npcm750-evaluation-board)
  * [WebUI](#webui)
    + [KVM](#kvm)
    + [Serial Over Lan](#serial-over-lan)
    + [Virtual Media](#virtual-media)
    + [BMC Firmware Update](#bmc-firmware-update)
  * [System](#system)
    + [Sensor](#sensor)
    + [LED](#led)
    + [ADC](#adc)
    + [Fan PID Control](#fan-pid-control)
    + [BIOS POST Code](#bios-post-code)
    + [FRU](#fru)
    + [IPMI SOL](#ipmi-sol)
- [Image Size](#image-size)

# Features of NPCM750 Evaluation Board

## WebUI

### KVM
<img align="right" width="30%" src="https://user-images.githubusercontent.com/81551963/171312575-5a4f15b7-393b-4177-aab0-e87de539923b.png">

This is a Virtual Network Computing (VNC) server programm using [LibVNCServer](https://github.com/LibVNC/libvncserver).
* Support Video Capture and Differentiation (VCD).
* Support 16-bit Hextile Encoding Compression Engine (ECE).
* Support USB HID (Keyboard and Mouse).

**Source URL**

* [https://github.com/Nuvoton-Israel/obmc-ikvm/tree/upstream-v4l2](https://github.com/Nuvoton-Israel/obmc-ikvm/tree/upstream-v4l2)

**How to use**

1. Prepare a motherboard and connect NPCM750 EVB through PCI-E.
2. Connect a USB cable from motherboard to **J1 header** of NPCM750 EVB.
3. Connect an ethernet cable between your workstation and **J12 header** of NPCM750 EVB.
4. Power up the NPCM750 EVB and configure IP address.
5. Launch a browser in your workstation and enter below URL.
    ```
    https://<NPCM750_EVB_IP>
    ```
6. Use below username/password to login OpenBMC home page, then navigate to the `KVM` page.
    ```
    Username: root
    Password: 0penBmc
    ```
    
    > _NOTE: You can use [Real VNC Viewer](https://www.realvnc.com/en/connect/download/viewer/) with below preferences instead of OpenBMC Web._


    ```
    /* Preference setting of Real VNC Viewer */
    Quality: Custom
    PreferredEncoding: Hextile
    ColorLevel: rgb565
    ```

7. Power up the motherboard and the video output will show on the WebUI (or Real VNC Viewer).

**Performance**

* Host OS: Windows Server 2016

|Playing video: [AQUAMAN](https://www.youtube.com/watch?v=2wcj6SrX4zw)|[Real VNC Viewer](https://www.realvnc.com/en/connect/download/viewer/) | WebUI (noVNC Viewer)
:-------------|:--------|:-----------|
Host Resolution | Average FPS | Average FPS|
1024 x 768      |  41         | 18         |
1280 x 1024     |  34         | 14         |
1600 x 1200     |  21         |      8     |


### Serial Over Lan
<img align="right" width="30%" src="https://user-images.githubusercontent.com/81551963/171312997-07b78ba5-269f-424a-b5bd-1f4db871c7c9.png">

The Serial over LAN (SOL) console redirects the output of the server’s serial port to WebUI.

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/console](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/console)

**How to use**

1. Prepare a motherboard (take Supermicro MBD-X9SCL-F-0 as example) and connect **pin 1-3, 5, 7-8, 10-12, 15-17 of JTPM** to **J10 header** of NPCM750 EVB with a LPC cable.

2. Download UEFI drivers and copy to the USB drive:

    * Format the USB drive in FAT or FAT32.  
    * Download **PolegSerialDxe.efi** and **TerminalDxe.efi** from  [https://github.com/Nuvoton-Israel/openbmc-uefi-util/tree/npcm7xx_v2.1/sol_binary](https://github.com/Nuvoton-Israel/openbmc-uefi-util/tree/npcm7xx_v2.1/sol_binary) and copy them to the USB drive.

3. Connect an ethernet cable between your workstation and **J12 header** of NPCM750 EVB.

4. Power up the NPCM750 EVB and configure IP address.

5. Configure Supermicro MBD-X9SCL-F-0 UEFI setting for SOL:
    * Power up Supermicro MBD-X9SCL-F-0 and boot into UEFI setting.  
    * Navigate to `Super IO Concifugration` in `Advanced` menu.  
    * Configure serial port 1 to **IO=3E8h; IRQ=5** then set to **disabled**.  
    * Navigate to `Boot` menu and select `UEFI: Built-in EFI Shell` as Boot Option #1.  
    * Navigate to `Exit` menu and select `Save changes and Reset`.  
    * When boot into UEFI shell, plug the USB drive (prepared in step-2).  
    * Type `exit` to route to UEFI shell again.
    * Type `fs0` and it will show **fs0:\>** from now.
    * Type `load PolegSerialDxe.efi` and `load TerminalDxe.efi` to load UEFI drivers.  
    * Unplug the USB drive and type `exit` to route to the UEFI setting.

6. Launch a browser in your workstation and enter URL `https://<NPCM750_EVB_IP>`, then navigate to the `SOL console` page. The output of serial port will show on the page.


### Virtual Media
<img align="right" width="20%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/3e65e7a/openbmc/vm_app_win.png">
<img align="right" width="30%" src="https://user-images.githubusercontent.com/81551963/171313708-13b8074f-5559-4f91-83d5-a61fb992077e.png">

Virtual Media (VM) is to emulate an USB drive on remote host PC via Network Block Device(NBD) and Mass Storage(MSTG).

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-phosphor/recipes-connectivity/jsnbd](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-phosphor/recipes-connectivity/jsnbd)

**How to use**

1. Clone a physical USB drive to an image file.
    * For Linux, use tool like **dd** command.
      ```
      dd if=/dev/sda of=usb.img bs=1M count=100
      ```
      > _**bs**: block size, **count**: block count._
      > 
      > _If the size of the USB drive is 1GB, then you could set "bs=1M" and "count=1024"_

    * For Windows, use tool like **Win32DiskImager.exe** to generate the image file.

2. Connect an ethernet cable between your workstation and **J12 header** of NPCM750 EVB. Then, power up the NPCM750 EVB and configure IP address.

3. Enable Virtual Media:

    * Through OpenBMC WebUI
        * Launch a browser and enter URL `https://<NPCM750_EVB_IP>`, then navigate to the `VM` page.
        * Click `Add File` to add image file, then click `Start` to start VM network service. You will see a new USB device on HOST OS.

    * Through VM standalone application
        * Download [application source code](https://github.com/Nuvoton-Israel/openbmc-util/tree/master/virtual_media_openbmc2.6).
        * Follow [instruction](https://github.com/Nuvoton-Israel/openbmc-util/blob/master/virtual_media_openbmc2.6/NBDServerWSWindows/README) to install QT and Openssl.
        * Start QT creator and open project **VirtualMedia.pro**, then build all of them.
        * Launch windows/linux application.
            > _NOTICE: use `sudo` to launch app in linux and install `nmap` first_
        *  Operations
            * Add image file and search On-Line NPCM750.
            * Click `Start VM` to start VM network service.
            * Click `Mount USB` to mount the image file on HOST OS.
            * If you want to stop this service, just click `UnMount USB` and `Stop VM`.


### BMC Firmware Update
<img align="right" width="30%" src="https://user-images.githubusercontent.com/81551963/171317350-a56b92e8-f71e-4697-a32b-cdd338723426.png">

This is a secure flash update mechanism to update BMC firmware via WebUI.

**Source URL**

* [https://github.com/openbmc/phosphor-bmc-code-mgmt](https://github.com/openbmc/phosphor-bmc-code-mgmt)
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/flash/phosphor-software-manager](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/flash/phosphor-software-manager)

**How to use**
1. Connect an ethernet cable between your workstation and **J12 header** of NPCM750 EVB. Then, power up the NPCM750 EVB and configure IP address.

2. Upload firmware image and start firmware update:
    * Through OpenBMC webUI
        * Launch a browser and enter URL `https://<NPCM750_EVB_IP>`, then navigate to the `Firmware` page.
        * Click `Add file` to add firmware image tar file.
        * Click `Start update` to start firmware update.
            > _NPCM750 EVB will reboot once firmware update completed._

    * Through Redfish API
        ```
        curl -k -H "X-Auth-Token: $token" -H "Content-Type: application/octet-stream" -X POST https://${NPCM750_EVB_IP}/redfish/v1/UpdateService -T {Path_of_Tar_File}
        ```
        >_${token} is the token value come from login API, refer to [here](https://github.com/openbmc/docs/blob/master/REST-cheatsheet.md) for more information._


## System

### Sensor
[phosphor-hwmon](https://github.com/openbmc/phosphor-hwmon) daemon will periodically check the sensor reading to see if it exceeds lower bound or upper bound. If alarm condition is hit, the [phosphor-sel-logger](https://github.com/openbmc/phosphor-sel-logger) will handle all events and add IPMI SEL records to the journal log, then [phosphor-host-ipmid](https://github.com/Nuvoton-Israel/phosphor-host-ipmid) will convert journal SEL records to IPMI SEL record format and reply to host.

**Source URL**
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/configuration](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/configuration)
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/ipmi](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/ipmi)
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/sensors](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/sensors)


**How to use**

* Add Sensor Configuration File:

    Each sensor has a [hwmon config file](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/sensors/phosphor-hwmon/obmc/hwmon/ahb/apb) and [ipmi sensor config file](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/configuration/evb-npcm750-yaml-config/evb-npcm750-ipmi-sensors.yaml) that defines the sensor name, thresholds and IPMI information.

    ```
      /* hwmon config file for LM75 temperature sensor on NPCM750 EVB. */
      LABEL_temp1=lm75
      WARNLO_temp1=10000
      WARNHI_temp1=40000
      CRITHI_temp1=70000
      CRITLO_temp1=0
      EVENT_temp1=WARNHI,WARNLO
    ```

    ```
      /* ipmi sensor config file for LM75 temperature sensor on NPCM750 EVB. */
      1: &temperature
      entityID: 0x07
      entityInstance: 1
      sensorType: 0x01
      path: /xyz/openbmc_project/sensors/temperature/lm75
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

* Monitor sensors and events:

  * Through OpenBMC WebUI  
    <img align="bottom" width="30%" src="https://user-images.githubusercontent.com/81551963/171321413-25f8eec8-2f7c-413f-9ae2-a29caab59f11.png">
    <img align="bottom" width="30%" src="https://user-images.githubusercontent.com/81551963/171321532-8e533e45-80ad-46e2-b29a-f01e98cb373d.png">

    Launch a browser and enter URL `https://<NPCM750_EVB_IP>`, then navigate to the `Sensors` or `Event logs` page. Reading data of sensors will show on the page.
    
  * Through IPMI command

    Use IPMI utilities like **ipmitool** to send commands for getting SDR or SEL records.  
    ```
    root@evb-npcm750:~# ipmitool sdr list
    lm75             | 37 degrees C    | ok
    tmp100           | 37 degrees C    | ok
    adc1             | 0 Volts         | ok
    adc2             | 0 Volts         | ok
    adc3             | 0 Volts         | ok
    adc4             | 0 Volts         | ok
    adc5             | 0 Volts         | ok
    adc6             | 0 Volts         | ok
    adc7             | 0 Volts         | ok
    adc8             | 0 Volts         | ok
    fan0             | 0 RPM           | ok
    fan1             | 0 RPM           | ok
    fan2             | 0 RPM           | ok
    fan3             | 0 RPM           | ok
    
    root@evb-npcm750:~# ipmitool sel list
    1 |  Pre-Init  |0000000089| Temperature #0x02 | Upper Non-critical going high | Asserted
    2 |  Pre-Init  |0000000247| Temperature #0x01 | Lower Non-critical going low  | Deasserted
    ```


### LED
<img align="right" width="30%" src="https://user-images.githubusercontent.com/81551963/171322034-a87212c4-6415-498e-b274-8d4926b472fd.png">  

Turn on system identify LED.

**Source URL**
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/leds](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/leds)

**How to use**
1. Add EnclosureIdentify in LED [config file](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/leds/evb-npcm750-led-manager-config/led.yaml).
    ```
	bmc_booted:
	    heartbeat:
		Action: 'Blink'
		DutyOn: 50
		Period: 1000
	power_on:
	    identify:
		Action: 'On'
		DutyOn: 50
		Period: 0
	EnclosureFault:
	    identify:
		Action: 'On'
		DutyOn: 50
		Period: 0
	EnclosureIdentify:
	    identify:
		Action: 'Blink'
		DutyOn: 10
		Period: 1000
    ```

2. Modify BSP layer [config](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/conf/machine/evb-npcm750.conf) to select npcm750 LED config file.
    ```
    PREFERRED_PROVIDER_virtual/phosphor-led-manager-config-native = "evb-npcm750-led-manager-config-native"
    ```

3. Launch a browser and enter URL `https://<NPCM750_EVB_IP>`, then navigate to the `Inventory and LEDs` page. Click `System identify LED` switch button, it will turn on the identify LED on NPCM750 EVB.


### ADC
NPCM750 contains an Analog-to-Digital Converter (ADC) interface that supports eight-channel inputs.    

**Source URL**
* [https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/sensors/phosphor-hwmon/obmc/hwmon/ahb/apb/adc@c000.conf](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/sensors/phosphor-hwmon/obmc/hwmon/ahb/apb/adc%40c000.conf)  

**How to use**  
 1. Prepare ADC configuration file (adc@c000.conf).
    ```
    LABEL_in1 = "adc1"
    LABEL_in2 = "adc2"
    LABEL_in3 = "adc3"
    LABEL_in4 = "adc4"
    LABEL_in5 = "adc5"
    LABEL_in6 = "adc6"
    LABEL_in7 = "adc7"
    LABEL_in8 = "adc8"
    ```  
    > _NOTE: For the LABEL assignment like `LABEL_in1 = "adc1"`, it must have corresponding hwmon sysfs file in `/sys/class/hwmon/hwmonN/in1_input`._

 2. Add ADC configuration file to rootfs in **phosphor-hwmon_%.bbappend**.
    ```
    FENVS = "obmc/hwmon/ahb/apb/{0}"
    ADC_ITEMS = "adc@c000.conf"
    SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'FENVS', 'ADC_ITEMS')}"
    ```
 
 3. Output 1.15v to **J25 header pin 1** (that is ADC channel3 input) on NPCM750 EVB.
 4. Launch a browser and enter URL `https://<NPCM750_EVB_IP>`, then navigate to the `Sensors` page. The ADC value will show on the page.


### Fan PID Control
<img align="right" width="30%" src="https://cdn.rawgit.com/NTC-CCBG/snapshots/e12e9dd/openbmc/fan_stepwise_pwm.png">

NPCM750 contains two PWM modules and supports eight PWM signals to control fans for dynamic adjustment according temperature variation.

**Source URL**

* [https://github.com/openbmc/phosphor-pid-control](https://github.com/openbmc/phosphor-pid-control)
* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/fans/phosphor-pid-control](https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/fans/phosphor-pid-control)

**How to use**  

In order to automatically apply accurate and responsive correction to a fan control function, we use the `swampd` (PID control daemon) to handle output PWM signal. To enable this daemon, we need to prepare the swampd configuration file and deploy a system service to start this daemon.

* The `swampd` configuration file (refer to [configure.md](https://github.com/openbmc/phosphor-pid-control/blob/master/configure.md) for more details)

    ```
    {
    "sensors" : [
           {
            "name": "fan0",
            "type": "fan",
            "readPath": "/xyz/openbmc_project/sensors/fan_tach/fan0",
            "writePath": "/sys/devices/platform/ahb/ahb:apb/f0103000.pwm-fan-controller/hwmon/**/pwm1",
            "min": 0,
            "max": 255
        },
        {
            "name": "lm75",
            "type": "temp",
            "readPath": "/xyz/openbmc_project/sensors/temperature/lm75",
            "writePath": "",
            "min": 0,
            "max": 0,
            "ignoreDbusMinMax": true,
            "timeout": 0
        }
    ],
    "zones" : [
        {
            "id": 0,
            "minThermalOutput": 0.0,
            "failsafePercent": 100.0,
            "pids": [
                {
                    "name": "Fan0",
                    "type": "fan",
                    "inputs": ["fan0"],
                    "setpoint": 40.0,
                    "pid": {
                        "samplePeriod": 1.0,
                        "proportionalCoeff": 0.0,
                        "integralCoeff": 0.0,
                        "feedFwdOffsetCoeff": 0.0,
                        "feedFwdGainCoeff": 1.0,
                        "integralLimit_min": 0.0,
                        "integralLimit_max": 0.0,
                        "outLim_min": 10.0,
                        "outLim_max": 100.0,
                        "slewNeg": 0.0,
                        "slewPos": 0.0
                    }
                },
                {
                    "name": "lm75",
                    "type": "stepwise",
                    "inputs": ["lm75"],
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
                            "1": 14,
                            "2": 21,
                            "3": 23,
                            "4": 25,
                            "5": 30,
                            "6": 33,
                            "7": 36,
                            "8": 40,
                            "9": 50,
                            "10": 60,
                            "11": 70,
                            "12": 80,
                            "13": 90,
                            "14": 95,
                            "15": 100
                        }
                    }
                }
            ]
        }
    ]
    }
    ```
* Deploy [phosphor-pid-control.service](https://github.com/Nuvoton-Israel/openbmc/blob/runbmc/meta-evb/meta-evb-nuvoton/meta-buv-runbmc/recipes-phosphor/fans/phosphor-pid-control/phosphor-pid-control.service) to start the `swampd` in **phosphor-pid-control_%.bbappend**.
    ```
    [Service]
    Type=simple
    ExecStart=/usr/bin/swampd
    Restart=always
    RestartSec=5
    StartLimitInterval=0
    ExecStopPost=/usr/bin/fan-default-speed.sh
    ```


### BIOS POST Code
NPCM750 supports a FIFO for monitoring BIOS POST Code.

**Source URL**

* [https://github.com/openbmc/phosphor-host-postd](https://github.com/openbmc/phosphor-host-postd)

**How to use**  
1. Prepare a motherboard (take Supermicro MBD-X9SCL-F-0 as example) and connect **pin 1-3, 5, 7-8, 10-12, 15-17 of JTPM** to **J10 header** of NPCM750 EVB with a LPC cable.

2. Execute `snooper` test program to record BIOS POST code from HOST port 0x80  
      ```
      root@evb-npcm750:~# snooper
      recv: 0x19
      recv: 0x15
      recv: 0x20
      recv: 0x20
      recv: 0x20
      recv: 0x23
      ```


### FRU
<img align="right" width="30%" src="https://user-images.githubusercontent.com/81551963/172313884-2b0cc4b1-9759-42be-abaf-53a419df03b7.png">

Field Replaceable Unit (FRU) information is used to provide “inventory” information about the board. In NPCM750, we connect EEPROM component as FRU Information Device to support this feature. Typically, this feature is used by the BMC to monitor host server health about H/W copmonents status.

**Source URL**

* [https://github.com/openbmc/ipmi-fru-parser](https://github.com/openbmc/ipmi-fru-parser)

* [https://github.com/Nuvoton-Israel/openbmc/tree/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/ipmi/phosphor-ipmi-fru_%.bbappend](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/ipmi/phosphor-ipmi-fru_%25.bbappend)
  
**How to use**  

* Prepare a Atmel 24C256A EEPROM component that stores FRU information, connect **SCL pin** to **J4 header pin 1** of NPCM750 EVB, **SDA pin** to **J4 header pin 2** of NPCM750 EVB, and the other pins connect to **GND**.

* In **nuvoton-npcm750-evb.dts** DTS, we have already added a EEPROM node to i2c bus 3.  

    ```
      i2c3: i2c@83000 {
          ...

      eeprom@50 {
          compatible = "atmel,24c256";
          pagesize = <16>;
          reg = <0x50>;
      };
    ```

* Define **SYSFS_PATH** and **FRUID** in [https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/ipmi/phosphor-ipmi-fru/obmc/eeproms/system/chassis/bmc](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/ipmi/phosphor-ipmi-fru/obmc/eeproms/system/chassis/bmc):

    ```
       SYSFS_PATH=/sys/bus/i2c/devices/3-0050/eeprom
       FRUID=1
    ```

    > _**SYSFS_PATH** is the sysfs path of i2c bus 3 and **FRUID** is an arbitrary number but need to match **Fruid** in [https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/configuration/evb-npcm750-yaml-config/evb-npcm750-ipmi-fru.yaml](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/configuration/evb-npcm750-yaml-config/evb-npcm750-ipmi-fru.yaml)._	 

* Launch a browser and enter URL `https://<NPCM750_EVB_IP>`, then navigate to the `Hardware status` page. The FRU information will show on the page.


### IPMI SOL
<img align="right" width="30%" src="https://user-images.githubusercontent.com/81551963/172339119-cbe1213b-0fba-4bd1-9dc9-7ad2b0d6dc99.png">

Use [ipmitool](https://github.com/ipmitool/ipmitool) or [ipmiutil](http://ipmiutil.sourceforge.net/) to interact with Serial over LAN (SoL) via IPMI, which redirects the output of the server’s serial port to a terminal window on your workstation.

**Source URL**

* [https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/ipmi/phosphor-ipmi-net_%.bbappend](https://github.com/Nuvoton-Israel/openbmc/blob/npcm-master/meta-evb/meta-evb-nuvoton/meta-evb-npcm750/recipes-phosphor/ipmi/phosphor-ipmi-net_%25.bbappend)

**How to use**

1. Follow `How to use` step 1~5 in [SOL](#serial-over-lan) to configure motherboard and your workstation.

2. Download [ipmitool](https://github.com/ipmitool/ipmitool) or [ipmiutil](http://ipmiutil.sourceforge.net/) tool.

3. Run SOL:

    ```
      ipmitool -I lanplus -C 17 -H <NPCM750_EVB_IP> -U root -P 0penBmc sol activate
    ```
   
    ```
      ipmiutil sol -N <NPCM750_EVB_IP> -U root -P 0penBmc -J 3 -V 4 -a
    ```      


# Image Size
Type          | Size    | Note                                                                                                     |
:-------------|:------- |:-------------------------------------------------------------------------------------------------------- |
image-uboot   |  694 KB | u-boot 2021.04 + bootblock for NPCM750 only                                                                       |
image-kernel  |  4.4 MB   | linux 5.10.67                                                                                      |
image-rofs    |  18.4 MB  | bottom layer of the overlayfs, read only                                                                 |
image-rwfs    |  0 MB  | middle layer of the overlayfs, rw files in this partition will be created at runtime,<br /> with a maximum capacity of 2MB|
